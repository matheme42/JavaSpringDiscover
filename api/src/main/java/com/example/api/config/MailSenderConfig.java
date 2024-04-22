package com.example.api.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @Configuration class for configuring and creating a JavaMailSender bean.
 * It reads mail-related configuration properties from the SecretConfig bean and sets up the JavaMailSender accordingly.
 */
@Configuration
public class MailSenderConfig {

    /**
     * @Autowired annotation injects the SecretConfig bean into the MailSenderConfig class automatically.
    */
    @Autowired
    SecretConfig secretConfig;

    /**
     * Creates and configures a JavaMailSender bean using properties from SecretConfig bean.
     *
     * @return the configured JavaMailSender bean
     */
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
