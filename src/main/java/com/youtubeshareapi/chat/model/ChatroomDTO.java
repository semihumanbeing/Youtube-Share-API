package com.youtubeshareapi.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.user.entity.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomDTO {

    private UUID chatroomId;
    private Long userId;
    private String username;
    @NotEmpty(message = "chatroom name cannot be empty")
    private String chatroomName;
    private String emoji;
    private String chatroomPassword;
    private int userCount;
    private int maxUserCount;
    private boolean hasPwd;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Chatroom toEntity(ChatroomDTO chatroomDTO){
        return Chatroom.builder()
            .chatroomId(chatroomDTO.getChatroomId())
            .user(User.builder()
                .userId(chatroomDTO.getUserId())
                .username(chatroomDTO.getUsername())
                .build())
            .chatroomName(chatroomDTO.getChatroomName())
            .emoji(chatroomDTO.getEmoji())
            .chatroomPassword(chatroomDTO.getChatroomPassword())
            .userCount(chatroomDTO.getUserCount())
            .maxUserCount(chatroomDTO.getMaxUserCount())
            .hasPwd(chatroomDTO.isHasPwd())
            .createdAt(chatroomDTO.getCreatedAt())
            .updatedAt(chatroomDTO.getUpdatedAt())
            .build();
    }


}
