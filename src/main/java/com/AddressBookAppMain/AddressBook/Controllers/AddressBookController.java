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
@Slf4j
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;

    @GetMapping
    public ResponseEntity<List<AddressBook>> getAllContacts() {
        return ResponseEntity.ok(addressBookService.getAllContacts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressBook> getContactById(@PathVariable int id) {
        return addressBookService.getContactById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addContact(@Valid @RequestBody AddressBookDTO addressBookDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(addressBookService.addContact(addressBookDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContact(@PathVariable int id, @Valid @RequestBody AddressBookDTO addressBookDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        return addressBookService.updateContact(id, addressBookDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable int id) {
        return addressBookService.deleteContact(id)
                ? ResponseEntity.ok("Contact deleted successfully.")
                : ResponseEntity.notFound().build();
    }
}