/**
 * Controller class for managing MQTT-related operations.
 */
package com.example.api.controller;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.DTO.AclDTO.CreateAclDTO;
import com.example.api.config.CustomHandler.CustomBadRequestHandler;
import com.example.api.model.database.Acl;
import com.example.api.model.database.User;
import com.example.api.service.AclService;
import com.example.api.service.JwtService;
import com.example.api.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller class for managing MQTT-related operations.
 */
@RestController
public class MqttController extends CustomBadRequestHandler {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

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

    /**
     * Retrieves the MQTT password for the authenticated user.
     *
     * @param user The authenticated user.
     * @return ResponseEntity<HashMap<String, Object>> A response entity containing the MQTT password.
     */
    @GetMapping("/mqtt_password")    
    public ResponseEntity<HashMap<String, Object>> getMqttPassword(@AuthenticationPrincipal User user) {
        boolean expired = true;
        try {
            expired = jwtService.isTokenExpired(user.getMqttPassword());
        } catch (ExpiredJwtException ex) {
            expired = true;
        }

        if (expired) {
            String jwtToken = jwtService.generateMqttToken(user);
            user.setMqttPassword(jwtToken);
            user.setMqttPasswordHash(passwordEncoder.encode(jwtToken));
            user = userService.save(user);
        }

        final String mqtttoken = user.getMqttPassword();

        return new ResponseEntity<>(new HashMap<>() {{put("password", mqtttoken);}}, HttpStatus.OK);
    }
}
