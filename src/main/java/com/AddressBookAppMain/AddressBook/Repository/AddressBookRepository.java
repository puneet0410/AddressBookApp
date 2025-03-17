package com.AddressBookAppMain.AddressBook.Repository;

import com.AddressBookAppMain.AddressBook.Entity.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressBookRepository extends JpaRepository<AddressBook, Integer> {
}