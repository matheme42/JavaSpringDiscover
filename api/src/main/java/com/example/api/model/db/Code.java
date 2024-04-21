package com.example.api.model.db;

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
import lombok.Data;
import com.example.api.model.enums.Type;

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
    @NotBlank(message = "isValid is mandatory")
    private boolean isValid;

    @Enumerated(value = EnumType.STRING)
    @NotBlank(message = "type is mandatory")
    Type type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotBlank(message = "user_id is mandatory")
    private User user;
}
