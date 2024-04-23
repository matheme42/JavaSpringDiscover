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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Entity
@Table(name = "acl")
@Data
public class Acl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "topic")
    @NotBlank(message = "topic is mandatory")
    private String topic;

    @Column(name = "username")
    @NotBlank(message = "topic is mandatory")
    private String username;

    @Column(name = "rw")
    @Min(0)
    @Max(7)
    @NotNull(message = "rw is mandatory")
    private Integer rw;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    @NotNull(message = "user must be provide")
    private User user;
}
