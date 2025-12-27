package com.example.fc_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FcAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(FcAuthApplication.class, args);
	}

}
