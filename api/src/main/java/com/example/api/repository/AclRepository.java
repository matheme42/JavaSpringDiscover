package com.example.api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.api.model.database.Acl;
import com.example.api.model.database.User;

/**
 * Repository interface for ACL (Access Control List) entities.
 */
@Repository
public interface AclRepository extends JpaRepository<Acl, UUID>{

    /**
     * Retrieves all ACLs associated with a specific user.
     * 
     * @param user the user
     * @return an iterable of ACLs
     */
    @Query("""
            Select t from Acl t
            inner join User u on t.user.id = u.id
            where t.user = :user
    """)
    Iterable<Acl> findByUser(User user);

    /**
     * Retrieves an ACL by user and topic.
     * 
     * @param user the user
     * @param topic the topic
     * @return an optional ACL
     */
    @Query("""
            Select t from Acl t
            inner join User u on t.user.id = u.id
            where t.user = :user and t.topic = :topic
    """)    
    Optional<Acl> findAclByUserAndTopic(User user, String topic);
}
