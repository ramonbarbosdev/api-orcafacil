package com.api_orcafacil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.api_orcafacil.config.DotenvLoader;

@SpringBootApplication
@EnableScheduling
public class ApiOrcafacilApplication {

    public static void main(String[] args) {
        DotenvLoader.init();
        SpringApplication.run(ApiOrcafacilApplication.class, args);
    }
}
