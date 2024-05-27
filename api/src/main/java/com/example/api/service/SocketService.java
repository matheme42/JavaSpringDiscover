package com.example.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.api.model.database.User;
import com.example.api.model.enums.SocketMessageType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SocketService {

    private final HashMap<String, List<WebSocketSession>> sessions = new HashMap<>();

    private List<WebSocketSession> getSocketListByUsername(String username) {
        return sessions.get(username);
    }

    public boolean disconnectSocketByUsername(WebSocketSession session) {
        String username = session.getPrincipal().getName();
        List<WebSocketSession> userSessions = getSocketListByUsername(username);
        userSessions.remove(session);
        if (userSessions.isEmpty()) {
            sessions.remove(username);
            return false;
        }
        return true;
    }

    public void disconnectSocketsByUsername(String username) {
        sendJsonMessageOnSession(SocketMessageType.disconnection, Map.of(), username);
        for (WebSocketSession session : sessions.get(username)) {
            try {
                if (session.isOpen()) {
                    session.close();
                }
            } catch (Exception e) {

            }
        }
        sessions.remove(username);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void connectSocketByUsername(String username, WebSocketSession session) {
        if (!sessions.containsKey(username)) {
            sessions.put(username, new CopyOnWriteArrayList());
        }
        sessions.get(username).add(session);
    }

    public void sendJsonMessageOnSession(String messageType, Map<String, Object> map, String username) {
        List<WebSocketSession> localSessions = sessions.get(username);
        if (localSessions == null) {
            return;
        }
        for (WebSocketSession session : localSessions) {
            if (!session.isOpen())
                continue;
            try {
                HashMap<String, Object> hashMap = new HashMap<>(map);
                hashMap.put("message_type", messageType);
                String mapAsJson = new ObjectMapper().writeValueAsString(hashMap);
                session.sendMessage(new TextMessage(mapAsJson));
            } catch (Exception e) {
                for (StackTraceElement x : e.getStackTrace()) {
                    System.err.println(x.toString());
                }
            }
        }
    }

    public void sendFullInformationUserAsJsonMessageOnSession(String messageType, User user, String username) {
        this.sendJsonMessageOnSession(messageType, Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "image", user.getImage(),
                "logged", user.getLogged(),
                "last_connection", String.format("%s", user.getLastConnection())), username);
    }

    public void sendUserAsJsonMessageOnSession(String messageType, User user, String username) {
        this.sendJsonMessageOnSession(messageType, Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "image", user.getImage()), username);
    }

    public void sendConnectionReplyAsJsonMessageOnSession(String messageType, User user, String username) {
        this.sendJsonMessageOnSession(messageType, Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "image", user.getImage()), username);
    }
}
