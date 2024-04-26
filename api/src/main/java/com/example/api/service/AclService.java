package com.example.api.service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
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

    @Autowired
    RedisTemplate<String, String> template;

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


    public HashMap<String, Object> clearAndFillAclCacheForUser(User user) {
        try {
            SetOperations<String, String> operator = template.opsForSet();
            String name = user.getUsername();
            template.delete(name + ":sacls");
            template.delete(name + ":racls");
            template.delete(name + ":wacls");

            findAclsByUser(user).forEach((acl) -> {
                String topic = acl.getTopic();
                Integer rw = acl.getRw();
                if ((rw & 1) != 0) operator.add(name + ":racls", topic);
                if ((rw & 2) != 0) operator.add(name + ":wacls", topic);
                if ((rw & 4) != 0) operator.add(name + ":sacls", topic);
            });
        } catch (Exception e) {
            return new HashMap<>() {{put("error", "cache server unavailable");}};
        }
        return new HashMap<>() {{put("message", "ok");}};
    }

    public HashMap<String, Object> createOrUpdateTopicForUser(User user, String topic, Integer rw) {
        Optional<Acl> saerchAcl = findAclsByUserAndTopic(user, topic);
        Acl acl = saerchAcl.isPresent() ? saerchAcl.get() : new Acl();

        String name = user.getUsername();

        acl.setUser(user);
        acl.setRw(rw);
        acl.setTopic(topic);
        acl.setUsername(name);
        acl = save(acl);
        
        try {
            SetOperations<String, String> operator = template.opsForSet();
            operator.remove(name + ":sacls", topic);
            operator.remove(name + ":racls", topic);
            operator.remove(name + ":wacls", topic);
            if ((rw & 1) != 0) operator.add(name + ":racls", topic);
            if ((rw & 2) != 0) operator.add(name + ":wacls", topic);
            if ((rw & 4) != 0) operator.add(name + ":sacls", topic);
            return new HashMap<>() {{put("message", "ok");}};
        } catch (Exception e) {
            return new HashMap<>() {{put("error", "cache server unavailable");}};
        }
    }

}
