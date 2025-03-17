package com.AddressBookAppMain.AddressBook.Services;

import com.AddressBookAppMain.AddressBook.DTO.AddressBookDTO;
import com.AddressBookAppMain.AddressBook.Entity.AddressBook;
import com.AddressBookAppMain.AddressBook.Exception.AddressBookException;
import com.AddressBookAppMain.AddressBook.Interfaces.IAddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@Slf4j  // Lombok annotation for logging
public class AddressBookService {

    private final List<AddressBook> contactList = new ArrayList<>();
    private int idCounter = 1;

    public List<AddressBook> getAllContacts() {
        log.info("Fetching all contacts");
        return contactList;
    }


    public Optional<AddressBook> getContactById(int id) {
        return Optional.ofNullable(contactList.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new AddressBookException("Employee with" + id + " not found")));
    }

    public AddressBook addContact(AddressBookDTO addressBookDTO) {
        log.info("Adding new contact: {}", addressBookDTO);
        AddressBook contact = new AddressBook();
        contact.setId(idCounter++);
        contact.setName(addressBookDTO.getName());
        contact.setPhone(addressBookDTO.getPhone());
        contact.setAddress(addressBookDTO.getAddress());

        contactList.add(contact);
        log.debug("Contact added successfully: {}", contact);
        return contact;
    }

    public Optional<AddressBook> updateContact(int id, AddressBookDTO addressBookDTO) {
        log.info("Updating contact with ID: {}", id);
        Optional<AddressBook> contactOpt = getContactById(id);

        contactOpt.ifPresent(contact -> {
            contact.setName(addressBookDTO.getName());
            contact.setPhone(addressBookDTO.getPhone());
            contact.setAddress(addressBookDTO.getAddress());
            log.debug("Updated contact: {}", contact);
        });

        return contactOpt;
    }

    public boolean deleteContact(int id) {
        log.warn("Deleting contact with ID: {}", id);
        boolean removed = contactList.removeIf(contact -> contact.getId() == id);
        if (removed) {
            log.info("Contact with ID {} deleted successfully", id);
        } else {
            log.error("Contact with ID {} not found", id);
        }
        return removed;
    }
}