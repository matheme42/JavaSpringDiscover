package com.example.api.model;

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

@Data // create the Setter and the Getter of all object declared in the class
@Entity // defined this bean as en Entity
@Table(name = "snap") // corresponding name of the table inside of the database
public class Snap {

    @Id // defined the field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // set the strategy of the primary key generation
    private Long id;

    private String title;

    private String description;

    private Long snap;

    @Column(name="image_url") // redefined the name of the field in the table as "last_name"
    private String imageUrl;

    private String location;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date") // redefined the name of the field in the table as "last_name"
    private Date createdDate;

    /** override the method toString() of the class
     * @return each field of the object seperate by a space
     */
    @Override
    public String toString() {
        return title + " " + description + " " + imageUrl + " " + location + " " + snap;
    }

}