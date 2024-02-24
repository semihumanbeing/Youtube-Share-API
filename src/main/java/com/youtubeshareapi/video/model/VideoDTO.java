package com.youtubeshareapi.video.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.youtubeshareapi.video.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {

    private Long videoId;
    private Long playlistId;
    private Long userId;
    private String username;
    private String url;
    private String title;
    private String artist;
    private String thumbnailImg;
    private int thumbnailWidth;
    private int thumbnailHeight;
    @JsonProperty("isCurrent")
    private boolean isCurrent;
    private Timestamp playedAt;

    public VideoDTO(Long videoId, Long userId, String url, String title, String artist, Boolean isCurrent, Timestamp playedAt, String thumbnailImg, int thumbnailWidth, int thumbnailHeight) {
        this.videoId = videoId;
        this.userId = userId;
        this.url = url;
        this.title = title;
        this.artist = artist;
        this.isCurrent = isCurrent;
        this.playedAt = playedAt;
        this.thumbnailImg = thumbnailImg;
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
    }

    public static VideoDTO of(Video video) {
        return VideoDTO.builder()
                .videoId(video.getVideoId())
                .playlistId(video.getPlaylist().getPlaylistId())
                .userId(video.getUserId())
                .username(video.getUsername())
                .url(video.getUrl())
                .title(video.getTitle())
                .artist(video.getArtist())
                .isCurrent(video.isCurrent())
                .playedAt(video.getPlayedAt())
                .thumbnailImg(video.getThumbnailImg())
                .thumbnailWidth(video.getThumbnailWidth())
                .thumbnailHeight(video.getThumbnailHeight())
                .build();
    }

}
