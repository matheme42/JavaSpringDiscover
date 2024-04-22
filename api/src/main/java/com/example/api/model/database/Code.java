package com.example.api.model.database;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.example.api.model.enums.Type;


/**
 * Code Entity
 * <p>
 * Represents a code entity in the database.
 */
@Entity
@Table(name = "code")
@Data
public class Code {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "token")
    @NotBlank(message = "token is mandatory")
    private String token;

    @Column(name = "is_valid")
    @NotNull(message = "is_valid must be set")
    private boolean isValid;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "type must be provide")
    Type type;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    @NotNull(message = "user must be provide")
    private User user;
}
