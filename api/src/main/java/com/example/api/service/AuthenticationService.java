package com.example.api.service;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.api.model.db.Token;
import com.example.api.model.db.User;
import com.example.api.model.request.authentication.loginRequest;
import com.example.api.repository.TokenRepository;
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


    @Autowired
    private TokenRepository tokenRepository;

    
    private void saveUserToken(String jwt, User user) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokenListByUser = tokenRepository.findAllTokenByUser(user.getId());
        if (validTokenListByUser.isEmpty()) return ;

        validTokenListByUser.forEach(t -> {t.setLoggedOut(true);});
        tokenRepository.saveAll(validTokenListByUser);
    }

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

        String jwt = jwtService.generateAuthToken(user);

        // save the generated token
        saveUserToken(jwt, user);


        return ResponseEntity.ok(new HashMap<>() {{put("token" , jwt);}});
    }

    public ResponseEntity<HashMap<String, Object>> authenticate(loginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        
        Optional<User> searchUser = repository.findByUsername(request.getUsername());
        if (!searchUser.isPresent()) return new ResponseEntity<>(new HashMap<>() {{
            put("message" , "rejected");
            put("error" , "user not found");
        }}, HttpStatus.BAD_REQUEST);


        User user = searchUser.get();
        String jwt = jwtService.generateAuthToken(user);
        
        revokeAllTokenByUser(user);
        saveUserToken(jwt, user);

        return ResponseEntity.ok(new HashMap<>() {{put("token" , jwt);}});
    }
}
