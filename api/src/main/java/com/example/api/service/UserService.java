package com.example.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.api.model.database.User;
import com.example.api.repository.UserRepository;

/**
 * Service class for managing User entities.
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user
     * @return UserDetails object containing the user's details
     * @throws UsernameNotFoundException if the user is not found
     */    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user
     * @return an optional containing the user if found, otherwise empty
     */   
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Saves a user.
     *
     * @param user the user to be saved
     * @return the saved user
     */   
    public User save(User user) {
        return userRepository.save(user);
    }

}
