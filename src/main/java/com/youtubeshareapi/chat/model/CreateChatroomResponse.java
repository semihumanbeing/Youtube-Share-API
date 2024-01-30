package com.youtubeshareapi.chat.model;

import jakarta.persistence.Column;
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
public class CreateChatroomResponse {

  private UUID chatroomId;
  private Long userId;
  private String chatroomName;
  private String emoji;
  private String chatroomPassword;
  private int userCount;
  private int maxUserCount;
  private boolean hasPwd;
  private Timestamp createdAt;

}
