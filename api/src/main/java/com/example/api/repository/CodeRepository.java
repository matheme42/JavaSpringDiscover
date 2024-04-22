package com.example.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.api.model.database.Code;
import com.example.api.model.enums.Type;

/**
 * CodeRepository Interface
 * <p>
 * Repository for managing Code entities in the database.
 */
@Repository
public interface CodeRepository extends JpaRepository<Code, UUID>{
    
   /**
     * Retrieves all codes associated with a user and a specific type.
     *
     * @param userUuid the UUID of the user
     * @param type     the type of the code
     * @return a list of codes associated with the user and type
     */    
    @Query("""
            Select t from Code t inner join User u
            on t.user.id = u.id
            where t.user.id = :userUuid and t.isValid = true and type = :type
    """)
    List<Code> findAllCodeByUserAndType(UUID userUuid, Type type);

    /**
     * Retrieves a code by its token.
     *
     * @param token the token of the code
     * @return an optional containing the code if found, otherwise empty
     */    
    @Query("""
            Select c from Code c
            where c.token = :token
    """)
    Optional <Code> findByToken(String token);

}