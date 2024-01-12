package com.youtubeshareapi.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.user.entity.User;
import jakarta.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomDTO {

    private Long chatroomId;
    @JsonIgnore
    private User user;
    @NotEmpty(message = "chatroom name cannot be empty")
    private String chatroomName;
    private String chatroomPassword;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Chatroom toEntity(ChatroomDTO chatroomDTO){
        return Chatroom.builder()
            .chatroomId(chatroomDTO.chatroomId)
            .user(user)
            .chatroomName(chatroomDTO.chatroomName)
            .chatroomPassword(chatroomDTO.chatroomPassword)
            .createdAt(chatroomDTO.createdAt)
            .updatedAt(chatroomDTO.updatedAt)
            .build();
    }


}
