package com.techeer.abandoneddog.chat.exception;

public class ChatRoomNotFoundException extends RuntimeException {

    public ChatRoomNotFoundException() {
        super("Chat room not found");
    }
}