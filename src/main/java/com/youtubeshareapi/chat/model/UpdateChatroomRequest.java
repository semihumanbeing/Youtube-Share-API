package com.youtubeshareapi.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChatroomRequest {

  private String chatroomName;
  private String chatroomPassword;
}
