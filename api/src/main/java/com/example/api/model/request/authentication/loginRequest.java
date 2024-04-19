package com.example.api.model.request.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class loginRequest {

    @NotBlank(message = "username in mandatory")
    private String username;

    @NotBlank(message = "password in mandatory")
    private String password;
}
