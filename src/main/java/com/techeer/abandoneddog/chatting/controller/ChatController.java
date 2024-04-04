package com.techeer.abandoneddog.chatting.controller;

import com.techeer.abandoneddog.chatting.dto.ChatRoomResponseDto;
import com.techeer.abandoneddog.chatting.dto.MessageResponseDto;
import com.techeer.abandoneddog.chatting.service.ChatService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/chatrooms/participants/{userId}")
    public ResponseEntity<?> getUserChatRooms(@PathVariable Long userId) {
        List<ChatRoomResponseDto> chatRooms = chatService.getChatRoomsByUserId(userId);

        if (chatRooms.isEmpty()) {
            return ResponseEntity.ok("User has no active chat rooms or has left all chat rooms.");
        }

        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/chatrooms/messages/{chatRoomId}")
    public ResponseEntity<?> getMessagesByChatRoomId(@PathVariable Long chatRoomId) {
        try {
            List<MessageResponseDto> messages = chatService.getMessagesByChatRoomId(chatRoomId);

            if (messages.isEmpty()) {
                return ResponseEntity.ok("No messages found in this chat room.");
            }

            return ResponseEntity.ok(messages);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
