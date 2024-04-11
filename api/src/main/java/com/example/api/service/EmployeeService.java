package com.example.api.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.model.Employee;
import com.example.api.repository.EmployeeRepository;

import lombok.Data;

/** EmployeeService
 *  provide an interface used by the employee controller to access employee objects store in
 *  the database
 *  @apiNote contains:
 *  getEmployee - @return an Optional<Employee>     - get 1 employee if exist
 *  getEmployees - @return an Iterable<Employee>    - get all the employe store in the db
 *  deleteEmployee - @return void                   - delete 1 employee if exist
 *  saveEmployee - @return Employee                 - insert or update 1 employee
 */
@Data // create the Setter and the Getter of all object declared in the class
@Service // define this bean as a service bean
public class EmployeeService {

    @Autowired // connect to the bean instance of EmployeeRepository (auto instanciate by Spring)
    private EmployeeRepository employeeRepository;

    /** get 1 employee if exist
     * @param id
     * @return
     */
    public Optional<Employee> getEmployee(final Long id) {
        return employeeRepository.findById(id);
    }

    /** get all the employees
     * @return
     */
    public Iterable<Employee> getEmployees() {
        return employeeRepository.findAll();
    }


    /** insert or update 1 employee
     * @param employee
     * @return
     */
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    /** delete 1 employee if exist
     * @param id
     */
    public void deleteEmployee(final Long id) {
        employeeRepository.deleteById(id);
    }

}