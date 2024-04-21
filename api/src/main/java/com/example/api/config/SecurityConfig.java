package com.example.api.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.api.config.CustomHandler.CustomAccessDeniedHandler;
import com.example.api.config.CustomHandler.CustomLogoutHandler;
import com.example.api.filter.AuthentificationFilter;
import com.example.api.filter.JwtAuthentificationFilter;

import lombok.Data;

@Data
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  UserDetailsService userDetailsService;

  @Autowired
  JwtAuthentificationFilter jwtFilter;

  @Autowired
  AuthentificationFilter authFilter;

  @Autowired
  CustomAccessDeniedHandler customAccessDeniedHandler;

  @Autowired
  CustomLogoutHandler customLogoutHandler;

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

      // keep default authentification for http request
      http.httpBasic(Customizer.withDefaults());

      // allow to access all the page without define role
      http.authorizeHttpRequests(req -> 
        req.requestMatchers("/login/**", "/register/**").authenticated().
        requestMatchers("/error").permitAll()
        .anyRequest().hasAnyAuthority("USER", "ADMIN")
        
      );

      http.userDetailsService(userDetailsService);

      http.exceptionHandling(e -> e.accessDeniedHandler(customAccessDeniedHandler).authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));  

      // disable session creation because this server is a REST API
      http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

      // add authentificationFilter before the user password login page to authenticate with a token instead of username password
      http.addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class);
      http.addFilterAfter(authFilter, JwtAuthentificationFilter.class);

      http.logout(l -> l.logoutUrl("/logout")
      .addLogoutHandler(customLogoutHandler)
      .logoutSuccessHandler((req, res, auth) -> SecurityContextHolder.clearContext()));
      // build the filterChain
      return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authentificationManager(AuthenticationConfiguration configuration) throws Exception {
      return configuration.getAuthenticationManager();
    }
}