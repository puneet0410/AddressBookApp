package com.AddressBookAppMain.AddressBook.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // No-argument constructor required by JPA
    public User() {
    }

    // Parameterized constructor for easy object creation
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}