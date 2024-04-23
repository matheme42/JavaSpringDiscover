package com.example.api.controller;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.DTO.AclDTO.CreateAclDTO;
import com.example.api.config.CustomHandler.CustomBadRequestHandler;
import com.example.api.model.database.Acl;
import com.example.api.model.database.User;
import com.example.api.service.AclService;

import jakarta.validation.Valid;

@RestController
public class AclController extends CustomBadRequestHandler {

    @Autowired
    AclService aclService;

    @PostMapping("/acl")    
    public ResponseEntity<HashMap<String, Object>> createOrUpdateTopicForUser(@AuthenticationPrincipal User user, @RequestBody @Valid CreateAclDTO body, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);

        System.out.println(user.getAcls().size());
        Optional<Acl> saerchAcl = aclService.findAclsByUserAndTopic(user, body.getTopic());
        Acl acl = saerchAcl.isPresent() ? saerchAcl.get() : new Acl();

        acl.setUser(user);
        acl.setRw(body.getRw());
        acl.setTopic(body.getTopic());
        acl.setUsername(user.getUsername());
        acl = aclService.save(acl);

        return new ResponseEntity<>(new HashMap<>() {{put("message", "ok");}}, HttpStatus.OK);
    }
}
