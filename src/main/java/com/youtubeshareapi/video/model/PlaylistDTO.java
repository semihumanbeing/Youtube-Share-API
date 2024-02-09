package com.youtubeshareapi.video.model;

import com.youtubeshareapi.video.entity.Playlist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDTO {

    private Long playlistId;
    private UUID chatroomId;
    private String playlistName;
    private boolean isActive;
    private List<VideoDTO> videos;

    public static PlaylistDTO of(Playlist savedPlaylist) {
        return PlaylistDTO.builder()
                .playlistId(savedPlaylist.getPlaylistId())
                .chatroomId(savedPlaylist.getChatroomId().getChatroomId())
                .playlistName(savedPlaylist.getPlaylistName())
                .isActive(savedPlaylist.isActive())
                .build();
    }

    public PlaylistDTO(Long playlistId, UUID chatroomId, String playlistName, boolean isActive) {
        this.playlistId = playlistId;
        this.chatroomId = chatroomId;
        this.playlistName = playlistName;
        this.isActive = isActive;
    }
}
