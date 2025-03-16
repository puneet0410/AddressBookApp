package com.AddressBookAppMain.AddressBook.Services;

import com.AddressBookAppMain.AddressBook.DTO.AddressBookDTO;
import com.AddressBookAppMain.AddressBook.Entity.AddressBook;
import com.AddressBookAppMain.AddressBook.Interfaces.IAddressBookService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AddressBookService implements IAddressBookService {

    private final List<AddressBook> contactList = new ArrayList<>();
    private int idCounter = 1;

    public List<AddressBook> getAllContacts() {
        return contactList;
    }

    public Optional<AddressBook> getContactById(int id) {
        return contactList.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }

    public AddressBook addContact(AddressBookDTO addressBookDTO) {
        AddressBook contact = new AddressBook();
        contact.setId(idCounter++);
        contact.setName(addressBookDTO.getName());
        contact.setPhone(addressBookDTO.getPhone());
        contact.setAddress(addressBookDTO.getAddress());

        contactList.add(contact);
        return contact;
    }

    public Optional<AddressBook> updateContact(int id, AddressBookDTO addressBookDTO) {
        Optional<AddressBook> contactOpt = getContactById(id);

        contactOpt.ifPresent(contact -> {
            contact.setName(addressBookDTO.getName());
            contact.setPhone(addressBookDTO.getPhone());
            contact.setAddress(addressBookDTO.getAddress());
        });

        return contactOpt;
    }

    public boolean deleteContact(int id) {
        return contactList.removeIf(contact -> contact.getId() == id);
    }
}