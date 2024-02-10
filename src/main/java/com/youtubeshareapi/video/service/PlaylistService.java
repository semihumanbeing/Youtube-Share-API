package com.youtubeshareapi.video.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.model.VideoDTO;

import java.util.UUID;

public interface PlaylistService {
    VideoDTO AddVideo(UUID chatroomId, VideoDTO videoDTO);
    PlaylistDTO getByChatroomId(UUID chatroomId) throws JsonProcessingException;

    PlaylistDTO createPlaylist(PlaylistDTO chatroomId);
}
