package com.example.api.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.api.model.database.User;

@Service
public class MqttService {

    @Autowired
    RedisTemplate<String, String> template;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public HashMap<String, Object> createOrUpdateTopicForUser(User user) {
        try {
            final String jwtToken = "";
            template.opsForValue().set(user.getUsername(), passwordEncoder.encode(jwtToken));
            return new HashMap<>() {
                {
                    put("password", jwtToken);
                }
            };
        } catch (Exception e) {
            return new HashMap<>() {
                {
                    put("error", "cache server unavailable");
                }
            };
        }
    }
}
