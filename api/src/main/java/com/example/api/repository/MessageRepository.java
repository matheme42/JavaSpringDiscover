package com.example.api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.api.model.database.Friendship;
import com.example.api.model.database.Message;
import com.example.api.model.database.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("""
            Select t from Message t
            inner join User u on t.user.id = u.id
            where t.user = :user
    """)
    List<Message> findByUser(User user);

    
    @Query("""
            Select t from Message t
            inner join Friendship u on t.friendship.id = u.id
            where t.friendship = :friendship
    """)
    List<Message> findByFriendship(Friendship friendship);
}
