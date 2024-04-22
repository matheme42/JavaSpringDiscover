package com.example.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.api.model.database.Token;

/**
 * TokenRepository Interface
 * <p>
 * Repository for managing Token entities in the database.
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, UUID>{


    /**
     * Retrieves all tokens associated with a user that are not logged out.
     *
     * @param userUuid the UUID of the user
     * @return a list of tokens associated with the user that are not logged out
     */
    @Query("""
            Select t from Token t inner join User u
            on t.user.id = u.id
            where t.user.id = :userUuid and t.loggedOut = false
    """)
    List<Token> findAllTokenByUser(UUID userUuid);

    /**
     * Retrieves a token by its token value.
     *
     * @param token the token value
     * @return an optional containing the token if found, otherwise empty
     */    
    @Query("""
        Select t from Token t
        where t.token = :token
    """)
    Optional <Token> findByToken(String token);
}
