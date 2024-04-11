package com.example.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.api.model.Employee;


/** EmployeeRepository (only Repository must be connected to the database)
 * provide de CRUD function to manage employees inside the database
 */
@Repository // define this bean as a repository bean
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

}
