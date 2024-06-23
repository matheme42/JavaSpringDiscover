package com.example.api.model.database;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Token Entity
 * <p>
 * Represents a code entity in the database.
 */
@Entity
@Table(name = "token", indexes = {
    @Index(name = "idx_token", columnList = "token")
})
@Data
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "token", unique = true)
    @NotBlank(message = "token is mandatory")
    private String token;

    @Column(name = "is_logged_out")
    @NotNull(message = "is_logged_out must be set")
    private boolean loggedOut;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    @NotNull(message = "user must be provide")
    private User user;
}
