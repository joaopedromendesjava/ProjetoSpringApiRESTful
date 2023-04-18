package com.projetoSpringApiRestfulll.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.projetoSprirngApiRestfull.service.ImplementsUserDetailsService;

//Mapeia url, endereços, autority de user
@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity //Habilita o modo de segurança
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ImplementsUserDetailsService implementsUserDetailsService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {	//configura as solicitações de acesso HTTP
		
		// Ativando a proteção contra usuarios que nao estao validados por TOKEN
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		.disable()
		.authorizeRequests()
		.antMatchers("/login").permitAll()
		.antMatchers("/").permitAll()
		.antMatchers("/index","/recuperar/**").permitAll()
		.antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")// Mapeia url direcionada apos logout
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout")) //invalidando usuario após logout
		
		//Filtra as requisições de login para autenticação
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
		
		//Filtra demais requisições para verificar a presença do TOKEN JWT no HEADER HTTP
		.addFilterBefore(new JWTApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
		
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		//service que irá consultar o usuario no banco de dados
		auth.userDetailsService(implementsUserDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder()); //padrao de codificação de senha


	}
	  public void addCorsMappings(CorsRegistry registry) {
	        registry.addMapping("/**")
	            .allowedOrigins("http://localhost:4200/")
	            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT");
	    }

	

}
