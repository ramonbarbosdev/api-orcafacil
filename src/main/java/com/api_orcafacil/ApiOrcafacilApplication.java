package com.api_orcafacil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.api_orcafacil.config.DotenvLoader;

@SpringBootApplication
@ComponentScan(basePackages = { "com.*" })
@EntityScan(basePackages = { "com.api_orcafacil.*" })
@EnableJpaRepositories(basePackages = { "com.api_orcafacil.*" })
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration
@EnableCaching
@EnableScheduling
@EnableMethodSecurity
public class ApiOrcafacilApplication {

	public static void main(String[] args) {

		DotenvLoader.init();

		SpringApplication.run(ApiOrcafacilApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("ramon"));
	

	}

}
