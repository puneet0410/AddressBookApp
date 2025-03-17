package com.AddressBookAppMain.AddressBook.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a generic email.
     */
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // 'true' enables HTML content

        mailSender.send(message);
    }

    /**
     * Sends a registration confirmation email.
     */
    public void sendRegistrationEmail(String to) {
        String subject = "Welcome to AddressBook App!";
        String body = "<h1>Registration Successful!</h1><p>Thank you for signing up.</p>";

        try {
            sendEmail(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace(); // Log this properly in production
        }
    }

    /**
     * Sends a login alert email.
     */
    public void sendLoginAlertEmail(String to) {
        String subject = "Login Alert";
        String body = "<p>Your account was logged in successfully!</p>";

        try {
            sendEmail(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace(); // Log this properly in production
        }
    }
}