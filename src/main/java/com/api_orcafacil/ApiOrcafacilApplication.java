package com.api_orcafacil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.api_orcafacil.config.DotenvLoader;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableMethodSecurity
@EnableTransactionManagement
public class ApiOrcafacilApplication {

	public static void main(String[] args) {

		DotenvLoader.init();

		SpringApplication.run(ApiOrcafacilApplication.class, args);
	}

}
