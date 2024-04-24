package com.example.api.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.model.database.Acl;
import com.example.api.model.database.User;
import com.example.api.repository.AclRepository;

/**
 * Service class for ACL (Access Control List) operations.
 */
@Service
public class AclService {

    @Autowired
    AclRepository aclRepository;

    /**
     * Retrieves all ACLs associated with a specific user.
     * 
     * @param user the user
     * @return an iterable of ACLs
     */
    public Iterable<Acl> findAclsByUser(User user) {
        return aclRepository.findByUser(user);
    }

    /**
     * Retrieves an ACL by user and topic.
     * 
     * @param user the user
     * @param topic the topic
     * @return an optional ACL
     */
    public Optional<Acl> findAclsByUserAndTopic(User user, String topic) {
        return aclRepository.findAclByUserAndTopic(user, topic);
    }

    /**
     * Saves an ACL.
     * 
     * @param acl the ACL to be saved
     * @return the saved ACL
     */
    public Acl save(Acl acl) {
        return aclRepository.save(acl);
    }

    /**
     * Deletes an ACL by its ID.
     * 
     * @param id the ID of the ACL to be deleted
     */
    public void deleteAcl(final UUID id) {
        aclRepository.deleteById(id);
    }

}
