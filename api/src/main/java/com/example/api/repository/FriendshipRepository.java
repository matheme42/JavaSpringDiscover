package com.example.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.api.model.database.Friendship;
import com.example.api.model.database.User;

/**
 * Repository interface for Friendship entities.
 */
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {
    

    /**
     * Retrieves all friendships associated with a specific user.
     * 
     * @param user the user
     * @return a list of friendships
     */
    @Query("""
            Select t from Friendship t
            inner join User u on t.user.id = u.id
            inner join User v on t.friend.id = v.id
            where t.user = :user or t.friend = :user
        """)
    List<Friendship> findByUser(User user);

    /**
     * Retrieves a friendship between two users.
     * 
     * @param u1 the first user
     * @param u2 the second user
     * @return an optional friendship
     */
    @Query("""
            Select t from Friendship t
            inner join User u on t.user.id = u.id
            inner join User v on t.friend.id = v.id
            where (t.user = :u1 and t.friend = :u2) or (t.user = :u2 and t.friend = :u1)
    """)
    Optional<Friendship> findFriendshipBetweenUsers(User u1, User u2);

    /**
     * Retrieves a pending friendship with a user.
     * 
     * @param u1 the first user
     * @param u2 the second user
     * @return an optional pending friendship
     */
    @Query("""
            Select t from Friendship t
            inner join User u on t.user.id = u.id
            inner join User v on t.friend.id = v.id
            where (t.friend = :u1 and t.user = :u2 and t.is_accepted is null)
    """)
    Optional<Friendship> findPendingFriendshipWithUser(User u1, User u2);    
}
