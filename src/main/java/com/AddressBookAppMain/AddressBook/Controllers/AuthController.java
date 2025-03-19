package com.AddressBookAppMain.AddressBook.Controllers;

import com.AddressBookAppMain.AddressBook.DTO.ForgotPasswordDTO;
import com.AddressBookAppMain.AddressBook.DTO.LoginRequestDTO;
import com.AddressBookAppMain.AddressBook.DTO.LoginResponseDTO;
import com.AddressBookAppMain.AddressBook.DTO.UserDTO;
import com.AddressBookAppMain.AddressBook.Services.AuthService;
import com.AddressBookAppMain.AddressBook.Services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    @Autowired
    private EmailService emailService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        System.out.println("Registering user: " + userDTO.getUsername()); // Debug log
        String response = authService.registerUser(userDTO);
        emailService.sendRegistrationEmail(userDTO.getEmail());

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) throws MessagingException {
        System.out.println("Login request received: " + loginRequestDTO.getEmail());

        LoginResponseDTO response = authService.loginUser(loginRequestDTO);

        System.out.println("Login response: " + response); // Debugging
//        emailService.sendLoginAlertEmail(loginRequestDTO.getEmail());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/login-with-token")
    public ResponseEntity<?> loginWithToken(@RequestHeader("Authorization") String token) {
        try {
            if (authService.loginWithToken(token.replace("Bearer ", ""))) {
                return ResponseEntity.ok("Login successful with token.");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        } catch (IllegalArgumentException | MessagingException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PutMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) throws MessagingException {
        boolean isUpdated = authService.forgotPassword(forgotPasswordDTO.getEmail(), forgotPasswordDTO.getNewPassword());
        if (!isUpdated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sorry! We cannot find the user email: " + forgotPasswordDTO.getEmail());
        }
        return ResponseEntity.ok("Password has been changed successfully!");
    }

    @PutMapping("/resetPassword/{email}")
    public ResponseEntity<String> resetPassword(@PathVariable String email, @RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        try {
            boolean isUpdated = authService.resetPassword(email, oldPassword, newPassword);
            if (!isUpdated) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sorry! We cannot find the user email: " + email);
            }
            return ResponseEntity.ok("Password has been reset successfully!");
        } catch (IllegalArgumentException e) {
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
