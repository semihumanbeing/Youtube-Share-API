package com.youtubeshareapi.video.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.youtubeshareapi.video.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {

    private Long videoId;
    private Long playlistId;
    private Long userId;
    private String url;
    private String title;
    private String artist;
    @JsonProperty("isCurrent")
    private boolean isCurrent;

    public VideoDTO(Long videoId, Long userId, String url, String title, String artist, Boolean isCurrent) {
        this.videoId = videoId;
        this.userId = userId;
        this.url = url;
        this.title = title;
        this.artist = artist;
        this.isCurrent = isCurrent;
    }

    public static VideoDTO of(Video video) {
        return VideoDTO.builder()
                .videoId(video.getVideoId())
                .playlistId(video.getPlaylist().getPlaylistId())
                .userId(video.getUserId())
                .url(video.getUrl())
                .title(video.getTitle())
                .artist(video.getArtist())
                .isCurrent(video.isCurrent())
                .build();
    }

}
