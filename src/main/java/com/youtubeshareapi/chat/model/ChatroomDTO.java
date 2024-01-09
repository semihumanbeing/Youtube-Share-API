package com.youtubeshareapi.chat.model;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomDTO {

    private String roomId;
    private String roomName;
    private String userId;
    private String roomPassword;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
