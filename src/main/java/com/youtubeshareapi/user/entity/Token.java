package com.youtubeshareapi.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token")
public class Token {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "token_id")
  private Long tokenId;
  @OneToOne
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private User user;
  @Column(name = "access_token")
  @Setter
  private String accessToken;
  @Column(name = "refresh_token")
  @Setter
  private String refreshToken;
  @CreationTimestamp
  @Column(name = "created_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Timestamp createdAt;

}
