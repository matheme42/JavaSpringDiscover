package com.example.api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.api.model.database.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>{

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user
     * @return an optional containing the user if found, otherwise empty
     */
    Optional<User> findByUsername(String username);

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user
     * @return an optional containing the user if found, otherwise empty
     */
    Optional<User> findByEmail(String email);
}