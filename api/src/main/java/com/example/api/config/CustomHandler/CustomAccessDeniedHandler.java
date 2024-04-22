package com.example.api.config.CustomHandler;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CustomAccessDeniedHandler is responsible for handling access denied exceptions
 * and setting the HTTP status code to 403 (Forbidden).
 * 
 * @Component annotation indicates that CustomAccessDeniedHandler is a Spring-managed component. 
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles the access denied exception by setting the HTTP status code to 403 (Forbidden).
     *
     * @param request               the HTTP servlet request
     * @param response              the HTTP servlet response
     * @param accessDeniedException the access denied exception that occurred
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(403);
    }
    
}
