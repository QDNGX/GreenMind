package ru.kolbasov_d_k.backend.config;

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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.HandlerMapping;
import ru.kolbasov_d_k.backend.services.MyUserDetailsService;


@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public SpringSecurity(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
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


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider(myUserDetailsService, passwordEncoder()));
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "index.html").permitAll()
                        .requestMatchers("/frontend/css/**", "/frontend/js/*", "frontend/img/index/**").permitAll()
                        .requestMatchers("/frontend/register.html", "/register").permitAll()
                        .requestMatchers("/api/session").permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/frontend/login.html")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout"))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
