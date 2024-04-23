package com.example.api.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.model.database.Acl;
import com.example.api.model.database.User;
import com.example.api.repository.AclRepository;

@Service
public class AclService {

    @Autowired
    AclRepository aclRepository;

    public Iterable<Acl> findAclsByUser(User user) {
        return aclRepository.findByUser(user);
    }

    public Optional<Acl> findAclsByUserAndTopic(User user, String topic) {
        return aclRepository.findAclByUserAndTopic(user, topic);
    }

    public Acl save(Acl acl) {
        return aclRepository.save(acl);
    }

    public void deleteAcl(final UUID id) {
        aclRepository.deleteById(id);
    }

}
