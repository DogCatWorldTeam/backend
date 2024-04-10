package com.techeer.abandoneddog.users.entity;

import com.techeer.abandoneddog.chat.entity.ChatRoom;
import com.techeer.abandoneddog.global.entity.BaseEntity;
import com.techeer.abandoneddog.users.dto.UserRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE user SET deleted = true WHERE user_id = ?")
@Where(clause = "deleted = false")
@Table(name = "user")
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone_num", nullable = false)
    private String phoneNum;

    @OneToMany(mappedBy = "sender")
    private List<ChatRoom> sentChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    private List<ChatRoom> receivedChatRooms = new ArrayList<>();

    public void update(UserRequestDto dto, PasswordEncoder passwordEncoder) {
        this.username = dto.getUsername();
        this.password = passwordEncoder.encode(dto.getPassword());
        this.email = dto.getEmail();
        this.phoneNum = dto.getPhoneNum();
    }
}

