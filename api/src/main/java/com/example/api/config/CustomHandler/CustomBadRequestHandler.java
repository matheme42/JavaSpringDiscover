package com.example.api.config.CustomHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class CustomBadRequestHandler {

    protected ResponseEntity<HashMap<String, Object>> badRequestErrorJsonResponse(Object field) {
        return new ResponseEntity<>(new HashMap<>() {{put("error", field);}}, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles bad request responses for validation errors.
     *
     * @param bindingResult the BindingResult containing validation errors
     * @return a ResponseEntity containing the error details
     */
    protected ResponseEntity<HashMap<String, Object>> handleBadRequest(BindingResult bindingResult) {
        List<String> result = new Vector<>();
        for (FieldError e : bindingResult.getFieldErrors()) {
            result.add("field: " + e.getField() + ", rejected value: " + e.getRejectedValue() + ", message: " + e.getDefaultMessage());
        }
        return badRequestErrorJsonResponse(result);
    }
}
