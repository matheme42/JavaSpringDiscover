package com.example.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

import lombok.Data;

/**
 * Configuration class for handling secret configuration properties.
 * It uses the @ConfigurationProperties annotation to bind properties with the 'secret' prefix from the application.properties file.
 * @Data Lombok annotation that automatically generates getters, setters, toString, equals, and hashCode methods for the class fields.
 * @Configuration Indicates that the class is a configuration class, providing bean definitions and other application configuration.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "secret") // add a prefix element of the declaration
public class SecretConfig {

    private String JWT_SECRET_KEY;
    private int JWT_ACCESS_TOKEN_EXPIRED;
    private int JWT_REFRESH_TOKEN_EXPIRED;

    /**
     * Represents field in the SecretConfig class that holds the email service configuration property.
     */
    private String MAIL_BODY_HOSTNAME;
    private String MAIL_HOST;
    private int MAIL_PORT;
    private String MAIL_USERNAME;
    private String MAIL_PASSWORD;

    /**
     * Creates a PropertySourcesPlaceholderConfigurer bean that resolves placeholders in bean definition property values.
     * It loads properties from the .env file located in the file system.
     *
     * @return the configured PropertySourcesPlaceholderConfigurer bean
    */
    @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new FileSystemResource(".env"));
        return configurer;
    }
}
