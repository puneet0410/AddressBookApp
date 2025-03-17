package com.AddressBookAppMain.AddressBook.Services;

import com.AddressBookAppMain.AddressBook.DTO.AddressBookDTO;
import com.AddressBookAppMain.AddressBook.Entity.AddressBook;
import com.AddressBookAppMain.AddressBook.Interfaces.IAddressBookService;
import com.AddressBookAppMain.AddressBook.Repository.AddressBookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AddressBookService implements IAddressBookService {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private RedisTemplate<String, AddressBook> redisTemplate;

    @Autowired
    private RedisTemplate<String, List<AddressBook>> redisListTemplate;

    private static final String REDIS_KEY_ALL_CONTACTS = "AddressBook:All";
    private static final String REDIS_KEY_CONTACT_PREFIX = "AddressBook:Contact:";

    @Override
    public List<AddressBook> getAllContacts() {
        log.info("Fetching all contacts...");

        ListOperations<String, List<AddressBook>> listOps = redisListTemplate.opsForList();
        List<AddressBook> cachedContacts = listOps.leftPop(REDIS_KEY_ALL_CONTACTS);

        if (cachedContacts != null && !cachedContacts.isEmpty()) {
            log.info("Returning contacts from Redis cache");
            return cachedContacts;
        }

        List<AddressBook> contacts = addressBookRepository.findAll();
        if (!contacts.isEmpty()) {
            listOps.rightPush(REDIS_KEY_ALL_CONTACTS, contacts);
            redisListTemplate.expire(REDIS_KEY_ALL_CONTACTS, 10, TimeUnit.MINUTES);
            log.info("Contacts stored in Redis cache");
        }

        return contacts;
    }

    @Override
    public Optional<AddressBook> getContactById(int id) {
        String redisKey = REDIS_KEY_CONTACT_PREFIX + id;
        log.info("Fetching contact with ID: {}", id);

        AddressBook cachedContact = redisTemplate.opsForValue().get(redisKey);
        if (cachedContact != null) {
            log.info("Returning contact {} from Redis cache", id);
            return Optional.of(cachedContact);
        }

        Optional<AddressBook> contact = addressBookRepository.findById(id);
        contact.ifPresent(value -> {
            redisTemplate.opsForValue().set(redisKey, value, Duration.ofMinutes(10));
            log.info("Contact {} stored in Redis cache", id);
        });

        return contact;
    }

    @Override
    public AddressBook addContact(AddressBookDTO addressBookDTO) {
        log.info("Adding new contact: {}", addressBookDTO);

        AddressBook contact = new AddressBook();
        contact.setName(addressBookDTO.getName());
        contact.setPhone(addressBookDTO.getPhone());
        contact.setAddress(addressBookDTO.getAddress());

        AddressBook savedContact = addressBookRepository.save(contact);

        redisTemplate.opsForValue().set(REDIS_KEY_CONTACT_PREFIX + savedContact.getId(), savedContact, Duration.ofMinutes(10));
        redisListTemplate.delete(REDIS_KEY_ALL_CONTACTS);

        log.info("Contact {} added & cached in Redis", savedContact.getId());
        return savedContact;
    }

    @Override
    public Optional<AddressBook> updateContact(int id, AddressBookDTO addressBookDTO) {
        log.info("Updating contact with ID: {}", id);

        return addressBookRepository.findById(id).map(contact -> {
            contact.setName(addressBookDTO.getName());
            contact.setPhone(addressBookDTO.getPhone());
            contact.setAddress(addressBookDTO.getAddress());

            AddressBook updatedContact = addressBookRepository.save(contact);

            redisTemplate.opsForValue().set(REDIS_KEY_CONTACT_PREFIX + updatedContact.getId(), updatedContact, Duration.ofMinutes(10));
            redisListTemplate.delete(REDIS_KEY_ALL_CONTACTS);

            log.info("Contact {} updated & cached in Redis", id);
            return updatedContact;
        });
    }

    @Override
    public boolean deleteContact(int id) {
        log.info("Deleting contact with ID: {}", id);

        if (addressBookRepository.existsById(id)) {
            addressBookRepository.deleteById(id);

            redisTemplate.delete(REDIS_KEY_CONTACT_PREFIX + id);
            redisListTemplate.delete(REDIS_KEY_ALL_CONTACTS);

            log.info("Contact {} deleted from DB and Redis", id);
            return true;
        }

        log.warn("Contact {} not found for deletion", id);
        return false;
    }
}