package com.AddressBookAppMain.AddressBook.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data// Lombok generates getters, setters, toString, equals, and hashCode
@Table(name = "address_book")
public class AddressBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String phone;
    private String address;
}