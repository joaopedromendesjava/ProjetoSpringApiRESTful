package com.projetoSpringApiRestfull.controller;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.projetoSpringApiRestfull.ObjectErro;
import com.projetoSpringApiRestfull.model.Usuario;
import com.projetoSpringApiRestfull.repository.UsuarioRepository;
import com.projetoSprirngApiRestfull.service.ServiceEnviaEmail;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaAcessController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceEnviaEmail serviceEnviaEmail;
	
	@ResponseBody
	@PostMapping(value = "/")
	public ResponseEntity<ObjectErro> recuperar(@RequestBody Usuario userlogin) throws MessagingException{
		
		ObjectErro objectErro = new ObjectErro();
		
		Usuario user = usuarioRepository.findUserByLogin(userlogin.getLogin());
		
		if(user == null) {
			
			objectErro.setCode("404");
			objectErro.setError("Usuario não encontrado");
			
		}else {
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova = dateFormat.format(Calendar.getInstance().getTime());
			
			String senhaCripto = new BCryptPasswordEncoder().encode(senhaNova);
			usuarioRepository.updateSenha(senhaCripto, user.getId());
			
			serviceEnviaEmail.enviarEmail("Recuperação de senha", user.getLogin(), "Sua nova senha é : " + senhaNova);
			
			objectErro.setCode("200");
			objectErro.setError("Acesso enviado por email");
		}
	
	
		return new ResponseEntity<ObjectErro>(objectErro, HttpStatus.OK);
	}

	
}
