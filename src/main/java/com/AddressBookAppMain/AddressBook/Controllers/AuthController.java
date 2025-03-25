package com.AddressBookAppMain.AddressBook.Controllers;


import com.AddressBookAppMain.AddressBook.DTO.ForgotPasswordDTO;
import com.AddressBookAppMain.AddressBook.DTO.LoginRequestDTO;
import com.AddressBookAppMain.AddressBook.DTO.LoginResponseDTO;
import com.AddressBookAppMain.AddressBook.DTO.UserDTO;
import com.AddressBookAppMain.AddressBook.Services.AuthService;
import com.AddressBookAppMain.AddressBook.Services.EmailService;
import com.AddressBookAppMain.AddressBook.Services.RabbitMQProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Tag(name = "Authentication Controller", description = "Handles user authentication, login, and password management")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    public AuthController(AuthService authService, EmailService emailService) {
        this.authService = authService;
        this.emailService = emailService;
    }

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @Operation(summary = "Send a message to RabbitMQ", description = "Publishes a message to RabbitMQ for testing purposes.")
    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
        rabbitMQProducer.sendMessage(message);
        return ResponseEntity.ok("Message sent to RabbitMQ: " + message);
    }

    @Operation(summary = "Register a new user", description = "Registers a new user and sends a confirmation email.")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        log.info("Registering user: {}", userDTO.getUsername());
        String response = authService.registerUser(userDTO);
        emailService.sendRegistrationEmail(userDTO.getEmail());
        log.info("User registered successfully: {}", userDTO.getEmail());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "User login", description = "Authenticates the user and returns a JWT token.")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) throws MessagingException {
        log.info("Login request received for email: {}", loginRequestDTO.getEmail());
        LoginResponseDTO response = authService.loginUser(loginRequestDTO);
        log.debug("Generated JWT Token: {}", response.getToken());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login using JWT Token", description = "Allows user login using a valid JWT token.")
    @PostMapping("/login-with-token")
    public ResponseEntity<?> loginWithToken(@RequestHeader("Authorization") String token) {
        try {
            String parsedToken = token.replace("Bearer ", "");
            log.info("Token-based login attempt.");
            if (authService.loginWithToken(parsedToken)) {
                log.info("Login successful with token.");
                return ResponseEntity.ok("Login successful with token.");
            }
            log.warn("Unauthorized token login attempt.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        } catch (IllegalArgumentException | MessagingException e) {
            log.error("Token login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(summary = "Forgot password", description = "Allows users to reset their password if forgotten.")
    @PutMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) throws MessagingException {
        log.info("Processing forgot password request for email: {}", forgotPasswordDTO.getEmail());
        boolean isUpdated = authService.forgotPassword(forgotPasswordDTO.getEmail(), forgotPasswordDTO.getNewPassword());
        if (!isUpdated) {
            log.warn("Failed password reset attempt for email: {}", forgotPasswordDTO.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sorry! We cannot find the user email: " + forgotPasswordDTO.getEmail());
        }
        log.info("Password changed successfully for email: {}", forgotPasswordDTO.getEmail());
        return ResponseEntity.ok("Password has been changed successfully!");
    }

    @Operation(summary = "Reset password", description = "Allows authenticated users to change their password.")
    @PutMapping("/resetPassword/{email}")
    public ResponseEntity<String> resetPassword(@PathVariable String email, @RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        log.info("Processing password reset request for email: {}", email);
        try {
            boolean isUpdated = authService.resetPassword(email, oldPassword, newPassword);
            if (!isUpdated) {
                log.warn("Reset password failed: User email not found {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sorry! We cannot find the user email: " + email);
            }
            log.info("Password reset successfully for email: {}", email);
            return ResponseEntity.ok("Password has been reset successfully!");
        } catch (IllegalArgumentException e) {
            log.error("Password reset error for email {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

//
//spring.datasource.url=jdbc:mysql://localhost:3306/addressbook_db?serverTimezone=UTC
//spring.datasource.username=${DB_USERNAME}
//spring.datasource.password=${DB_PASSWORD}
//spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
//spring.jpa.hibernate.ddl-auto=update
//spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
//
//jwt.secret=${JWT_SECRET}
//jwt.expiration=3600000
//
//spring.mail.host=smtp.gmail.com
//spring.mail.port=587
//spring.mail.username=${MAIL_USERNAME}
//spring.mail.password=${MAIL_PASSWORD}
//spring.mail.properties.mail.smtp.auth=true
//spring.mail.properties.mail.smtp.starttls.enable=true
//
//springdoc.api-docs.enabled=true
//springdoc.swagger-ui.enabled=true
//
//server.servlet.context-path=/
//
//spring.redis.host=127.0.0.1
//spring.redis.port=6379
//spring.redis.password=  # Leave blank if no password is used
//spring.cache.type=redis
