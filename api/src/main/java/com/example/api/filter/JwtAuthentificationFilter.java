package com.example.api.filter;

import java.io.IOException;
import java.util.logging.Logger;

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
import jakarta.servlet.http.Cookie;
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
    static Logger logger = Logger.getLogger("JWT Authentification Filter");

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
    String token = null;

    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().compareTo("Identity") == 0) {
                token = cookie.getValue();
                break ;
            }
        }
    } else {
        String identity = request.getHeader("Authorization");
        if (identity != null) {
            token = identity.substring(7);
        } else {
            identity = request.getParameter("Authorization");
            if (identity != null) token = identity;
        }
    }

    logger.info("New Authetification: " + token);

    // Check if the Identity Cookies is missing
    if (token == null) {
        // If missing or invalid, proceed with the next filter in the chain
        filterChain.doFilter(request, response);
        return;
    }
    // Extract the username from the JWT token
    String username = jwtService.extractUsername(token);
    logger.info("New Authetification for: " + username);

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

                logger.info("Authetification result: " + isTokenValid + " as: " + userDetails.getAuthorities());
                // Proceed with the next filter in the chain
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                logger.info("Authetification result: false:" + e.toString());
                // If an exception occurs during authentication, set the HTTP status to UNAUTHORIZED
                if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    return ;
                }
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
            return ;
        }
       
        // Proceed with the next filter
        filterChain.doFilter(request, response);
    }
    
}