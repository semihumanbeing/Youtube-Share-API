package com.youtubeshareapi.user.model;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
  private Long userId;
  private String username;
  private String accessToken;
  private String refreshToken;
  private Timestamp createdAt;
}
