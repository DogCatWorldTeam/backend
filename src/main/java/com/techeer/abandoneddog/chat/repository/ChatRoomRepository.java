package com.techeer.abandoneddog.chat.repository;

import com.techeer.abandoneddog.chat.entity.ChatRoom;
import com.techeer.abandoneddog.users.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.sender.id = :senderId AND cr.receiver.id = :receiverId) OR " +
            "(cr.sender.id = :receiverId AND cr.receiver.id = :senderId)")
    ChatRoom findBySenderAndReceiverOrReceiverAndSender(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
    @Query("SELECT cr.id FROM ChatRoom cr WHERE (cr.sender.id = :userId OR cr.receiver.id = :userId) " +
            "AND (cr.userLeft IS NULL OR cr.userLeft != :userId) " +
            "AND cr.isDeleted = false")
    List<Long> findValidChatRoomsByUserId(@Param("userId") Long userId);

//    @Query(value = "SELECT * FROM chat_room " +
//            "WHERE (sender.id = :userId OR receiver.id = :userId) " +
//            "AND (user_left IS NULL OR user_left != :userId) " +
//            "AND is_deleted = false", nativeQuery = true)
//    List<ChatRoom> findValidChatRoomsByUserId(@Param("userId") Long userId);
}
