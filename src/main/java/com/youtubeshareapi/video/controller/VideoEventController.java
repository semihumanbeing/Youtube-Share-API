package com.youtubeshareapi.video.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.youtubeshareapi.chat.service.RedisPublisher;
import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.model.VideoDTO;
import com.youtubeshareapi.video.model.VideoMessage;
import com.youtubeshareapi.video.service.PlaylistService;
import com.youtubeshareapi.video.service.VideoService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class VideoEventController {
    private final RedisPublisher redisPublisher;
    private final PlaylistService playlistService;
    private final VideoService videoService;
    private static final String VIDEO_PREFIX = "/video/";

    // 현재 곡을 모든 사용자에게 전달
    @MessageMapping("/video/current")
    public void getCurrentVideo(VideoMessage videoMessage) throws IOException {
        VideoDTO currentVideo = videoService.getCurrentVideo(videoMessage.getChatroomId());
        if (currentVideo != null) {
            redisPublisher.publishVideo(new ChannelTopic(getVideoPrefix(videoMessage.getChatroomId())), currentVideo);
        }
        playlistService.sendPlaylistUpdateSSE(videoMessage.getChatroomId());
    }

    // 다음 곡을 모든 사용자에게 전달
    @MessageMapping("/video/next")
    public void getNextVideo(VideoMessage videoMessage) throws IOException {
        // 레디스에서 현재 곡을 rightpop
        VideoDTO nextVideo = videoService.getNextVideo(videoMessage);
        // 웹소켓으로 다음 곡을 전달
        redisPublisher.publishVideo(new ChannelTopic(getVideoPrefix(videoMessage.getChatroomId())),
                                                     Objects.requireNonNullElseGet(nextVideo, VideoDTO::new));
        playlistService.sendPlaylistUpdateSSE(videoMessage.getChatroomId());

        // 비디오를 전달했다면 전달한 시간을 db에 저장한다
        // 새로운 사람이 들어온다면 시작 시간은 현재 시간에서 전달한 시간을 뺀 만큼에서 시작한다.

    }
    private String getVideoPrefix(UUID chatroomId) {
        return String.format("%s%s", VIDEO_PREFIX, chatroomId);
    }
}
