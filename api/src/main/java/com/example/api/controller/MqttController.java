/**
 * Controller class for managing MQTT-related operations.
 */
package com.example.api.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.DTO.AclDTO.CreateAclDTO;
import com.example.api.config.CustomHandler.CustomBadRequestHandler;
import com.example.api.model.database.User;
import com.example.api.service.AclService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller class for managing MQTT-related operations.
 */
@RestController
public class MqttController extends CustomBadRequestHandler {


    @Autowired
    AclService aclService;
    
    /**
     * Creates or updates a topic for the authenticated user.
     *
     * @param user The authenticated user.
     * @param body The request body containing ACL details.
     * @param result Binding result for validation errors.
     * @return ResponseEntity<HashMap<String, Object>> A response entity indicating success or failure.
     */
    @PostMapping("/acl")    
    public ResponseEntity<HashMap<String, Object>> createOrUpdateTopicForUser(@AuthenticationPrincipal User user, @RequestBody @Valid CreateAclDTO body, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);
        HashMap<String, Object> response = aclService.createOrUpdateTopicForUser(user, body.getTopic(), body.getRw());
        return new ResponseEntity<>(response, response.containsKey("error") ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.OK);
    }
}
