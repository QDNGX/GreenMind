package ru.kolbasov_d_k.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for the GreenMind backend.
 * This class serves as the entry point for the Spring Boot application.
 * 
 * The @SpringBootApplication annotation enables auto-configuration, component scanning,
 * and defines this class as a configuration class.
 * 
 * The @EnableJpaAuditing annotation enables JPA auditing features, which are used
 * for automatically populating createdAt and updatedAt fields in entity classes.
 */
@EnableJpaAuditing
@SpringBootApplication
public class BackendApplication {

	/**
	 * Main method that starts the Spring Boot application.
	 * 
	 * @param args Command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
