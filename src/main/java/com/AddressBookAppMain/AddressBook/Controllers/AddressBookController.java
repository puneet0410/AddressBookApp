package com.AddressBookAppMain.AddressBook.Controllers;

import com.AddressBookAppMain.AddressBook.DTO.AddressBookDTO;
import com.AddressBookAppMain.AddressBook.Entity.AddressBook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/addressbook")
public class AddressBookController {

    private final List<AddressBook> contactList = new ArrayList<>();
    private int idCounter = 1;

    // GET all contacts
    @GetMapping
    public ResponseEntity<List<AddressBook>> getAllContacts() {
        return ResponseEntity.ok(contactList);
    }

    // GET contact by ID
    @GetMapping("/{id}")
    public ResponseEntity<AddressBook> getContactById(@PathVariable int id) {
        Optional<AddressBook> contact = contactList.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
        return contact.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Add a new contact
    @PostMapping
    public ResponseEntity<AddressBook> addContact(@RequestBody AddressBookDTO addressBookDTO) {
        AddressBook contact = new AddressBook();
        contact.setId(idCounter++);
        contact.setName(addressBookDTO.getName());
        contact.setPhone(addressBookDTO.getPhone());
        contact.setAddress(addressBookDTO.getAddress());

        contactList.add(contact);
        return ResponseEntity.ok(contact);
    }

    // PUT - Update a contact by ID
    @PutMapping("/{id}")
    public ResponseEntity<AddressBook> updateContact(@PathVariable int id, @RequestBody AddressBookDTO addressBookDTO) {
        Optional<AddressBook> contactOpt = contactList.stream()
                .filter(c -> c.getId() == id)
                .findFirst();

        if (contactOpt.isPresent()) {
            AddressBook contact = contactOpt.get();
            contact.setName(addressBookDTO.getName());
            contact.setPhone(addressBookDTO.getPhone());
            contact.setAddress(addressBookDTO.getAddress());

            return ResponseEntity.ok(contact);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE contact by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable int id) {
        boolean removed = contactList.removeIf(contact -> contact.getId() == id);
        if (removed) {
            return ResponseEntity.ok("Contact deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }
}