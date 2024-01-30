package com.youtubeshareapi.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youtubeshareapi.chat.model.ChatroomDTO;
import com.youtubeshareapi.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.id.uuid.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chatroom")
public class Chatroom {
  @Id
  @Column(name = "chatroom_id")
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private UUID chatroomId;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  @Column(name = "emoji")
  private String emoji;
  @Column(name = "chatroom_name", nullable = false)
  private String chatroomName;
  @Column(name = "chatroom_password")
  private String chatroomPassword;
  @Setter
  @Column(name = "user_count")
  private int userCount;
  @Column(name = "max_user_count")
  private int maxUserCount;
  @Column(name = "has_pwd")
  private boolean hasPwd;
  @CreationTimestamp
  @Column(name = "created_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Timestamp createdAt;
  @UpdateTimestamp
  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Timestamp updatedAt;

  public static ChatroomDTO toDTO(Chatroom chatroom) {
    return ChatroomDTO.builder()
        .chatroomId(chatroom.getChatroomId())
        .userId(chatroom.getUser().getUserId())
        .username(chatroom.getUser().getUsername())
        .chatroomName(chatroom.getChatroomName())
        .emoji(chatroom.getEmoji())
        .chatroomPassword(chatroom.getChatroomPassword())
        .userCount(chatroom.getUserCount())
        .maxUserCount(chatroom.getMaxUserCount())
        .hasPwd(chatroom.isHasPwd())
        .createdAt(chatroom.getCreatedAt())
        .updatedAt(chatroom.getUpdatedAt())
        .build();
  }
}
