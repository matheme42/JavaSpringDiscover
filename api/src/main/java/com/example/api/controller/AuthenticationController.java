package com.example.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.model.db.User;
import com.example.api.model.request.authentication.loginRequest;
import com.example.api.service.AuthenticationService;
import com.example.api.service.MailService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


class Input {

    @Min(1)
    @Max(10)
    private int numberBetweenOneAndTen;
  }

@RestController
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    MailService mailService;

    private ResponseEntity<HashMap<String, Object>>  handleBadRequest(BindingResult bindingResult) {
        List<String> result = new Vector<>();
        for (FieldError e : bindingResult.getFieldErrors()) {
            result.add("field: " + e.getField() + ", rejected value: " + e.getRejectedValue() + ", message: " + e.getDefaultMessage());
        }
        return new ResponseEntity<>(new HashMap<>() {{
            put("message" , "rejected");
            put("error" , result);
        }}, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/register")
    public ResponseEntity<HashMap<String, Object>> register(@RequestBody @Valid User user, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);
        mailService.sendSimpleMessage("null", "null", "");
        return authenticationService.register(user);
    }
 
    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> login(@RequestBody @Valid loginRequest request, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);
        return authenticationService.authenticate(request);
    }
    
}
