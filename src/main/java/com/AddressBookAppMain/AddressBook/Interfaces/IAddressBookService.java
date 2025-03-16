package com.AddressBookAppMain.AddressBook.Interfaces;

import com.AddressBookAppMain.AddressBook.DTO.AddressBookDTO;
import com.AddressBookAppMain.AddressBook.Entity.AddressBook;

import java.util.List;
import java.util.Optional;

public interface IAddressBookService {
    List<AddressBook> getAllContacts();
    Optional<AddressBook> getContactById(int id);
    AddressBook addContact(AddressBookDTO addressBookDTO);
    Optional<AddressBook> updateContact(int id, AddressBookDTO addressBookDTO);
    boolean deleteContact(int id);
}
