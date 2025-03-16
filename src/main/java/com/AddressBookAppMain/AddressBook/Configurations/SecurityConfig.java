package com.AddressBookAppMain.AddressBook.Configurations;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Updated CSRF disable syntax
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/addressbook/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }}