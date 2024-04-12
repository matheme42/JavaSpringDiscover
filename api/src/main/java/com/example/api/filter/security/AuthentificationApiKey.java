package com.example.api.filter.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * This class represent the Authentification token object
 */
public class AuthentificationApiKey extends AbstractAuthenticationToken {
    private final String apiKey;
    

    public AuthentificationApiKey(String apiKey, Boolean authenticated) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.apiKey = apiKey;
        setAuthenticated(authenticated);
    }
    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return apiKey;
    }
}