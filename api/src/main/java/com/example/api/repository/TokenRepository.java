package com.example.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.api.model.database.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID>{


    @Query("""
            Select t from Token t inner join User u
            on t.user.id = u.id
            where t.user.id = :userUuid and t.loggedOut = false
    """)
    List<Token> findAllTokenByUser(UUID userUuid);

    @Query("""
    """)
    Optional <Token> findByToken(String token);
}
