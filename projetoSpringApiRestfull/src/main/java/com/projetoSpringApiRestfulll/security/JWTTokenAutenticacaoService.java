package com.projetoSpringApiRestfulll.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.projetoSpringApiRestfull.ApplicationContextLoad;
import com.projetoSpringApiRestfull.model.Usuario;
import com.projetoSpringApiRestfull.repository.UsuarioRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	//Tempo de validade do token 
	private static final long EXPIRATION_TIME = 172800000;

	// uma senha unica para compor a autenticação
	private static final String SECRET = "*SenhaExtremamenteSecreta";
	
	//Prefixo padrão de token
	private static final String TOKEN_PREFIX = "Bearer";
	private static final String HEADER_STRING = "Authorization";
	
	//Gerando token de autenticação e adicionando ao cabeçalho e resposta http
	public void addAutehentication(HttpServletResponse response, String username) throws IOException {
		
		//Montagem do token
		String JWT = Jwts.builder() //chama o gerador de token
				.setSubject(username) // adiciona o usuario
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //pega o tempo real e adc o tempo de expiracao
				.signWith(SignatureAlgorithm.HS512,SECRET).compact(); //gera a string do token
		
		//junta token com o prefix
		String token = TOKEN_PREFIX + " " + JWT; // Bearer 
		
		//Adiciona no cabeçalho http
		response.addHeader(HEADER_STRING, token);
		
		//atualiza token no banco
		ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
			.atualizaTokenUser(JWT, username);
		
		liberacaoCors(response); //liberando resposta para portas diferentes que usam a api
		
		//Escreve token como resposta no corpo http
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
	}
	
	//Retorna o usuario validado com token ou caso nao seja valido retorna null
	public Authentication getAuthentication (HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//pega o token enviado no cabecalho http
		String token = request.getHeader(HEADER_STRING);
		
	 try {
			
		if(token != null) {
			
			String tokenClear = token.replace(TOKEN_PREFIX,"").trim(); // tira o prefixo
			
			// faz a validação do token do usuario na requisição
			String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(tokenClear) 
							.getBody().getSubject(); // descompacta e retorna o usuario
			
			if(user != null) {
				
				Usuario usuario = ApplicationContextLoad.getApplicationContext()//pega em memoria o metodo que consulta o usuario no banco
						.getBean(UsuarioRepository.class).findUserByLogin(user);
	
				//Retorna o usuario logado
				if(usuario != null) {
					
						if(tokenClear.equalsIgnoreCase(usuario.getToken())) {
					
							return new UsernamePasswordAuthenticationToken
									(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
					}
				}
				
			}
			
		}

		} catch (ExpiredJwtException e) {
			response.getOutputStream().println("Seu token está expirado! Faça login novamente");
			
		}
		
			liberacaoCors(response);
			return null; // nao autorizado
	}

	private void liberacaoCors(HttpServletResponse response) {
		
		if(response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
			
		}
		
		if(response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
}
	



