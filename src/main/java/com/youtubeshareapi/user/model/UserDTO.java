package com.youtubeshareapi.user.model;


import com.youtubeshareapi.user.entity.User;
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
public class UserDTO {

  private Long userId;
  private String username;
  private String password;
  private String email;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  public static UserDTO of(User user) {
    return UserDTO.builder()
        .userId(user.getUserId())
        .username(user.getUsername())
        .password(user.getPassword())
        .email(user.getEmail())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
