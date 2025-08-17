package com.example.sulmuro_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SulmuroAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SulmuroAppApplication.class, args);
	}

}
