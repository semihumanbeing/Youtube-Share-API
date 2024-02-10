package com.youtubeshareapi.video.entity;

import com.youtubeshareapi.video.model.VideoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "video")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long videoId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "url", nullable = false, length = 1000)
    private String url;
    @Column(name = "title", length = 200)
    private String title;
    @Column(name = "artist", length = 200)
    private String artist;
    @Column(name = "is_current")
    @ColumnDefault("0")
    private boolean isCurrent;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public static Video of(VideoDTO videoDTO) {
        return Video.builder()
                .userId(videoDTO.getUserId())
                .playlist(Playlist.builder().playlistId(videoDTO.getPlaylistId()).build())
                .url(videoDTO.getUrl())
                .title(videoDTO.getTitle())
                .artist(videoDTO.getArtist())
                .isCurrent(videoDTO.isCurrent())
                .build();
    }


}
