package com.example.api.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDTO {
    /**
     * DTO class for a reset password request.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EditUserDTO {
        @NotBlank(message = "username is mandatory")
        private String username;

        @NotBlank(message = "email is mandatory")
        @Email(message = "must be a valid email")
        private String email;

        private String image;

    }

    /**
     * DTO class for a reset password request.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EditUserPasswordDTO {
        @NotBlank(message = "oldPassword is mandatory")
        private String oldPassword;

        @NotBlank(message = "newPassword is mandatory")
        private String newPassword;
    }
}
