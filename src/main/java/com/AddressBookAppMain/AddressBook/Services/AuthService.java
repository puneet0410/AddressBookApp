package com.AddressBookAppMain.AddressBook.Services;


import com.AddressBookAppMain.AddressBook.DTO.LoginRequestDTO;
import com.AddressBookAppMain.AddressBook.DTO.LoginResponseDTO;
import com.AddressBookAppMain.AddressBook.DTO.UserDTO;
import com.AddressBookAppMain.AddressBook.Entity.User;
import com.AddressBookAppMain.AddressBook.Repository.UserRepository;
import com.AddressBookAppMain.AddressBook.Security.JwtUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    private EmailService emailService;  // ✅ Add EmailService here

    // Register user
    public String registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return "Error: Email already registered!";
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);
        return "User registered successfully!";
    }

    // Login user and generate JWT token
    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password!");
        }

        String token = jwtUtils.generateToken(user.getEmail());
        return new LoginResponseDTO("Login successful!", token);
    }

    // Forgot Password
    public boolean forgotPassword(String email, String newPassword) throws MessagingException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // ✅ Send email notification
            String subject = "Password Changed Successfully";
            String message = "Hello " + user.getUsername() + ",\n\nYour password has been successfully changed.\n\nIf you did not request this, please contact support immediately.";

            emailService.sendEmail(user.getEmail(), subject, message);
            return true;
        }
        return false;
    }

    public boolean resetPassword(String email, String oldPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Verify old password
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new IllegalArgumentException("Old password is incorrect!");
            }

            // Hash and update new password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // ✅ Send email notification
            try {
                String subject = "Password Reset Successful";
                String message = "Hello " + user.getUsername() + ",\n\nYour password has been successfully reset.\n\nIf you did not request this, please contact support immediately.";

                emailService.sendEmail(user.getEmail(), subject, message);
            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
            }

            return true;
        }

        return false; // User not found
    }

}