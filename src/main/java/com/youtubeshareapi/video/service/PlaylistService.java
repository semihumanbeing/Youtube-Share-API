package com.youtubeshareapi.video.service;

import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.model.VideoDTO;

import java.util.UUID;

public interface PlaylistService {
    VideoDTO AddVideo(UUID chatroomId, VideoDTO videoDTO);
    PlaylistDTO getByChatroomId(UUID chatroomId);

    PlaylistDTO createPlaylist(PlaylistDTO chatroomId);
}
