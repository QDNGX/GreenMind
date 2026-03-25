package ru.kolbasov_d_k.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Main application class for the GreenMind backend.
 * This class serves as the entry point for the Spring Boot application.
 *
 * The @SpringBootApplication annotation enables auto-configuration, component scanning,
 * and defines this class as a configuration class.
 *
 * JPA auditing is configured separately in {@link ru.kolbasov_d_k.backend.config.JpaAuditingConfig}
 * to avoid conflicts with @WebMvcTest slice tests.
 */
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
