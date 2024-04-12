package com.example.web.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.web.CustomProperties;
import com.example.web.model.Employee;

import lombok.extern.slf4j.Slf4j;

/** EmployeeProxy is an interface to communicate with the backend
 * @apiNote
 *  getEmployees -  return the list of all the employee (Backend: GET /employees)
 *  getEmployee -   return employee if exist (Backend: GET /employee/id)
 *  createEmployee -    return the employee after sending to the DB (POST /employee)
 *  deleteEmployee -    delete the Employee from the DB (DELETE /employee/id)
 */
@Slf4j // allow access to the log Object
@Component // declare this class as a Component
public class EmployeeProxy {

    @Autowired // auto connect to the CustomProperties component (auto instanciate)
    private CustomProperties props;

    /**
    * Get all employees
    * @return An iterable of all employees
    */
    public Iterable<Employee> getEmployees() {
        String baseApiUrl = props.getApiUrl();
        String getEmployeesUrl = baseApiUrl + "/employees";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(props.getTokenName(), props.getToken());
        HttpEntity<Object> request = new HttpEntity<Object>(null, headers);
        ResponseEntity<Iterable<Employee>> response = restTemplate.exchange(
                getEmployeesUrl,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<Iterable<Employee>>() {}
                );

        log.debug("Get Employees call " + response.getStatusCode().toString());
        
        return response.getBody();
    }


	/**
	 * Get an employee by the id
	 * @param id The id of the employee
	 * @return The employee which matches the id
	 */
	public Employee getEmployee(long id) {
		String baseApiUrl = props.getApiUrl();
		String getEmployeeUrl = baseApiUrl + "/employee/" + id;

		RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(props.getTokenName(), props.getToken());
        HttpEntity<Object> request = new HttpEntity<Object>(null, headers);
		ResponseEntity<Employee> response = restTemplate.exchange(
				getEmployeeUrl, 
				HttpMethod.GET, 
				request,
				Employee.class
			);
		
		log.debug("Get Employee call " + response.getStatusCode().toString());
		
		return response.getBody();
	}
	

    /**
	 * Add a new employee 
	 * @param e A new employee (without an id)
	 * @return The employee full filled (with an id)
	 */
    public Employee createEmployee(Employee e) {
        String baseApiUrl = props.getApiUrl();
        String createEmployeeUrl = baseApiUrl + "/employee";
    
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(props.getTokenName(), props.getToken());
        HttpEntity<Employee> request = new HttpEntity<Employee>(e, headers);
        ResponseEntity<Employee> response = restTemplate.exchange(
            createEmployeeUrl,
            HttpMethod.POST,
            request,
            Employee.class);
    
        
        log.debug("Create Employee call " + response.getStatusCode().toString());
    
        return response.getBody();
    }


    /**
	 * Update an employee - using the PUT HTTP Method.
	 * @param e Existing employee to update
	 */
    public Employee updateEmployee(Employee e) {
        String baseApiUrl = props.getApiUrl();
        String createEmployeeUrl = baseApiUrl + "/employee/" + e.getId();
    
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(props.getTokenName(), props.getToken());
        HttpEntity<Employee> request = new HttpEntity<Employee>(e, headers);
        ResponseEntity<Employee> response = restTemplate.exchange(
            createEmployeeUrl,
            HttpMethod.PUT,
            request,
            Employee.class);
    
        
        log.debug("Update Employee call " + response.getStatusCode().toString());
    
        return response.getBody();
    }


    	
	/**
	 * Delete an employee using exchange method of RestTemplate
	 * instead of delete method in order to log the response status code.
	 * @param e The employee to delete
	 */
	public void deleteEmployee(long id) {
		String baseApiUrl = props.getApiUrl();
		String deleteEmployeeUrl = baseApiUrl + "/employee/" + id;
		
		RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(props.getTokenName(), props.getToken());
        HttpEntity<Object> request = new HttpEntity<Object>(null, headers);
		ResponseEntity<Void> response = restTemplate.exchange(
				deleteEmployeeUrl, 
				HttpMethod.DELETE, 
				request, 
				Void.class);
		
		log.debug("Delete Employee call " + response.getStatusCode().toString());
	}

}