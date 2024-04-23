package com.example.api.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.config.CustomHandler.CustomBadRequestHandler;
import com.example.api.model.database.User;
import com.example.api.service.JwtService;
import com.example.api.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class MqttController extends CustomBadRequestHandler {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

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
