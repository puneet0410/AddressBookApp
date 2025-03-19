package com.AddressBookAppMain.AddressBook.Services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private static final String QUEUE_NAME = "addressbook_queue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(QUEUE_NAME, message);
        System.out.println("✅ Sent message: " + message);
    }
}