package com.example.api.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.example.api.config.SecretConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * AuthenticationFilter
 * <p>
 * This filter checks if the token is present and valid, and authorizes or denies the request accordingly.
 */
@Component
public class AuthentificationFilter extends GenericFilterBean {


    @Autowired
    SecretConfig secretConfig;
    
    /**
     * doFilter is called for each incoming request.
     * It checks if the token is valid and sets the security context with the AuthenticationApiKey created.
     *
     * @param request  the request to process
     * @param response the response to send
     * @param chain    the filter chain
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String apiKey = ((HttpServletRequest) request).getHeader(secretConfig.getAUTHENTICATE_TOKEN_NAME());

        // Create the authentication token
        Authentication authentication = new AuthentificationApiKey(apiKey, !(apiKey == null || !apiKey.equals(secretConfig.getAUTHENTICATE_TOKEN())));
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // Change the default user password authentication with a token authentication
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        }
        // Proceed with the next filter
        chain.doFilter(request, response);
    }
    
}
