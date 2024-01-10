package com.youtubeshareapi.user.model;

import com.youtubeshareapi.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
  private Long userId;
  private String accessToken;
  private String refreshToken;
  private Timestamp createdAt;

}
