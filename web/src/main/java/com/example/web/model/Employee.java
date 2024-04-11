package com.example.web.model;

import lombok.Data;

@Data // create Setter and Getter for each declaration in the class
/** Employee
 * same field of the Employee model of the backend 
*/
public class Employee {

    private Integer id;

    private String firstName;

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