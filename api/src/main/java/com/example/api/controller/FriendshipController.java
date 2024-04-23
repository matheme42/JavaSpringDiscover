package com.example.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.DTO.FriendshipDTO.FriendshipInvitationDTO;
import com.example.api.DTO.FriendshipDTO.ReplyFriendshipInvitationDTO;
import com.example.api.config.CustomHandler.CustomBadRequestHandler;
import com.example.api.model.database.Friendship;
import com.example.api.model.database.User;
import com.example.api.service.FriendshipService;
import com.example.api.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class FriendshipController extends CustomBadRequestHandler {

    @Autowired
    FriendshipService friendshipService;

    @Autowired
    UserService userService;

    @GetMapping("/friendships")
    public ResponseEntity<HashMap<String, Object>> listFriendships(@AuthenticationPrincipal User user) {
        List<Friendship> friendships = new ArrayList<>();
        friendshipService.getFriendshipsByUser(user).iterator().forEachRemaining(friendships::add);
    
        List<Friendship> friends = friendships.stream().filter(c -> c.getIs_accepted() != null && c.getIs_accepted() == true).collect(Collectors.toList());
        List<Friendship> pendingInvitation = friendships.stream().filter(c -> c.getIs_accepted() == null).collect(Collectors.toList());

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("friends", friends);
        map.put("pendingInvitation", pendingInvitation);
        return ResponseEntity.ok(map);
    }

    @DeleteMapping("/friendship")
    public ResponseEntity<HashMap<String, Object>> deleteFriendship(@RequestParam("username") String username, @AuthenticationPrincipal User user, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);
        User claimantUser;
        try {
            claimantUser = (User)userService.loadUserByUsername(username);
        } catch(UsernameNotFoundException ex) {
            return badRequestErrorJsonResponse("user doesn't exist");
        }

        Optional<Friendship> searchFriendship = friendshipService.getFriendshipBetweenUsers(user, claimantUser);
        if (searchFriendship.isEmpty()) {
            return badRequestErrorJsonResponse("friendship doest not exist");
        }
  
        Friendship friendship = searchFriendship.get();
        friendshipService.deleteFriendship(friendship.getId());
        return ResponseEntity.ok(new HashMap<>(){{put("message", "ok");}});
    }

    @PostMapping("/friendship_request")
    public ResponseEntity<HashMap<String, Object>> createFriendshipInvitation(@AuthenticationPrincipal User user, @RequestBody @Valid FriendshipInvitationDTO request, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);
        User friend;
        try {
            friend = (User)userService.loadUserByUsername(request.getUsername());
        } catch(UsernameNotFoundException ex) {
            return badRequestErrorJsonResponse("user doesn't exist");
        }

        if (user.getId().compareTo(friend.getId()) == 0) {
            return badRequestErrorJsonResponse("can't create friendship with yourself");
        }

        Optional<Friendship> searchFriendship = friendshipService.getFriendshipBetweenUsers(user, friend);
  
        Friendship friendship = searchFriendship.isPresent() ? searchFriendship.get() : new Friendship();
        if (searchFriendship.isPresent() && friendship.getIs_accepted() != null && friendship.getIs_accepted() == true) {
            return badRequestErrorJsonResponse("this relation already exist");
        }

        friendship.setFriend(friend);
        friendship.setUser(user);
        friendship.setIs_accepted(null);

        friendshipService.saveFriendship(friendship);
        return ResponseEntity.ok(new HashMap<>(){{put("message", "ok");}});
    }


    @PutMapping("/friendship_request")
    public ResponseEntity<HashMap<String, Object>> replyFriendshipInvitation(@AuthenticationPrincipal User user, @RequestBody @Valid ReplyFriendshipInvitationDTO request, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);
        User claimantUser;
        try {
            claimantUser = (User)userService.loadUserByUsername(request.getUsername());
        } catch(UsernameNotFoundException ex) {
            return badRequestErrorJsonResponse("user doesn't exist");
        }

        Optional<Friendship> searchFriendship = friendshipService.findPendingFriendshipWithUser(user, claimantUser);
        if (searchFriendship.isEmpty()) {
            return badRequestErrorJsonResponse("invitation doest not exist");
        }
  
        Friendship friendship = searchFriendship.get();
        friendship.setIs_accepted(request.getResponse());
        friendshipService.saveFriendship(friendship);

        return ResponseEntity.ok(new HashMap<>(){{put("message", "ok");}});
    }
    
}
