package com.example.api.model.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


/**
 * Employee Entity
 * <p>
 * Represents a code entity in the database.
 */
@Data 
@Entity 
@Table(name = "employees")
public class Employee {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    private String mail;

    private String password;

    @Override
    public String toString() {
        return firstName + " " + lastName + " " + mail + " " + password;
    }

}