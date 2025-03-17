package com.AddressBookAppMain.AddressBook.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,  "/api/auth/register").permitAll() // Allow both login & register
                        .anyRequest().authenticated() // Secure all other endpoints
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth/**")) // Disable CSRF for auth endpoints
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // Allow H2 Console in frames

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}