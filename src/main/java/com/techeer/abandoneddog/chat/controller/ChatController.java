package com.techeer.abandoneddog.chat.controller;

import com.techeer.abandoneddog.chat.dto.ChatLeaveRoomDto;
import com.techeer.abandoneddog.chat.dto.ChatMessageDTO;
import com.techeer.abandoneddog.chat.dto.ChatRoomDTO;
import com.techeer.abandoneddog.chat.entity.ChatMessage;
import com.techeer.abandoneddog.chat.entity.ChatRoom;
import com.techeer.abandoneddog.chat.repository.ChatMessageRepository;
import com.techeer.abandoneddog.chat.repository.ChatRoomRepository;
import com.techeer.abandoneddog.chat.service.ChatService;
import com.techeer.abandoneddog.users.dto.ResultDto;
import com.techeer.abandoneddog.users.entity.Users;
import com.techeer.abandoneddog.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
public class ChatController {


    private final ChatService chatService;
   // private final SimpMessagingTemplate messagingTemplate;


    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @MessageMapping("/chat/sendMessage")
    public void sendMessage( @Payload ChatMessageDTO chatMessageDTO) {

        chatService.sendMessage(chatMessageDTO);
        // 채팅 메시지 저장

    }

    @MessageMapping("/chat/findRoom")
    public void addUser(@Payload ChatRoomDTO chatRoomDTO) {

        chatService.findRoom(chatRoomDTO);
    }


    // 채팅 내용 불러오기 API
    @GetMapping("/getMessages")
    public ResponseEntity<?> getChatMessages(@RequestParam("chatRoomId") Long chatRoomId) {

        return ResponseEntity.ok(ResultDto.res(HttpStatus.OK, "성공", chatService.getChatMessages(chatRoomId)));
    }



    // 채팅방 나가기 APi
    @MessageMapping("/chat/leaveRoom")
    public void leaveChatRoom( @Payload ChatLeaveRoomDto chatLeaveRoomDTO) {
        chatService.updateChatRoom(chatLeaveRoomDTO);
        // 채팅 메시지 저장

    }

    @GetMapping("/getChatRoom")
    public ResponseEntity<?> getChatRoom(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(ResultDto.res(HttpStatus.OK, "성공",  chatService.getChatRoom(userId)));
    }


}

