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

/** AuthentificationFilter
 * - check if the token is present and valid
 * - Authorize or not the request
 */
@Component
public class AuthentificationFilter extends GenericFilterBean {


    @Autowired
    SecretConfig secretConfig;
    
    /**
     * doFilter is call for each incoming request
     * check that the token is valid a set the security context with the AuthentificationApiKey created
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String apiKey = ((HttpServletRequest) request).getHeader(secretConfig.getAUTHENTICATE_TOKEN_NAME());

        // create the authentification token
        Authentication authentication = new AuthentificationApiKey(apiKey, !(apiKey == null || !apiKey.equals(secretConfig.getAUTHENTICATE_TOKEN())));
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // change the default user password authentification with a token authentification
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        }
        // create the new filter
        chain.doFilter(request, response);
    }
    
}
