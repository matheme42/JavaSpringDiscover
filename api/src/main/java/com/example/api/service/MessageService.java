package com.example.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.model.database.Friendship;
import com.example.api.model.database.Message;
import com.example.api.model.database.User;
import com.example.api.repository.MessageRepository;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public List<Message> findByUser(User user) {
        return messageRepository.findByUser(user);
    }

    public List<Message> findByFriendship(Friendship friendship) {
        return messageRepository.findByFriendship(friendship);
    }

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }
}
