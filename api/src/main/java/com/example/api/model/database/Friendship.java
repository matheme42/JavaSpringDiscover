package com.example.api.model.database;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "friendship")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "messages"})
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

    @OneToMany(mappedBy = "friendship", fetch = FetchType.LAZY)
    private List<Message> messages;
}
