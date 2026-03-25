package ru.kolbasov_d_k.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class that enables JPA auditing features.
 *
 * Extracted from {@link ru.kolbasov_d_k.backend.BackendApplication} to avoid loading
 * JPA infrastructure during {@code @WebMvcTest} slice tests.
 *
 * Enables automatic population of {@code @CreatedDate} and {@code @LastModifiedDate}
 * fields in entity classes (e.g., User, Product).
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
