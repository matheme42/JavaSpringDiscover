package com.example.api.model.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;



@Data // create the Setter and the Getter of all object declared in the class
@Entity // defined this bean as en Entity
@Table(name = "employees") // corresponding name of the table inside of the database
public class Employee {

    @Id // defined the field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // set the strategy of the primary key generation
    private Long id;

    @Column(name="first_name") // redefined the name of the field in the table as "first_name"
    private String firstName;

    @Column(name="last_name") // redefined the name of the field in the table as "last_name"
    private String lastName;

    private String mail;

    private String password;

    /** override the method toString() of the class
     * @return each field of the object seperate by a space
     */
    @Override
    public String toString() {
        return firstName + " " + lastName + " " + mail + " " + password;
    }

}