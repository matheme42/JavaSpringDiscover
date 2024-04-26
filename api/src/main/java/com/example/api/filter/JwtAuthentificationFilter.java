package com.example.api.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.api.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * JwtAuthenticationFilter
 * <p>
 * This filter intercepts incoming requests and checks for JWT authentication tokens in the Authorization header.
 * It validates the token, loads the user details, and sets the authentication in the SecurityContextHolder.
 */
@Component
public class JwtAuthentificationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Intercepts incoming requests and performs JWT authentication.
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

    // Extract the Authorization header which contains the JWT token
    String authHeader = request.getHeader("Authorization");
    // Check if the Authorization header is missing or does not start with "Bearer "
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        // If missing or invalid, proceed with the next filter in the chain
        filterChain.doFilter(request, response);
        return;
    }

    // Extract the token from the Authorization header
    String token = authHeader.substring(7);

    // Extract the username from the JWT token
    String username = jwtService.extractUsername(token);
        
        // Check if the username is extracted successfully and if no authentication is already set in the SecurityContextHolder
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Load user details associated with the username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // check the token validity
                Boolean isTokenValid = jwtService.isValid(token, userDetails);
                // Create an authentication token with the user details and authorities
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, isTokenValid ? userDetails.getAuthorities() : null);
                // Set the details of the authentication token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Proceed with the next filter in the chain
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                // If an exception occurs during authentication, set the HTTP status to UNAUTHORIZED
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
            return ;
        }
       
        // Proceed with the next filter
        filterChain.doFilter(request, response);
    }
    
}