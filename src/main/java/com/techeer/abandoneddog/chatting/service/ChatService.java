package com.techeer.abandoneddog.chatting.service;


import com.techeer.abandoneddog.chatting.domain.ChatRoom;
import com.techeer.abandoneddog.chatting.domain.Message;
import com.techeer.abandoneddog.chatting.domain.MessageType;
import com.techeer.abandoneddog.chatting.domain.UsersChatRoom;
import com.techeer.abandoneddog.chatting.dto.ChatRoomResponseDto;
import com.techeer.abandoneddog.chatting.dto.MessageResponseDto;
import com.techeer.abandoneddog.chatting.dto.SimplifiedMessageDto;
import com.techeer.abandoneddog.chatting.repository.ChatRoomRepository;
import com.techeer.abandoneddog.chatting.repository.MessageRepository;
import com.techeer.abandoneddog.chatting.repository.UsersChatRoomRepository;
import com.techeer.abandoneddog.users.entity.Users;
import com.techeer.abandoneddog.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository usersRepository;
    private final UsersChatRoomRepository usersChatRoomRepository;

    // 사용자 ID를 기반으로 활성 채팅방 목록을 조회
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getChatRoomsByUserId(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<ChatRoom> chatRooms = usersChatRoomRepository.findActiveChatRoomsByUserId(userId);

        if (chatRooms.isEmpty()) {
            return Collections.emptyList();
        }

        return chatRooms.stream()
                .map(ChatRoomResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 채팅방의 모든 메시지를 시간순으로 조회
    @Transactional(readOnly = true)
    public List<MessageResponseDto> getMessagesByChatRoomId(Long chatRoomId) {
        chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id: " + chatRoomId));

        List<Message> messages = messageRepository.findMessagesByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        return messages.stream()
                .map(message -> new MessageResponseDto(
                        message.getMessageId(),
                        message.getSender().getId(),
                        message.getMessage(),
                        message.getType(),
                        message.getSender().getUsername(),
                        message.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 채팅방 입장 처리
    @Transactional
    public void enterChatRoom(SimplifiedMessageDto messageDto) {
        try {
            Users sender = usersRepository.findById(messageDto.getSenderId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + messageDto.getSenderId()));
            Users receiver = usersRepository.findById(messageDto.getReceiverId())
                    .orElseThrow(() -> new EntityNotFoundException("Receiver not found with id: " + messageDto.getReceiverId()));

            // 시도: 채팅방 생성 또는 기존 채팅방 확인
            ChatRoom chatRoom = createRoom(messageDto.getChatRoomName(), messageDto.getSenderId(), messageDto.getReceiverId());

            // UsersChatRoom 관계 설정
            UsersChatRoom usersChatRoom = usersChatRoomRepository
                    .findByUserIdAndChatRoomId(sender.getId(), chatRoom.getChatRoomId())
                    .orElseGet(() -> new UsersChatRoom());

            usersChatRoom.setChatRoom(chatRoom);
            usersChatRoom.setUser(sender);
            if (usersChatRoom.getJoinedAt() == null) { // 새로운 입장이라면
                usersChatRoom.setJoinedAt(LocalDateTime.now());
            }
            usersChatRoom.setLeftAt(null); // 입장 상태 채팅방 나가지 않았음을 의미
            usersChatRoomRepository.save(usersChatRoom);

            // 새로운 채팅방이 성공적으로 생성되었거나 기존 채팅방에 재입장한 경우 입장 메시지와 추가 메시지를 저장
            saveAndSendMessage(chatRoom, sender, "님이 입장했습니다.", MessageType.ENTER, true);

            if (messageDto.getMessage() != null && !messageDto.getMessage().trim().isEmpty()) {
                saveAndSendMessage(chatRoom, sender, messageDto.getMessage(), MessageType.TALK, false);
            }
        } catch (IllegalStateException e) {
            // 이미 존재하는 채팅방을 다시 만들려고 할 때의 예외 처리
            log.error("Chat room already exists: {}", e.getMessage(), e);
        }
    }

    // 새 채팅방 생성 또는 기존 채팅방 가져오기
    @Transactional
    public ChatRoom createRoom(String name, Long senderId, Long receiverId) throws IllegalStateException {
        Users sender = usersRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found with id: " + senderId));
        Users receiver = usersRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found with id: " + receiverId));

        // 같은 채팅방 확인 (송신자와 수신자 역할 포함)
        boolean roomExists = chatRoomRepository.findBySenderAndReceiver(sender, receiver).isPresent() ||
                chatRoomRepository.findByReceiverAndSender(sender, receiver).isPresent();

        if(roomExists) {
            // 이미 존재하는 채팅방, 새로 생성되지 않음. 예외 던짐
            throw new IllegalStateException("Chat room already exists between sender and receiver");
        } else {
            // 새로운 채팅방 생성
            ChatRoom chatRoom = ChatRoom.builder()
                    .name(name)
                    .receiver(receiver)
                    .sender(sender)
                    .build();
            return chatRoomRepository.save(chatRoom);
        }
    }

    // 채팅방 나가기 처리
    @Transactional
    public void leaveChatRoom(Long userId, Long chatRoomId) {
        // 사용자와 채팅방 간의 관계를 찾습니다.
        UsersChatRoom usersChatRoom = usersChatRoomRepository.findByUserIdAndChatRoomId(userId, chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Membership not found between user and chat room"));

        // 사용자가 채팅방에서 나갔음을 표시하기 위해 left_at 시간을 현재 시간으로 설정
        usersChatRoom.setLeftAt(LocalDateTime.now());
        usersChatRoomRepository.save(usersChatRoom);

        // 사용자가 채팅방에서 나갔음을 알리는 메시지를 생성하고 저장
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id: " + chatRoomId));
        Message leaveMessage = Message.builder()
                .chatRoom(chatRoom)
                .sender(user)
                .message(user.getUsername() + "님이 채팅방을 떠났습니다.")
                .type(MessageType.LEAVE)
                .build();
        messageRepository.save(leaveMessage);

        // 채팅방에 남은 사용자들에게 알림을 보냄
        messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoomId,
                new MessageResponseDto(
                        leaveMessage.getMessageId(),
                        leaveMessage.getSender().getId(),
                        leaveMessage.getMessage(),
                        leaveMessage.getType(),
                        user.getUsername(),
                        leaveMessage.getCreatedAt()
                ));
    }


    // 채팅방에 메시지 전송 처리
    @Transactional
    public void sendMessageToChatRoom(SimplifiedMessageDto messageDto) {
        Users sender = usersRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getChatRoomId())
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found"));

        // 채팅방과 사용자 간의 관계를 확인하고, 없으면 생성
        UsersChatRoom usersChatRoom = usersChatRoomRepository
                .findByUserIdAndChatRoomId(sender.getId(), chatRoom.getChatRoomId())
                .orElseGet(() -> {
                    UsersChatRoom newUsersChatRoom = new UsersChatRoom();
                    newUsersChatRoom.setChatRoom(chatRoom);
                    newUsersChatRoom.setUser(sender);
                    newUsersChatRoom.setJoinedAt(LocalDateTime.now()); // 참여 시간 설정
                    // 여기서 입장 메시지를 자동으로 보냄
                    sendEntranceMessage(chatRoom, sender);
                    return usersChatRoomRepository.save(newUsersChatRoom);
                });

        // 메시지가 입장 메시지인 경우 이미 처리했으므로 생략, 그 외 메시지를 처리
        if (messageDto.getType() != MessageType.ENTER) {
            saveAndSendMessage(chatRoom, sender, messageDto.getMessage(), messageDto.getType(), false);
        }
    }


    // 입장 메시지 전송
    private void sendEntranceMessage(ChatRoom chatRoom, Users sender) {
        String entranceMessage = sender.getUsername() + "님이 입장했습니다.";
        saveAndSendMessage(chatRoom, sender, entranceMessage, MessageType.ENTER, false);
    }


    // 메시지 저장 및 전송
    private void saveAndSendMessage(ChatRoom chatRoom, Users sender, String messageContent, MessageType messageType, boolean includeUsername) {
        String finalMessageContent = includeUsername ? sender.getUsername() + " " + messageContent : messageContent;
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(finalMessageContent)
                .type(messageType)
                .build();
        messageRepository.save(message);

        MessageResponseDto responseDto = new MessageResponseDto(
                message.getMessageId(),
                message.getSender().getId(),
                message.getMessage(),
                message.getType(),
                sender.getUsername(),
                message.getCreatedAt()
        );
        messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoom.getChatRoomId(), responseDto);
    }
}

