package com.projetoSpringApiRestfull;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@EntityScan(basePackages = {"com.projetoSpringApiRestfull.model"}) // identificar todas classes de modelos que persiste no banco
@ComponentScan(basePackages = {"com.projeto*"}) // manipula todas clases e configura, trabalhar a injection depency 
@EnableJpaRepositories(basePackages = {"com.projetoSpringApiRestfull.repository"}) // aponta clases que faz transação com banco
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration
@EnableCaching
public class ProjetoSpringApiRestfullApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoSpringApiRestfullApplication.class, args);
		//System.out.println(new BCryptPasswordEncoder().encode("123"));
	
	}
	// mapeamento global
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/usuario/**")
					.allowedMethods("*")
						.allowedOrigins("*");
		
		registry.addMapping("/profissao/**")
		.allowedMethods("*")
			.allowedOrigins("*");
		
		registry.addMapping("/recuperar/**")
		.allowedMethods("*")
			.allowedOrigins("*");// liberando controler e todos os metodos http
	}
}