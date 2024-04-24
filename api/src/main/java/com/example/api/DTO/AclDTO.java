package com.example.api.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for ACL-related operations.
 */
public class AclDTO {

    /**
     * DTO class for creating an ACL.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateAclDTO {
    
        @NotBlank(message = "topic is mandatory")
        private String topic;
    

        /**
         * Access level for the topic.
         * - 0: no access
         * - 1: read
         * - 2: write
         * - 3: read and write
         * - 4: subscribe
         * - 5: read & subscribe
         * - 6: write & subscribe
         * - 7: read, write and subscribe
         */
        @Min(0)
        @Max(7)
        @NotNull(message = "rw is mandatory")
        private Integer rw;
    }
       
}