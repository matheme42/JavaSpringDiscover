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
    
    /**
     * DTO class for a login request.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequestDTO {
    
        @NotBlank(message = "username is mandatory")
        private String username;
    
        @NotBlank(message = "password is mandatory")
        private String password;
    }
    
    /**
     * DTO class for a reset password request.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResetPasswordRequestDTO {
        @NotBlank(message = "token is mandatory")
        private String token;
    
        @NotBlank(message = "password is mandatory")
        private String password;
    }
    
    /**
     * DTO class for sending a user code.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendUserCodeDTO {
        @NotBlank(message = "mail is mandatory")
        @Email
        private String mail;
    }
    
    /**
     * DTO class for a token request.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenRequestDTO {
        @NotBlank(message = "token is mandatory")
        private String token;
    }
}
