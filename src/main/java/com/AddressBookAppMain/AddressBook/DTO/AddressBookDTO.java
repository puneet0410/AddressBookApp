package com.AddressBookAppMain.AddressBook.DTO;
import lombok.Data;

@Data  // Lombok automatically generates getters, setters, toString, etc.
public class AddressBookDTO {
    private String name;
    private String phone;
    private String address;
}