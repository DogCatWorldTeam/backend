package com.techeer.abandoneddog.chatting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techeer.abandoneddog.chatting.domain.ChatRoom;
import com.techeer.abandoneddog.users.entity.Users;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	Optional<ChatRoom> findBySenderAndReceiver(Users sender, Users receiver);

	Optional<ChatRoom> findByReceiverAndSender(Users receiver, Users sender);

}
