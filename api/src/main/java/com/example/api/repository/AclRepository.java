package com.example.api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.api.model.database.Acl;
import com.example.api.model.database.User;

@Repository
public interface AclRepository extends JpaRepository<Acl, UUID>{

    @Query("""
            Select t from Acl t
            inner join User u on t.user.id = u.id
            where t.user = :user
    """)
    Iterable<Acl> findByUser(User user);

    @Query("""
            Select t from Acl t
            inner join User u on t.user.id = u.id
            where t.user = :user and t.topic = :topic
    """)    
    Optional<Acl> findAclByUserAndTopic(User user, String topic);
}
