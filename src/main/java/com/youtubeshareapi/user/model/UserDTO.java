package com.youtubeshareapi.user.model;


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

  private UUID userId;
  private String username;
  private String password;
  private String email;
  private Timestamp createdAt;
  private Timestamp updatedAt;

}
