package com.youtubeshareapi.video.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.youtubeshareapi.video.model.VideoDTO;
import com.youtubeshareapi.video.model.VideoMessage;

import java.util.UUID;

public interface VideoService {
    VideoDTO getNextVideo(VideoMessage videoMessage) throws JsonProcessingException;
    VideoDTO getCurrentVideo(UUID chatroomId) throws JsonProcessingException;
    VideoDTO AddVideo(UUID chatroomId, VideoDTO videoDTO);
    VideoDTO deleteVideo(UUID chatroomId, Long videoId) throws JsonProcessingException;
}
