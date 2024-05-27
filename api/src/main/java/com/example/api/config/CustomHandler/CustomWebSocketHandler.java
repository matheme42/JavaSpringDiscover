package com.example.api.config.CustomHandler;

import java.security.Principal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.api.model.database.Friendship;
import com.example.api.model.database.User;
import com.example.api.model.enums.SocketMessageType;
import com.example.api.service.AuthenticationService;
import com.example.api.service.FriendshipService;
import com.example.api.service.SocketService;
import com.example.api.service.UserService;

public class CustomWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    UserService userService;

    @Autowired
    SocketService socketService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    FriendshipService friendshipService;

    private void sendToUserFriendAMessage(User user, String messageType) {
        Iterable<Friendship> friendships = friendshipService.getFriendshipsByUser(user);
        for (Friendship friendship : friendships) {
            boolean isUserInFriendship = friendship.getUser().getUsername().compareTo(user.getUsername()) == 0;
            User friend = isUserInFriendship ? friendship.getFriend() : friendship.getUser();
            if (friend.getLogged() != null && friend.getLogged() == true) {
                socketService.sendUserAsJsonMessageOnSession(messageType, user, friend.getUsername());
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Principal principal = session.getPrincipal();
        System.out.println("connection etablish: " + principal.getName());
        User user = (User) userService.loadUserByUsername(principal.getName());

        socketService.connectSocketByUsername(user.getUsername(), session);
        socketService.sendConnectionReplyAsJsonMessageOnSession(SocketMessageType.connection, user, principal.getName());

        if (user.getLogged() == false) {
            sendToUserFriendAMessage(user, SocketMessageType.friendshipConnection);
            user.setLogged(true);
            userService.save(user);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Principal principal = session.getPrincipal();
        System.out.println("connection closed: " + principal.getName());

        if (socketService.disconnectSocketByUsername(session) == false) {
            User user = (User) userService.loadUserByUsername(principal.getName());
            sendToUserFriendAMessage(user, SocketMessageType.friendshipDisconnection);
            user.setLogged(false);
            user.setLastConnection(new Date());
            userService.save(user);
        }
    }

}