package com.projetoSprirngApiRestfull.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projetoSpringApiRestfull.model.Usuario;
import com.projetoSpringApiRestfull.repository.UsuarioRepository;

//por padrao ja busca usuario no banco 
@Service
public class ImplementsUserDetailsService implements UserDetailsService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		if(username == null ) {
			throw new UsernameNotFoundException("Usuario " + username + "não existe");
		}else {
			 
			return new User(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
		}
	}

	public void insereAcessoPadrao(Long id) {
			
		String constraint = usuarioRepository.consultaConstraintRole();
		
		if(constraint != null) {
			jdbcTemplate.execute("ALTER TABLE usuarios_role DROP CONSTRAINT " + constraint);
		}
		usuarioRepository.insertPermissionsRoleUser(id);
		
	}
}




