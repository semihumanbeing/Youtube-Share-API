package com.youtubeshareapi.chat.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatroomRequest {
  @NotEmpty(message = "chatroom name cannot be empty")
  private String chatroomName;
  private String chatroomPassword;
}
