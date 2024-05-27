package com.example.api.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MessageDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NewMessageDTO {
    
        @NotBlank(message = "content is mandatory")
        private String content;
    }
}
