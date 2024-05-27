package com.example.api.config;


import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.example.api.config.CustomHandler.CustomAccessDeniedHandler;
import com.example.api.config.CustomHandler.CustomLogoutHandler;
import com.example.api.filter.JwtAuthentificationFilter;

/**
 * Configuration class for defining security configurations for the application.
 * @Data Lombok annotation that automatically generates getters, setters, toString, equals, and hashCode methods for the class fields.
 * @Configuration Indicates that the class is a configuration class, providing bean definitions and other application configuration.
   @EnableWebSecurity Enables Spring Security's web security features in the application.
*/
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  UserDetailsService userDetailsService;

  @Autowired
  JwtAuthentificationFilter jwtFilter;

  @Autowired
  CustomAccessDeniedHandler customAccessDeniedHandler;

  @Autowired
  CustomLogoutHandler customLogoutHandler;

    /**
     * Defines the security filter chain for handling incoming requests.
     *
     * @param http the HttpSecurity object to configure security settings
     * @return the configured security filter chain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

      /**
       * Disables CSRF protection in the HTTP security configuration. (Cross Platform)
       */
      http.csrf(AbstractHttpConfigurer::disable);

      /**
       * Configures HTTP Basic authentication with default settings in the HTTP security configuration.
       */
      http.httpBasic(Customizer.withDefaults());

      /**
       * Configures request authorization rules in the HTTP security configuration.
       * - Requests to specific endpoints are authenticated.
       * - Requests to "/error" endpoint are permitted without authentication.
       * - All other requests require either "USER" or "ADMIN" authority.
       */

      http.authorizeHttpRequests(req -> 
        req.requestMatchers("/login/**", "/register/**", "/validate_accout/**", "/validate/**", "/forget_password/**", "/reset_password/**", "/refresh/**", "/error").permitAll().
        requestMatchers("/**").hasAnyAuthority("USER", "ADMIN")
      );

      /**
       * Sets the UserDetailsService to be used for authentication in the HTTP security configuration.
       */
      http.userDetailsService(userDetailsService);

      /**
       * Configures exception handling in the HTTP security configuration.
       * - Sets a custom access denied handler and sets an HTTP status entry point for unauthorized access.
       */
      http.exceptionHandling(e -> e.accessDeniedHandler(customAccessDeniedHandler).authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));  

      /**
       * Configures session management in the HTTP security configuration.
       * - Sets the session creation policy to STATELESS to indicate that no HTTP session should be created.
       */
      http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

      /**
       * Adds filters to the HTTP security configuration after the specified filters.
       * - Adds JwtAuthentificationFilter after UsernamePasswordAuthenticationFilter.
       * - Adds AuthentificationFilter after JwtAuthentificationFilter.
       */
      http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    /**
     * Configures logout behavior in the HTTP security configuration.
     * - Specifies the logout URL ("/logout").
     * - Adds a custom logout handler.
     * - Sets a custom logout success handler to clear the security context.
     */
      http.logout(l -> l.logoutUrl("/logout")
      .addLogoutHandler(customLogoutHandler)
      .logoutSuccessHandler((req, res, auth) -> SecurityContextHolder.clearContext()));

    /**
     * Builds the HTTP security configuration and returns the configured HttpSecurity object.
     */
      return http.build();
    }

    /**
     * Creates a password encoder bean for encoding passwords.
     *
     * @return the password encoder bean
    */
    @Bean
    PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }

    /**
     * Creates an AuthenticationManager bean.
     *
     * @param configuration the AuthenticationConfiguration object
     * @return the AuthenticationManager bean
     * @throws Exception if an error occurs while creating the bean
    */
    @Bean
    AuthenticationManager authentificationManager(AuthenticationConfiguration configuration) throws Exception {
      return configuration.getAuthenticationManager();
    }

    @Bean
    CorsFilter corsFilter() {
      final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      final CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);
      // Don't do this in production, use a proper list  of allowed origins
      config.addAllowedOriginPattern("*");
      config.addAllowedHeader("*");
      config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
    }
}