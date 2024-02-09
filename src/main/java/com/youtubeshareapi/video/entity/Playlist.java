package com.youtubeshareapi.video.entity;

import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.user.entity.User;
import com.youtubeshareapi.video.model.PlaylistDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlist")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Long playlistId;
    @OneToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroomId;
    @Column(name = "is_active")
    private boolean isActive;
    @Column(name = "playlist_name", nullable = false)
    private String playlistName;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public static Playlist of(PlaylistDTO playlistDTO) {
        return Playlist.builder()
                .playlistId(playlistDTO.getPlaylistId())
                .chatroomId(Chatroom.builder().chatroomId(playlistDTO.getChatroomId()).build())
                .build();
    }

}
