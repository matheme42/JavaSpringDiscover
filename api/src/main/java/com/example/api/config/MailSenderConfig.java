package com.example.api.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailSenderConfig {

    @Autowired
    SecretConfig secretConfig;

    @Bean
    JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(secretConfig.getMAIL_HOST());
        mailSender.setPort(secretConfig.getMAIL_PORT());
        
        mailSender.setUsername(secretConfig.getMAIL_USERNAME());
        mailSender.setPassword(secretConfig.getMAIL_PASSWORD());
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        
        return mailSender;
    }
}
