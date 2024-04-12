package com.example.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data // create Setter and Getter of declaration in the class below
@Configuration // define this class as a Configuration Bean
@ConfigurationProperties(prefix = "com.example.web") // add a prefix element of the declaration 
public class CustomProperties {
    private String apiUrl; // match with com.example.web.apiUrl in the application.properties

    private String tokenName; // match with com.example.web.tokenName in the application.properties

    private String token; // match with com.example.web.token in the application.properties
}