package com.example.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.DTO.UserDTO.EditUserDTO;
import com.example.api.DTO.UserDTO.EditUserPasswordDTO;
import com.example.api.config.CustomHandler.CustomBadRequestHandler;
import com.example.api.model.database.User;
import com.example.api.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController extends CustomBadRequestHandler {

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/search/user")
    public ResponseEntity<List<Map<String, Object>>> FilterUserByID(@RequestParam("pattern") String pattern) {
        List<User> users = userService.findByUSernamePattern(pattern);
        List<Map<String, Object>> response = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> map = Map.of(
                    "username", user.getUsername(),
                    "image", String.format("%s", user.getImage()),
                    "role", user.getRole());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/user")
    public ResponseEntity<HashMap<String, Object>> edit(@AuthenticationPrincipal User user,
            @RequestBody @Valid EditUserDTO body,
            BindingResult result) {
        if (result.hasErrors())
            return handleBadRequest(result);

        user.setUsername(body.getUsername());
        user.setEmail(body.getEmail());
        user.setImage(body.getImage());

        userService.save(user);

        HashMap<String, Object> map = new HashMap<>();
        map.put("message", "ok");
        return ResponseEntity.ok(map);
    }

    @PutMapping("/user/password")
    public ResponseEntity<HashMap<String, Object>> editPassword(@AuthenticationPrincipal User user,
            @RequestBody @Valid EditUserPasswordDTO body,
            BindingResult result) {
        if (result.hasErrors())
            return handleBadRequest(result);

        boolean matches = passwordEncoder.matches(body.getOldPassword(), user.getPassword());
        if (matches == false) {
            return badRequestErrorJsonResponse("l'ancien mot de passe n'est pas correct");
        }

        user.setPassword(passwordEncoder.encode(body.getNewPassword()));
        userService.save(user);

        HashMap<String, Object> map = new HashMap<>();
        map.put("message", "ok");
        return ResponseEntity.ok(map);
    }

}
