package com.example.api.model.database;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

/**
 * Snap Entity
 * <p>
 * Represents a code entity in the database.
 */
@Data
@Entity
@Table(name = "snap")
public class Snap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Long snap;

    @Column(name="image_url")
    private String imageUrl;

    private String location;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date")
    private Date createdDate;

    /** override the method toString() of the class
     * @return each field of the object seperate by a space
     */
    @Override
    public String toString() {
        return title + " " + description + " " + imageUrl + " " + location + " " + snap;
    }

}