package com.example.api.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.model.Employee;
import com.example.api.service.EmployeeService;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * EmployeeController is a RestController / Received incoming request:
 * @apiNote this controller managed:
 *  POST /employee - Create an Employee
 *  GET /employees - get all available employee
 * 
 *  GET /employee/id - get the specified employee or null if doesn't exist
 *  PUT /employee/id - update the specified employee if exist
 *  DELETE /employee/id - delete the specified employee if exist
 * 
 * @service used EmployeeService
 */
@RestController
public class EmployeeController {

    @Autowired // connect to the bean instance of EmployeeService  (auto instanciate by Spring)
    private EmployeeService employeeService;

    /**
     * @param employee
     * @return - the employee after saving in the database
     */
    @PostMapping("/employee")
    public Employee createEmployees(@RequestBody Employee employee) {
        return employeeService.saveEmployee(employee);
    }


    /**
    * Read - Get all employees
    * @return - An Iterable object of Employee full filled
    */
    @GetMapping("/employees")
    public Iterable<Employee> getEmployees() {
        return employeeService.getEmployees();
    }

    /**
     * @param id The id of the employee
     * @return - the employee corresponding to the id if exist it exist in the database
     */
    @GetMapping("/employee/{id}")
    public Employee saveEmployees(@PathVariable("id") final Long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        return employee.isPresent() ? employee.get() : null;
    }

    /**
	 * Update - Update an existing employee
	 * @param id - The id of the employee to update
	 * @param employee - The employee object updated
	 * @return - the employee updated can be null
	 */
    @PutMapping("employee/{id}")
    public Employee putMethodName(@PathVariable("id") final Long id, @RequestBody Employee updatedEmployee) {

        Optional<Employee> e = employeeService.getEmployee(id);
        if (!e.isPresent()) return null;
        Employee employee = e.get();

        // update the firstName if exist
        String firstName = updatedEmployee.getFirstName();
        if (firstName != null) employee.setFirstName(firstName);

        // update the firstName if exist
        String lastName = updatedEmployee.getLastName();
        if (firstName != null) employee.setLastName(lastName);

        // update the firstName if exist
        String mail = updatedEmployee.getMail();
        if (firstName != null) employee.setMail(mail);

        // update the firstName if exist
        String password = updatedEmployee.getPassword();
        if (password != null) employee.setPassword(password);

        // return the updatedEmployee;
        return employee;
    }

    /**
     * @param id The id of the employee
     * delete the employee corresponding to the id
     */
    @DeleteMapping("/employee/{id}")
    public void deleteEmployees(@PathVariable("id") final long id) {
        employeeService.deleteEmployee(id);
    }
}