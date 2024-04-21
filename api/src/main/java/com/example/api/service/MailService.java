package com.example.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.api.config.SecretConfig;

@Service
public class MailService {
    
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SecretConfig secretConfig;

    public void sendSimpleMessage(String to, String subject, String text) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage(); 
        
        message.setFrom(secretConfig.getMAIL_USERNAME());
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        emailSender.send(message);
    }
}
