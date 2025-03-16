package com.AddressBookAppMain.AddressBook.DTO;

public class AddressBookDTO {
    private String name;
    private String phone;
    private String address;

    // Default Constructor
    public AddressBookDTO() {}

    // Parameterized Constructor
    public AddressBookDTO(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // toString Method
    @Override
    public String toString() {
        return "AddressBookDTO{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
