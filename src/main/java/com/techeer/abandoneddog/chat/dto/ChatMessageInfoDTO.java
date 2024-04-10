package com.techeer.abandoneddog.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ChatMessageInfoDTO {

    private String message;
    private LocalDateTime createdAt;
    private Long senderId;

    public ChatMessageInfoDTO(String message, LocalDateTime createdAt, Long senderId) {
        this.message = message;
        this.createdAt = createdAt;
        this.senderId = senderId;
    }

}