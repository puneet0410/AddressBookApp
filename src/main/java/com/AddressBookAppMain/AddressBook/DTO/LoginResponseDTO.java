package com.AddressBookAppMain.AddressBook.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDTO {
    private String message;
    private String token;

    // ✅ Default constructor (required for Spring Boot serialization)
    public LoginResponseDTO() {
    }

    // ✅ Parameterized constructor (fixes your issue)
    public LoginResponseDTO(String message, String token) {
        this.message = message;
        this.token = token;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}