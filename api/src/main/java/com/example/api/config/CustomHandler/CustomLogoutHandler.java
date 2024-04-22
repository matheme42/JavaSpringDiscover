package com.example.api.config.CustomHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.example.api.model.database.Token;
import com.example.api.repository.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CustomLogoutHandler is responsible for handling user logout actions.
 * It implements the LogoutHandler interface to define the logout behavior.
 * 
 * @Component annotation indicates that CustomAccessDeniedHandler is a Spring-managed component. 
 */
@Component
public class CustomLogoutHandler implements LogoutHandler {

    /**
     * @Autowired annotation marks the TokenRepository for automatic dependency injection by Spring.
     */
    @Autowired
    TokenRepository tokenRepository;

    /**
     * Performs logout functionality by invalidating the token associated with the user's authentication.
     *
     * @param request         the HTTP servlet request
     * @param response        the HTTP servlet response
     * @param authentication  the authentication object representing the current user's authentication
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // search for the Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ;

        // extract the token value
        String token = authHeader.substring(7);

        // search the token in the database
        Token storedToken = tokenRepository.findByToken(token).orElse(null);

        // disable the token
        if (storedToken != null) {
            storedToken.setLoggedOut(true);
            tokenRepository.save(storedToken);
        }
    }
    
}
