/**
 * Controller class for managing MQTT-related operations.
 */
package com.example.api.controller;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
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

    @Autowired
    private RedisTemplate<String, String> template;
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

        Optional<Acl> saerchAcl = aclService.findAclsByUserAndTopic(user, body.getTopic());
        Acl acl = saerchAcl.isPresent() ? saerchAcl.get() : new Acl();

        String topic = body.getTopic();
        Integer rw = body.getRw();
        String name = user.getUsername();

        acl.setUser(user);
        acl.setRw(rw);
        acl.setTopic(topic);
        acl.setUsername(name);
        acl = aclService.save(acl);


        try {
            SetOperations<String, String> operator = template.opsForSet();
            operator.remove(name + ":sacls", topic);
            operator.remove(name + ":racls", topic);
            operator.remove(name + ":wacls", topic);
            if ((rw & 1) != 0) operator.add(name + ":racls", topic);
            if ((rw & 2) != 0) operator.add(name + ":wacls", topic);
            if ((rw & 4) != 0) operator.add(name + ":sacls", topic);
            return new ResponseEntity<>(new HashMap<>() {{put("message", "ok");}}, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new HashMap<>() {{put("error", "cache server unavailable");}}, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * Retrieves the MQTT password for the authenticated user.
     *
     * @param user The authenticated user.
     * @return ResponseEntity<HashMap<String, Object>> A response entity containing the MQTT password.
     */
    @GetMapping("/mqtt_password")    
    public ResponseEntity<HashMap<String, Object>> getMqttPassword(@AuthenticationPrincipal User user) {
        try {
            SetOperations<String, String> operator = template.opsForSet();
            String name = user.getUsername();
            template.delete(name + ":sacls");
            template.delete(name + ":racls");
            template.delete(name + ":wacls");

            aclService.findAclsByUser(user).forEach((acl) -> {
                String topic = acl.getTopic();
                Integer rw = acl.getRw();
                if ((rw & 1) != 0) operator.add(name + ":racls", topic);
                if ((rw & 2) != 0) operator.add(name + ":wacls", topic);
                if ((rw & 4) != 0) operator.add(name + ":sacls", topic);
            });    
        } catch (Exception e) {
            return new ResponseEntity<>(new HashMap<>() {{put("error", "cache server unavailable");}}, HttpStatus.SERVICE_UNAVAILABLE);
        }

        try {
            final String jwtToken = jwtService.generateMqttToken(user);
            template.opsForValue().set(user.getUsername(), passwordEncoder.encode(jwtToken));
            return new ResponseEntity<>(new HashMap<>() {{put("password", jwtToken);}}, HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(new HashMap<>() {{put("error", "cache server unavailable");}}, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
