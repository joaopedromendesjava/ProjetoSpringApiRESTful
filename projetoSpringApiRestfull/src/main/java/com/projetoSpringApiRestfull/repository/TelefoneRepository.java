package com.projetoSpringApiRestfull.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.projetoSpringApiRestfull.model.Telefone;

@Repository
public interface TelefoneRepository extends CrudRepository<Telefone, Long> {

	
	
}
