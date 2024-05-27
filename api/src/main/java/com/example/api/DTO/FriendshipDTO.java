package com.example.api.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class FriendshipDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FriendshipInvitationDTO {
    
        @NotBlank(message = "username is mandatory")
        private String username;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class  ReplyFriendshipInvitationDTO {

        @NotNull(message = "response is mandatory")
        Boolean response;

        @NotBlank(message = "username is mandatory")
        private String username;   
    }
}
