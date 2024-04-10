package com.techeer.abandoneddog.chat.repository;

import com.techeer.abandoneddog.chat.dto.ChatMessageInfoDTO;
import com.techeer.abandoneddog.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.techeer.abandoneddog.chat.dto.ChatMessageInfoDTO;
import com.techeer.abandoneddog.chat.entity.ChatMessage;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT cm.message, cm.createdAt,cm.senderId.id FROM ChatMessage cm WHERE cm.chatRoomId.id = :chatRoomId")
    List<Object[]> findMessagesAndCreatedAtAndSenderIdByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}