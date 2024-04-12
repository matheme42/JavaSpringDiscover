package com.example.api.filter.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/** AuthentificationFilter
 * - check if the token is present and valid
 * - Authorize or not the request
 */
public class AuthentificationFilter extends GenericFilterBean {

    // the token value
    private String token;

    // the token name
    private String name;

    AuthentificationFilter(String token, String name) {
        this.token = token;
        this.name = name;
    }
    
    /**
     * doFilter is call for each incoming request
     * check that the token is valid a set the security context with the AuthentificationApiKey created
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String apiKey = ((HttpServletRequest) request).getHeader(name);

        // create the authentification token
        Authentication authentication = new AuthentificationApiKey(apiKey, !(apiKey == null || !apiKey.equals(token)));
        
        // change the default user password authentification with a token authentification
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create the new filter
        chain.doFilter(request, response);
    }
    
}
