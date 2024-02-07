package com.youtubeshareapi.video.entity;

import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "playlist")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Long playlistId;

    @OneToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "artist", length = 200)
    private String artist;

    @Column(name = "is_current")
    private Boolean isCurrent;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
