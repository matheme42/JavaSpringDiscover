package com.example.api.model.database;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "friendship")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "is_accepted", nullable = true)
    private Boolean is_accepted;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "user must be set")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    @NotNull(message = "friend must be set")
    private User friend;
}
