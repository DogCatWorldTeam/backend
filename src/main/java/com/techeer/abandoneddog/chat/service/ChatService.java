package com.techeer.abandoneddog.chat.service;

import com.techeer.abandoneddog.chat.dto.ChatLeaveRoomDto;
import com.techeer.abandoneddog.chat.dto.ChatMessageDTO;
import com.techeer.abandoneddog.chat.dto.ChatMessageInfoDTO;
import com.techeer.abandoneddog.chat.dto.ChatRoomDTO;
import com.techeer.abandoneddog.chat.entity.ChatMessage;
import com.techeer.abandoneddog.chat.entity.ChatRoom;
import com.techeer.abandoneddog.chat.exception.ChatRoomNotFoundException;
import com.techeer.abandoneddog.chat.repository.ChatMessageRepository;
import com.techeer.abandoneddog.chat.repository.ChatRoomRepository;
import com.techeer.abandoneddog.global.exception.user.UserNotFoundException;
import com.techeer.abandoneddog.users.entity.Users;
import com.techeer.abandoneddog.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {


    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatService(SimpMessagingTemplate messagingTemplate, ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }


    // 채팅방 아이디로 채팅 내용 불러오기

    public List<ChatMessageInfoDTO> getChatMessages(Long chatRoomId) {
        List<Object[]> results = chatMessageRepository.findMessagesAndCreatedAtAndSenderIdByChatRoomId(chatRoomId);
        List<ChatMessageInfoDTO> messagesWithDetails = new ArrayList<>();

        for (Object[] result : results) {
            String message = (String) result[0];
            LocalDateTime createdAt = (LocalDateTime) result[1];
            Long senderId = (Long) result[2];

            ChatMessageInfoDTO messageWithDetails = new ChatMessageInfoDTO(message, createdAt, senderId);
            messagesWithDetails.add(messageWithDetails);
        }

        return messagesWithDetails;
    }
//    public List<ChatMessage> getChatMessages(Long chatRoomId) {
//        // 채팅 내용 불러오기
//        return chatMessageRepository.findByChatRoomId(chatRoomId);
//    }

    //userId로 참여되있는 채팅방불러오기
    public List<Long> getChatRoom(Long userId) {

        return chatRoomRepository.findValidChatRoomsByUserId(userId);
    }


    @Transactional
    public void updateChatRoom(ChatLeaveRoomDto chatLeaveRoomDTO) {
        Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

        String out="상대가 채팅을 나갔습니다";
        messagingTemplate.convertAndSend("/topic/" + chatLeaveRoomDTO.getChatRoomId(), out);

        ChatRoom chatRoom = chatRoomRepository.findById(chatLeaveRoomDTO.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        //Users user= userRepository.findById( chatLeaveRoomDTO.getUserId()).orElseThrow(() -> new UserNotFoundException());
        log.info(String.valueOf(chatRoom.getId()));
        //이미 userleft에 존재한다면 에러나도록
        //해당 아이디가 receiver나 sender에 존재하는지 확인하기

        if(chatRoom.getUserLeft() != null){
            chatRoom.setIsDeleted(true);
        }
        else chatRoom.setUserLeft(chatLeaveRoomDTO.getUserId());

//        chatRoom = chatRoom.builder()
//                .userLeft(chatRoom.getUserLeft() == null ? chatLeaveRoomDTO.getUserId() : chatRoom.getUserLeft())
//                .isDeleted(chatRoom.getUserLeft() != null)
//                .build();



        chatRoomRepository.save(chatRoom);
    }
///채팅 보내기 및 저장
    public void sendMessage(ChatMessageDTO chatMessageDTO) {
        messagingTemplate.convertAndSend("/topic/" + chatMessageDTO.getChatRoomId(), chatMessageDTO);

        Users user = userRepository.findById(chatMessageDTO.getSenderId()).orElseThrow(() -> new UserNotFoundException());

        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageDTO.getChatRoomId())
                .orElseThrow(() -> new ChatRoomNotFoundException());
        ChatMessage chatMessage = new ChatMessage(chatRoom,user, chatMessageDTO.getMessage());

        chatMessageRepository.save(chatMessage);
    }
//채팅방 찾기
    public void findRoom(ChatRoomDTO chatRoomDTO) {
          Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
        ChatRoom chatRoom = chatRoomRepository.findBySenderAndReceiverOrReceiverAndSender(chatRoomDTO.getSenderId(), chatRoomDTO.getReceiverId());

        // 이미 채팅방이 존재하는 경우
        if (chatRoom != null) {
            // 채팅방 ID를 클라이언트에게 보내줌
            messagingTemplate.convertAndSend("/topic/chatRoomId/" + chatRoom.getId(), chatRoom.getId());
            return;
        }


        // request로 받은 id로 user 정보 찾아서 chatroom에 저장
        Users user1 = userRepository.findById(chatRoomDTO.getSenderId()).orElseThrow(() -> new UserNotFoundException());
        Users user2 = userRepository.findById(chatRoomDTO.getReceiverId()).orElseThrow(() -> new UserNotFoundException());


        ChatRoom chatRoom1 = new ChatRoom(user1, user2);
        chatRoomRepository.save(chatRoom1);

        messagingTemplate.convertAndSend("/topic/chatRoomId/" + chatRoom1.getId(), "입장합니다:" + user1.getUsername());
    }
}

