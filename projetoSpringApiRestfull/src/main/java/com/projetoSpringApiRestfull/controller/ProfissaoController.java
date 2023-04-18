package com.projetoSpringApiRestfull.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.projetoSpringApiRestfull.model.Profissao;
import com.projetoSpringApiRestfull.repository.ProfissaoRepository;

@RestController
@RequestMapping(value = "/profissao")
@CacheEvict(cacheNames = "cacheproferssall", allEntries = true)
@CachePut(cacheNames = "cacheprofersall") 
public class ProfissaoController {
	
	@Autowired
	private ProfissaoRepository profissaoRepository;
	
	
	@GetMapping(value = "/" , produces = "application/json")
	public ResponseEntity<List<Profissao>> profissoes(){
		
		List<Profissao> listProff = profissaoRepository.findAll();
		
		return new ResponseEntity<List<Profissao>>(listProff, HttpStatus.OK);
	}
}
