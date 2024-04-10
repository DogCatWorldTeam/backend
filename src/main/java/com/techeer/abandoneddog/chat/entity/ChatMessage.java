package com.techeer.abandoneddog.chat.entity;

import com.techeer.abandoneddog.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


//    @Column
//    private MessageType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoomId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Users senderId;

    @Column(nullable = false)
    private String message;

    @CreatedDate
    private LocalDateTime createdAt;

    public ChatMessage(ChatRoom chatRoomId, Users senderId, String message) {
        this.chatRoomId = chatRoomId;
    //    this.type=type;
        this.senderId = senderId;
        this.message = message;
    }
}