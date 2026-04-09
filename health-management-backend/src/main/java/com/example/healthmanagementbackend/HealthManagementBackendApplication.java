package com.example.healthmanagementbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.healthmanagementbackend")
@EnableScheduling
public class HealthManagementBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthManagementBackendApplication.class, args);
	}

}
