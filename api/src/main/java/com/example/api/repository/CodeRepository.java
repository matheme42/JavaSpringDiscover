package com.example.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.api.model.database.Code;
import com.example.api.model.enums.Type;

@Repository
public interface CodeRepository extends JpaRepository<Code, UUID>{
    
    @Query("""
            Select t from Code t inner join User u
            on t.user.id = u.id
            where t.user.id = :userUuid and t.isValid = true and type = :type
    """)
    List<Code> findAllCodeByUserAndType(UUID userUuid, Type type);

    
    @Query("""
    """)
    Optional <Code> findByToken(String token);

}