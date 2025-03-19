package com.AddressBookAppMain.AddressBook.Services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    @RabbitListener(queues = "addressbook_queue")
    public void receiveMessage(String message) {
        System.out.println("📩 Received message: " + message);
    }
}