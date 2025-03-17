package com.AddressBookAppMain.AddressBook.Controllers;

import com.AddressBookAppMain.AddressBook.DTO.AddressBookDTO;
import com.AddressBookAppMain.AddressBook.Entity.AddressBook;
import com.AddressBookAppMain.AddressBook.Interfaces.IAddressBookService;
import com.AddressBookAppMain.AddressBook.Services.AddressBookService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/addressbook")
@Slf4j  // Lombok annotation for logging
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @GetMapping
    public ResponseEntity<List<AddressBook>> getAllContacts() {
        log.info("Received request to fetch all contacts");
        List<AddressBook> contacts = addressBookService.getAllContacts();
        log.debug("Returning {} contacts", contacts.size());
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressBook> getContactById(@PathVariable int id) {
        log.info("Received request to fetch contact with ID: {}", id);
        return addressBookService.getContactById(id)
                .map(contact -> {
                    log.debug("Returning contact: {}", contact);
                    return ResponseEntity.ok(contact);
                })
                .orElseGet(() -> {
                    log.error("Contact with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<?> addContact(@Valid @RequestBody AddressBookDTO addressBookDTO, BindingResult result) {
        log.info("Received request to add new contact: {}", addressBookDTO);

        // Handling validation errors
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            log.error("Validation failed: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        AddressBook contact = addressBookService.addContact(addressBookDTO);
        log.debug("Contact added successfully: {}", contact);
        return ResponseEntity.ok(contact);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContact(@PathVariable int id, @Valid @RequestBody AddressBookDTO addressBookDTO, BindingResult result) {
        log.info("Received request to update contact with ID: {}", id);

        // Handling validation errors
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            log.error("Validation failed: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        return addressBookService.updateContact(id, addressBookDTO)
                .map(updatedContact -> {
                    log.debug("Updated contact: {}", updatedContact);
                    return ResponseEntity.ok(updatedContact);
                })
                .orElseGet(() -> {
                    log.error("Failed to update. Contact with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable int id) {
        log.info("Received request to delete contact with ID: {}", id);
        if (addressBookService.deleteContact(id)) {
            log.info("Contact with ID {} deleted successfully", id);
            return ResponseEntity.ok("Contact deleted successfully.");
        }
        log.error("Contact with ID {} not found, cannot delete", id);
        return ResponseEntity.notFound().build();
    }
}