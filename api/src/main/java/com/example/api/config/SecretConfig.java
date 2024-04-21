package com.example.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "com.example.api.secret") // add a prefix element of the declaration
public class SecretConfig {
    private String AUTHENTICATE_TOKEN_NAME;

    private String AUTHENTICATE_TOKEN;

    private String JWT_SECRET_KEY;


    private String MAIL_HOST;
    private int MAIL_PORT;

    private String MAIL_USERNAME;
    private String MAIL_PASSWORD;
}
