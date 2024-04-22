package com.example.api.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) classes for authentication-related requests and responses.
 */
public class AuthenticateDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequestDTO {
    
        @NotBlank(message = "username in mandatory")
        private String username;
    
        @NotBlank(message = "password in mandatory")
        private String password;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResetPasswordRequestDTO {
        @NotBlank(message = "token in mandatory")
        private String token;
    
        @NotBlank(message = "password in mandatory")
        private String password;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendUserCodeDTO {
        @NotBlank(message = "mail is mandatory")
        @Email
        private String mail;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenRequestDTO {
        @NotBlank(message = "token in mandatory")
        private String token;
    }
}

