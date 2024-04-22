package com.example.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.api.config.SecretConfig;

/**
 * MailService Class
 * <p>
 * Service class for sending emails.
 */
@Service
public class MailService {
    
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SecretConfig secretConfig;

    /**
     * Sends a simple email message.
     *
     * @param to      the recipient's email address
     * @param subject the subject of the email
     * @param text    the content of the email
     * @throws MailException if an error occurs while sending the email
     */    
    public void sendSimpleMessage(String to, String subject, String text) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage(); 
        
        message.setFrom(secretConfig.getMAIL_USERNAME());
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        emailSender.send(message);
    }
}
