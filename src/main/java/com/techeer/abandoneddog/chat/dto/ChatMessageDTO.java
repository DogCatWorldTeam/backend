package com.techeer.abandoneddog.chat.dto;

import lombok.Getter;

@Getter
public class ChatMessageDTO {

    private Long chatRoomId;
    private Long senderId;
    private String message;


}
