package com.example.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.web.model.Employee;
import com.example.web.service.EmployeeService;

import jakarta.validation.Valid;

/** EmployeeController is a RestController / Received incoming request:
 * @apiNote contains:
 * GET /            - return an HTML Content (name: home)
 * GET /add         - return an HTML Content (name: add)
 * GET /{id}/edit   - return an HTML Content (name: edit)
 * 
 * POST /saveEmployee        - save or update an Employee object (connection to backend server)
 * POST /deleteEmployee/{id} - delete an Employee object (connection to backend server)
 */
@Controller // define this bean as a Controller
public class EmployeeController {

    @Autowired // auto connect to the EmployeeService (auto instanciate by spring)
    EmployeeService service;

    /**
     * send the HTML content of home (store in ressouces/templates/home)
     * @param model - Object that allow to granted access to data inside the HTML page
     * @return - the HTML page by his name
     */
    @GetMapping("/")
    public String home(Model model) {
        Iterable<Employee> listEmployee = service.getEmployees(); // get the list of the employee by calling the Backend
        model.addAttribute("employees", listEmployee); // add an element name employees that contains an Iterable<Employee> to the object model
        
        return "home"; // match with the html file name
    }

    /**
     * send the HTML content that corresponding to the form to create an employee
     * @param model - Object that allow to granted access to data inside the HTML page
     * @return - the HTML page by his name
     */
    @GetMapping("/add")
    public String saveEmployee(Model model) {

        Employee employee = new Employee(); // create an empty employee

        model.addAttribute("employee", employee); // add an element name employee that contains an Employee to the object model

        return "add"; // match with the html file name
    }

    /**
     * redirect to the HTML page that corresponding to the form to edit an employee
     * @param id - the id of the employee
     * @param model - Object that allow to granted access to data inside the HTML page
     * @return - the HTML page by his name
     */
    @GetMapping("/{id}/edit")
    public String editEmployee(@PathVariable("id") final long id, Model model) {
        Employee employee = service.getEmployee(id);
        model.addAttribute("employee", employee);
        return "edit"; // match with the html file name
    }

    /** 
     * routes that create a employee with the given attribute
     * @param employee - an employee corresponding to the form-data
     * @return - Redirection
     */
    @PostMapping("/saveEmployee")
    public ModelAndView saveEmployee(@ModelAttribute @Valid Employee employee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("employee", employee);        
        return new ModelAndView("/add"); // redirect to the page home
        }
        service.saveEmployee(employee);
        return new ModelAndView("redirect:/"); // redirect to the page home
    }

    /**
     * routes that delete a employee corresponding to the id parameter
     * @param id - the id of the employee
     * @return - Redirection
     */
    @GetMapping("/deleteEmployee/{id}")
    public ModelAndView deleteEmployee(@PathVariable("id") final long id) {
        service.deleteEmployee(id);
        return new ModelAndView("redirect:/"); // redirect to the page /
    }

}
