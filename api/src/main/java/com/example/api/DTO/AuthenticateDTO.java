package com.example.api.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class AuthenticateDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class LoginRequestDTO {
    
        @NotBlank(message = "username in mandatory")
        private String username;
    
        @NotBlank(message = "password in mandatory")
        private String password;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ResetPasswordRequestDTO {
        @NotBlank(message = "token in mandatory")
        private String token;
    
        @NotBlank(message = "password in mandatory")
        private String password;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class SendUserCodeDTO {
        @NotBlank(message = "mail is mandatory")
        @Email
        private String mail;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class TokenRequestDTO {
        @NotBlank(message = "token in mandatory")
        private String token;
    }
}

