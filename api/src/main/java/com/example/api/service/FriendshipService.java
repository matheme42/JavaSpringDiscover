package com.example.api.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.model.database.Friendship;
import com.example.api.model.database.User;
import com.example.api.repository.FriendshipRepository;

/**
 * Service class for managing friendships.
 */
@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    /**
     * Retrieves all friendships associated with a given user.
     *
     * @param user the user to retrieve friendships for
     * @return an iterable collection of friendships
     */
    public Iterable<Friendship> getFriendshipsByUser(User user) {
        return friendshipRepository.findByUser(user);
    }

    public Optional<Friendship> getFriendshipBetweenUsers(User u1, User u2) {
        return friendshipRepository.findFriendshipBetweenUsers(u1, u2);
    }

    /**
     * Retrieves a friendship by its UUID.
     *
     * @param uuid the UUID of the friendship to retrieve
     * @return an optional containing the retrieved friendship, or empty if not found
     */
    public Optional<Friendship> getFriendship(UUID uuid) {
        return friendshipRepository.findById(uuid);
    }

    /**
     * Retrieves all friendships.
     *
     * @return an iterable collection of all friendships
     */
    public Iterable<Friendship> getFriendships() {
        return friendshipRepository.findAll();
    }

    /**
     * Saves a friendship.
     *
     * @param friendship the friendship to save
     * @return the saved friendship
     */
    public Friendship saveFriendship(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }

    /**
     * Deletes a friendship by its UUID.
     *
     * @param uuid the UUID of the friendship to delete
     */
    public void deleteFriendship(UUID uuid) {
        friendshipRepository.deleteById(uuid);
    }

    public Optional<Friendship> findPendingFriendshipWithUser(User user, User friend) {
        return friendshipRepository.findPendingFriendshipWithUser(user, friend);
    }

}
