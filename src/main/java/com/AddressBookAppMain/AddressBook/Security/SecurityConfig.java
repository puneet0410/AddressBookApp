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
                        //  Permit access to authentication APIs
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login","/api/auth/login-with-token", "/api/auth/send-message").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/auth/forgotPassword/**", "/api/auth/resetPassword/**").permitAll()

                        //  Permit access to AddressBook APIs

                        .requestMatchers(HttpMethod.GET, "/addressbook/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/addressbook/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/addressbook/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/addressbook/**").permitAll()

                        //  Require authentication for all other requests
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()) //  Disable CSRF for APIs
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); //  Allow H2 Console

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}