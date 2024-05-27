package com.example.api.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.DTO.MessageDTO.NewMessageDTO;
import com.example.api.config.CustomHandler.CustomBadRequestHandler;
import com.example.api.model.database.Friendship;
import com.example.api.model.database.Message;
import com.example.api.model.database.User;
import com.example.api.model.enums.SocketMessageType;
import com.example.api.service.FriendshipService;
import com.example.api.service.MessageService;
import com.example.api.service.SocketService;
import com.example.api.service.UserService;

import jakarta.validation.Valid;

class MessageComparator implements java.util.Comparator<Message> {
    @Override
    public int compare(Message o1, Message o2) {
        return o1.getCreatedDate().compareTo(o2.getCreatedDate());
    }
}

@RestController
public class MessageController extends CustomBadRequestHandler {

    @Autowired
    MessageService messagService;

    @Autowired
    FriendshipService friendshipService;

    @Autowired
    UserService userService;

    @Autowired
    SocketService socketService;

    @SuppressWarnings("rawtypes")
    @GetMapping("/messages/{username}")
    public ResponseEntity getMessagesFromFriendship(@AuthenticationPrincipal User user, @PathVariable String username) {
        User friend;

        try {
            friend = (User) userService.loadUserByUsername(username);
        } catch (Exception e) {
            return badRequestErrorJsonResponse("user not found");
        }

        Optional<Friendship> searchFriendship = friendshipService.getFriendshipBetweenUsers(user, friend);
        if (searchFriendship.isEmpty())
            return badRequestErrorJsonResponse("friendship doesn't exist");

        List<Message> messages = messagService.findByFriendship(searchFriendship.get());
        ArrayList<Map<Object, Object>> messagesList = new ArrayList<Map<Object, Object>>();

        Collections.sort(messages, new MessageComparator());

        for (Message m : messages) {
            messagesList.add(Map.of(
                    "content", m.getContent(),
                    "date", m.getCreatedDate(),
                    "user_name", m.getUser().getUsername(),
                    "user_image", m.getUser().getImage()));
        }
        return ResponseEntity.ok(messagesList);
    }

    @PostMapping("/messages/{username}")
    public ResponseEntity<HashMap<String, Object>> InsertMessagesFromFriendship(@AuthenticationPrincipal User user,
            @PathVariable String username, @RequestBody @Valid NewMessageDTO body, BindingResult result) {
        User friend;
        if (result.hasErrors()) {
            return handleBadRequest(result);
        }

        try {
            friend = (User) userService.loadUserByUsername(username);
        } catch (Exception e) {
            return badRequestErrorJsonResponse("user not found");
        }

        Optional<Friendship> searchFriendship = friendshipService.getFriendshipBetweenUsers(user, friend);
        if (searchFriendship.isEmpty())
            return badRequestErrorJsonResponse("friendship doesn't exist");
        Friendship friendship = searchFriendship.get();

        Message message = new Message();
        message.setContent(body.getContent());
        message.setCreatedDate(new Date());
        message.setFriendship(friendship);
        message.setUser(user);

        message.setFriendship(friendship);
        messagService.saveMessage(message);

        socketService.sendJsonMessageOnSession(SocketMessageType.friendshipMessage, Map.of(
                "content", message.getContent(),
                "date", message.getCreatedDate(),
                "user_name", message.getUser().getUsername(),
                "user_image", message.getUser().getImage()), username);

        return ResponseEntity.ok(new HashMap<>() {
            {
                put("message", "ok");
            }
        });
    }
}
