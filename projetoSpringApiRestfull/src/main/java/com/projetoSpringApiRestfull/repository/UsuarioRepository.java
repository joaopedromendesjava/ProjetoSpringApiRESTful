package com.projetoSpringApiRestfull.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.projetoSpringApiRestfull.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	

	@Query(value = "select u from Usuario u where u.login = :login")
	Usuario findUserByLogin(String login);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "update Usuario set token = :token where login = :login")
	void atualizaTokenUser(String token ,String login);
	
	@Transactional
	@Query(value = "select u from Usuario u where u.nome like %:nome%")
	List<Usuario> findUserByName(String nome);

	@Query(value = "SELECT constraint_name \r\n"
			+ "		from information_schema.constraint_column_usage \r\n"
			+ "		\r\n"
			+ "			WHERE table_name = 'usuarios_role' and column_name = 'role_id'\r\n"
			+ "			and constraint_name <> 'unique_role_user'", nativeQuery = true)
	String consultaConstraintRole();
	
	@Transactional
	@Modifying
	@Query(value = "INSERT INTO usuarios_role (usuario_id, role_id) VALUES(:id, "
			+ "(SELECT id FROM \"role\" where nome_role = 'ROLE_USER'));", nativeQuery = true)
	void insertPermissionsRoleUser(Long id);

	@Query(value = "SELECT * from Usuario where lower(nome) like lower(concat('%',:nome,'%'))",
			countQuery = "SELECT count(*) FROM Usuario where nome like %:nome%",
			nativeQuery = true)
	Page<Usuario> findUserByNamePage(@Param("nome")String nome, Pageable pageable);
	
	@Transactional
	@Modifying
	@Query(value = "update usuario set senha = :senha where id = :codUser", nativeQuery = true)
	void updateSenha(String senha, Long codUser);
		
	
}




