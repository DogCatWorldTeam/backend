package com.techeer.abandoneddog.chatting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.techeer.abandoneddog.chatting.domain.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

	@Query("SELECT m FROM Message m WHERE m.chatRoom.chatRoomId = :chatRoomId ORDER BY m.createdAt ASC")

	List<Message> findMessagesByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);
}
