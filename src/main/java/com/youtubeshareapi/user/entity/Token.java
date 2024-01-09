package com.youtubeshareapi.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token")
public class Token {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "token_id")
  private Long tokenId;
  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;
  @Column(name = "access_token")
  private String accessToken;
  @Column(name = "refresh_token")
  private String refreshToken;
  @Column(name = "created_at")
  private Timestamp createdAt;

}
