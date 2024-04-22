package com.example.api.filter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * This class represents the authentication token object.
 * It is used to authenticate the frontend using an API key.
 */
public class AuthentificationApiKey extends AbstractAuthenticationToken {
   
    /** The API key associated with this authentication token. */
    private final String apiKey;
    

    /**
     * Constructor for the AuthenticationApiKey object.
     *
     * @param apiKey the API key to associate with this authentication token
     * @param authenticated true if the authentication token is authenticated, false otherwise
     */
    public AuthentificationApiKey(String apiKey, Boolean authenticated) {
        super(AuthorityUtils.NO_AUTHORITIES);  // No default authorities
        this.apiKey = apiKey;
        setAuthenticated(authenticated);  // Set whether the token is authenticated or not
    }


    /**
     * Returns the credentials associated with this authentication token.
     *
     * @return the credentials (can be null)
     */
    @Override
    public Object getCredentials() {
        return null; // Implement if needed
    }

    /**
     * Returns the principal associated with this authentication token, which is the API key.
     *
     * @return the API key associated with this authentication token
     */    
    @Override
    public Object getPrincipal() {
        return apiKey;
    }
}