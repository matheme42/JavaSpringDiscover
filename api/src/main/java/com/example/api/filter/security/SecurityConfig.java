package com.example.api.filter.security;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.Data;

@Data
@Configuration
@EnableWebSecurity
@ConfigurationProperties(prefix = "com.example.api.filter.security") // add a prefix element of the declaration
public class SecurityConfig {

    private String token;
    private String name;

    /**
     * The incoming request will pass throught this filter chain before the arriving to controllers
     * @param http - http security chain
     * @return - the http security chain
     * @throws Exception
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

      // disable cross platform access
      http.csrf(AbstractHttpConfigurer::disable);

      // allow to access all the page without define role
      http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry.requestMatchers("/**").authenticated());
      
      // keep default authentification for http request
      http.httpBasic(Customizer.withDefaults());

      // disable session creation because this server is a REST API
      http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

      // add authentificationFilter before the user password login page to authenticate with a token instead of username password
      AuthentificationFilter authentificationFilter = new AuthentificationFilter(token, name);
      http.addFilterBefore(authentificationFilter, UsernamePasswordAuthenticationFilter.class);

      // build the filterChain
      return http.build();
    }

}