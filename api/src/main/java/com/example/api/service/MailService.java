package com.example.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    
    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage(); 
        
        message.setFrom("maxeltra6999@gmail.com");
        message.setTo("maxeltra69@gmail.com"); 
        message.setSubject("Validate your account"); 
        message.setText("wow");
        emailSender.send(message);
    }
}
