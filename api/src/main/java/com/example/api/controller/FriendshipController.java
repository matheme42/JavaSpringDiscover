/**
 * Controller class for managing friendships between users.
 */
package com.example.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.example.api.model.enums.SocketMessageType;
import com.example.api.service.FriendshipService;
import com.example.api.service.SocketService;
import com.example.api.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class for managing friendships between users.
 */
@RestController
public class FriendshipController extends CustomBadRequestHandler {

    @Autowired
    FriendshipService friendshipService;

    @Autowired
    UserService userService;

    @Autowired
    SocketService socketService;

    /**
     * Retrieves the list of friendships for the authenticated user, along with
     * pending invitations.
     *
     * @param user The authenticated user.
     * @return ResponseEntity<HashMap<String, Object>> A response entity containing
     *         a map with 'friends' and 'pendingInvitation' lists.
     */
    @GetMapping("/friendships")
    public ResponseEntity<HashMap<String, Object>> listFriendships(@AuthenticationPrincipal User user) {
        Iterator<Friendship> friendships = friendshipService.getFriendshipsByUser(user).iterator();

        HashMap<String, Object> map = new HashMap<String, Object>();
        ArrayList<Map<Object, Object>> friends = new ArrayList<Map<Object, Object>>();
        ArrayList<Map<Object, Object>> pendingInvitation = new ArrayList<Map<Object, Object>>();
        ArrayList<Map<Object, Object>> invitation = new ArrayList<Map<Object, Object>>();
        try {
            while (friendships.hasNext()) {
                Friendship friendship = friendships.next();
                if (friendship.getIs_accepted() != null && friendship.getIs_accepted() == false)
                    continue;

                Boolean isRemainUser = friendship.getUser().getUsername().compareTo(user.getUsername()) == 0;
                User friend = isRemainUser ? friendship.getFriend() : friendship.getUser();

                if (friendship.getIs_accepted() != null && friendship.getIs_accepted() == true) {
                    friends.add(Map.of(
                            "username", friend.getUsername(),
                            "image", String.format("%s", friend.getImage()),
                            "role", friend.getRole(),
                            "logged", friend.getLogged(),
                            "last_connection", String.format("%s", friend.getLastConnection())));
                    continue;
                }

                if (isRemainUser) {
                    pendingInvitation.add(Map.of(
                            "username", friend.getUsername(),
                            "image", String.format("%s", friend.getImage()),
                            "role", friend.getRole()));
                    continue;
                }
                invitation.add(Map.of(
                        "username", friend.getUsername(),
                        "image", String.format("%s", friend.getImage()),
                        "role", friend.getRole()));
            }

        } catch (Exception e) {
            StackTraceElement[] elm = e.getStackTrace();
            for (StackTraceElement x : elm) {
                System.err.println(x.toString());
            }
        }

        map.put("friends", friends);
        map.put("pendingInvitation", pendingInvitation);
        map.put("invitation", invitation);

        return ResponseEntity.ok(map);
    }

    /**
     * Deletes a friendship between the authenticated user and another user.
     *
     * @param username The username of the user to remove friendship with.
     * @param user     The authenticated user.
     * @param result   Binding result for validation errors.
     * @return ResponseEntity<HashMap<String, Object>> A response entity indicating
     *         success or failure.
     */
    @DeleteMapping("/friendship")
    public ResponseEntity<HashMap<String, Object>> deleteFriendship(@AuthenticationPrincipal User user,
            @RequestParam("username") String username) {
        User claimantUser;
        try {
            claimantUser = (User) userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ex) {
            return badRequestErrorJsonResponse("user doesn't exist");
        }

        try {
            Optional<Friendship> searchFriendship = friendshipService.getFriendshipBetweenUsers(user, claimantUser);
            if (searchFriendship.isEmpty()) {
                return badRequestErrorJsonResponse("friendship does not exist");
            }
            Friendship friendship = searchFriendship.get();
            socketService.sendUserAsJsonMessageOnSession(SocketMessageType.friendshipRemove, user, username);
            socketService.sendUserAsJsonMessageOnSession(SocketMessageType.friendshipRemove, claimantUser,
                    user.getUsername());

            friendshipService.deleteFriendship(friendship.getId());
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        return ResponseEntity.ok(new HashMap<>() {
            {
                put("message", "ok");
            }
        });
    }

    /**
     * Creates a friendship invitation from the authenticated user to another user.
     *
     * @param user    The authenticated user.
     * @param request The friendship invitation request.
     * @param result  Binding result for validation errors.
     * @return ResponseEntity<HashMap<String, Object>> A response entity indicating
     *         success or failure.
     */
    @PostMapping("/friendship_request")
    public ResponseEntity<HashMap<String, Object>> createFriendshipInvitation(@AuthenticationPrincipal User user,
            @RequestBody @Valid FriendshipInvitationDTO request, BindingResult result) {
        if (result.hasErrors())
            return handleBadRequest(result);
        User friend;
        try {
            friend = (User) userService.loadUserByUsername(request.getUsername());
        } catch (UsernameNotFoundException ex) {
            return badRequestErrorJsonResponse("user doesn't exist");
        }

        if (user.getId().compareTo(friend.getId()) == 0) {
            return badRequestErrorJsonResponse("can't create friendship with yourself");
        }

        Optional<Friendship> searchFriendship = friendshipService.getFriendshipBetweenUsers(user, friend);

        Friendship friendship = searchFriendship.isPresent() ? searchFriendship.get() : new Friendship();
        if (searchFriendship.isPresent()) {
            boolean isMainUser = friendship.getUser().getUsername().compareTo(user.getUsername()) == 0;
            if (friendship.getIs_accepted() == null) {
                return badRequestErrorJsonResponse(isMainUser ? "you already have send an invitation"
                        : "you already have an invitation for this relation");
            }
            if (friendship.getIs_accepted() == true) {
                return badRequestErrorJsonResponse("this relation already exists");
            }
            if (isMainUser) {
                return badRequestErrorJsonResponse("you can't spam the user for this relation");
            }
        }

        friendship.setFriend(friend);
        friendship.setUser(user);
        friendship.setIs_accepted(null);

        socketService.sendUserAsJsonMessageOnSession(SocketMessageType.selfFriendshipInvitation, friend,
                user.getUsername());

        if (friend.getLogged() != null && friend.getLogged() == true) {
            socketService.sendUserAsJsonMessageOnSession(SocketMessageType.friendshipInvitation, user,
                    friend.getUsername());
        }

        friendshipService.saveFriendship(friendship);
        return ResponseEntity.ok(new HashMap<>() {
            {
                put("message", "ok");
            }
        });
    }

    /**
     * Replies to a friendship invitation sent to the authenticated user.
     *
     * @param user    The authenticated user.
     * @param request The reply to friendship invitation request.
     * @param result  Binding result for validation errors.
     * @return ResponseEntity<HashMap<String, Object>> A response entity indicating
     *         success or failure.
     */
    @PutMapping("/friendship_request")
    public ResponseEntity<HashMap<String, Object>> replyFriendshipInvitation(@AuthenticationPrincipal User user,
            @RequestBody @Valid ReplyFriendshipInvitationDTO request, BindingResult result) {
        if (result.hasErrors())
            return handleBadRequest(result);
        User claimantUser;
        try {
            claimantUser = (User) userService.loadUserByUsername(request.getUsername());
        } catch (UsernameNotFoundException ex) {
            return badRequestErrorJsonResponse("user doesn't exist");
        }

        Optional<Friendship> searchFriendship = friendshipService.findPendingFriendshipWithUser(user, claimantUser);
        if (searchFriendship.isEmpty()) {
            return badRequestErrorJsonResponse("invitation does not exist");
        }

        Friendship friendship = searchFriendship.get();
        friendship.setIs_accepted(request.getResponse());
        friendshipService.saveFriendship(friendship);

        if (request.getResponse() == null || request.getResponse() == false) {
            socketService.sendUserAsJsonMessageOnSession(SocketMessageType.friendshipRemove, user,
                    claimantUser.getUsername());
            socketService.sendUserAsJsonMessageOnSession(SocketMessageType.friendshipRemove, claimantUser,
                    user.getUsername());
            return ResponseEntity.ok(new HashMap<>() {
                {
                    put("message", "ok");
                }
            });

        }
        if (claimantUser.getLogged() != null && claimantUser.getLogged() == true) {
            String username = claimantUser.getUsername();
            socketService.sendFullInformationUserAsJsonMessageOnSession(SocketMessageType.friendshipInvitationReply,
                    user,
                    username);
        }
        socketService.sendFullInformationUserAsJsonMessageOnSession(SocketMessageType.friendshipInvitationReply,
                claimantUser,
                user.getUsername());

        HashMap<String, Object> response = new HashMap<>();
        response.put("username", claimantUser.getUsername());
        response.put("image", claimantUser.getImage());
        response.put("role", claimantUser.getRole());
        response.put("logged", claimantUser.getLogged());
        response.put("last_connection", String.format("%s", claimantUser.getLastConnection()));
        return ResponseEntity.ok(response);
    }

}
