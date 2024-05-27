package com.example.api.model.database;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "content")
    @NotBlank(message = "content must be not be blank")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date")
    @NotNull(message = "created_date must be set")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "user must be set")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friendship_id")
    private Friendship friendship;
}
