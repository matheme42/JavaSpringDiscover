package com.example.api.service;


import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.api.model.db.User;
import com.example.api.model.request.authentication.loginRequest;
import com.example.api.repository.UserRepository;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;


    public ResponseEntity<HashMap<String, Object>> register(User request) {

        Optional<User> searchUser = repository.findByUsername(request.getUsername());
        if (searchUser.isPresent()) return new ResponseEntity<>(new HashMap<>() {{
            put("message" , "rejected");
            put("error" , "user already exist");
        }}, HttpStatus.BAD_REQUEST);


        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setImage(request.getImage());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user = repository.save(user);

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new HashMap<>() {{put("token" , token);}});
    }

    public ResponseEntity<HashMap<String, Object>> authenticate(loginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        
        Optional<User> searchUser = repository.findByUsername(request.getUsername());
        if (!searchUser.isPresent()) return new ResponseEntity<>(new HashMap<>() {{
            put("message" , "rejected");
            put("error" , "user not found");
        }}, HttpStatus.BAD_REQUEST);


        User user = searchUser.get();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new HashMap<>() {{put("token" , token);}});
    }
}
