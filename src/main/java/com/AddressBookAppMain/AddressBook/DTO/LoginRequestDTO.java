package com.AddressBookAppMain.AddressBook.DTO;

public class LoginRequestDTO {

    private String email;
    private String password;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    // Constructor with parameters
    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Default constructor
    public LoginRequestDTO() {
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}