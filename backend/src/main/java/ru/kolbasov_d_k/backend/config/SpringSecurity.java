package ru.kolbasov_d_k.backend.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import ru.kolbasov_d_k.backend.services.MyUserDetailsService;

/**
 * Configuration class for Spring Security.
 * This class configures authentication, authorization, login/logout behavior,
 * and other security-related settings for the application.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurity {

    /**
     * Creates a password encoder bean for securely hashing passwords.
     * Uses BCrypt hashing algorithm for password storage.
     *
     * @return A BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final MyUserDetailsService myUserDetailsService;

    /**
     * Constructs a new SpringSecurity with the required user details service.
     *
     * @param myUserDetailsService The service used to load user-specific data
     */
    @Autowired
    public SpringSecurity(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    /**
     * Creates an authentication provider that uses the specified user details service and password encoder.
     * This provider is responsible for authenticating users against the database.
     *
     * @param userDetailsService The service used to load user-specific data
     * @param passwordEncoder The encoder used to verify passwords
     * @return A configured DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Creates a handler for successful authentication.
     * For AJAX requests (X-Requested-With: XMLHttpRequest), returns a JSON success response.
     * For regular form submissions, redirects the user to the requested page after successful login,
     * or to the profile page if no specific redirect target is provided.
     *
     * @return An AuthenticationSuccessHandler implementation
     */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String requestedWith = request.getHeader("X-Requested-With");
            
            // AJAX запрос - возвращаем JSON ответ
            if("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"message\":\"Вход выполнен успешно\"}");
                return;
            }
            
            // Обычный запрос - redirect как прежде
            String target = request.getParameter("redirect");

            if (target == null || target.isBlank()) {
                target = "/frontend/profile.html";
            }
            if (!target.startsWith("/")) {
                target = "/frontend/profile.html";
            }

            new DefaultRedirectStrategy()
                    .sendRedirect(request, response, target);
        };
    }

    /**
     * Creates a handler for authentication failures.
     * This handler provides appropriate responses for both AJAX requests and regular form submissions.
     * For AJAX requests (X-Requested-With: XMLHttpRequest), it returns a 401 status with a JSON error message.
     * For regular requests, it redirects to the login page with an error parameter.
     *
     * @return An AuthenticationFailureHandler implementation
     */
    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return (request, response, authentication) -> {
            String requestedWith = request.getHeader("X-Requested-With");
            if("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"error\":\"Неверный e-mail или пароль\"}");
            } else {
                response.sendRedirect("/frontend/login.html?error");
            }
        };
    }

    /**
     * Configures the security filter chain for the application.
     * This method sets up:
     * - The authentication provider
     * - URL-based authorization rules
     * - Form login configuration
     * - Logout behavior
     * - CSRF protection (disabled in this configuration)
     *
     * @param http The HttpSecurity to configure
     * @return A configured SecurityFilterChain
     * @throws Exception If an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider(myUserDetailsService, passwordEncoder()));
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "index.html").permitAll()
                        .requestMatchers("/frontend/css/**", "/frontend/js/**", "/frontend/img/index/**").permitAll()
                        .requestMatchers("/frontend/register.html", "/register").permitAll()
                        .requestMatchers("/api/session").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/frontend/login.html")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler())
                        .failureHandler(failureHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout"))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
