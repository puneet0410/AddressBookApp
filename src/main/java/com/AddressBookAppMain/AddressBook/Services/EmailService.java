package com.AddressBookAppMain.AddressBook.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j  // ✅ Lombok annotation for SLF4J logging
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a generic email.
     */
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        log.info("Preparing to send email to: {}", to);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // 'true' enables HTML content

        mailSender.send(message);

        log.info("Email sent successfully to: {}", to);
    }

    /**
     * Sends a registration confirmation email.
     */
    public void sendRegistrationEmail(String to) {
        String subject = "Welcome to AddressBook App!";
        String body = "<h1>Registration Successful!</h1><p>Thank you for signing up.</p>";

        try {
            log.info("Sending registration email to: {}", to);
            sendEmail(to, subject, body);
            log.info("Registration email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send registration email to {}: {}", to, e.getMessage());
        }
    }
}