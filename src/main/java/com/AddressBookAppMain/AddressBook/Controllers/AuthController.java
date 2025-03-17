package com.AddressBookAppMain.AddressBook.Controllers;

import com.AddressBookAppMain.AddressBook.DTO.UserDTO;
import com.AddressBookAppMain.AddressBook.Services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO userDTO) {
        String response = authService.registerUser(userDTO);
        return ResponseEntity.ok(response);
    }
}