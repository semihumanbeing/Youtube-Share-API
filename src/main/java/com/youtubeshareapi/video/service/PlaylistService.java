package com.youtubeshareapi.video.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.model.VideoDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public interface PlaylistService {
    PlaylistDTO getByChatroomId(UUID chatroomId) throws JsonProcessingException;

    PlaylistDTO createPlaylist(PlaylistDTO chatroomId);
    SseEmitter subscribeSSE(UUID chatroomId);
    void sendPlaylistUpdateSSE(UUID chatroomId) throws JsonProcessingException;
}
