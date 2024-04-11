package com.example.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.web.model.Employee;
import com.example.web.repository.EmployeeProxy;

import lombok.Data;

/**
 * This service give access to the Employee Backend Api
 */
@Data // create the setter and the getter of each field below
@Service // define this class as a bean service
public class EmployeeService {

    @Autowired // auto connect to the bean component EmployeeProxy
    private EmployeeProxy employeeProxy;

    /**
     * get the corresponding employee of the ID
     * @param id - the id of the requested employee
     * @return - the employee or null
     */
    public Employee getEmployee(final long id) {
        return employeeProxy.getEmployee(id);
    }

    /**
     * get all the employee
     * @return - return an Iterable<Employee>
     */
    public Iterable<Employee> getEmployees() {
        return employeeProxy.getEmployees();
    }

    /**
     * delete the corresponding employee
     * @param id - the id of the employee that must be deleted
     */
    public void deleteEmployee(final long id) {
        employeeProxy.deleteEmployee(id);
    }

    /**
     * save the given employee
     * @param employee - the Employee object to be saved or update
     * @return - return the saved employee
     */
    public Employee saveEmployee(Employee employee) {
        // Règle de gestion : Le nom de famille doit être mis en majuscule.
        employee.setLastName(employee.getLastName().toUpperCase());

        return (employee.getId() == null) ? employeeProxy.createEmployee(employee) :  employeeProxy.updateEmployee(employee);
    }

}
