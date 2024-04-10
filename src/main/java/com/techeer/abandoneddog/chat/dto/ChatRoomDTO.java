package com.techeer.abandoneddog.chat.dto;

import com.techeer.abandoneddog.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {

   // private Long id;
    private Long senderId;
    private Long receiverId;


}