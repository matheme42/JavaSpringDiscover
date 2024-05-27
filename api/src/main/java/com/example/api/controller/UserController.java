package com.example.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.model.database.User;
import com.example.api.service.UserService;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/search/user")
    public ResponseEntity<List<Map<String, Object>>> FilterUserByID(@RequestParam("pattern") String pattern) {
        List<User> users = userService.findByUSernamePattern(pattern);
        List<Map<String, Object>> response = new ArrayList<>();
        for (User user : users) {
            response.add(Map.of(
                "username", user.getUsername(),
                "image", user.getImage(),
                "role", user.getRole()
            ));
        }
        return ResponseEntity.ok(response);
    }
}
