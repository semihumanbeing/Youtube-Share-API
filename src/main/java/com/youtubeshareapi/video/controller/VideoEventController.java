package com.youtubeshareapi.video.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtubeshareapi.chat.service.RedisPublisher;
import com.youtubeshareapi.video.model.VideoDTO;
import com.youtubeshareapi.video.model.VideoMessage;
import com.youtubeshareapi.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoEventController {
    private final RedisPublisher redisPublisher;
    private final VideoService videoService;
    private static final String VIDEO_PREFIX = "/video/";

    // 처음 재생하기를 눌렀을 때
    @MessageMapping("/current")
    public void getCurrentVideo(VideoMessage videoMessage) throws JsonProcessingException {
        VideoDTO currentVideo = videoService.getCurrentVideo(videoMessage.getChatroomId());
        if (currentVideo != null) {
            redisPublisher.publishVideo(new ChannelTopic(getVideoPrefix(videoMessage.getChatroomId())), currentVideo);
        }
    }

    @MessageMapping("/next")
    public void getNextVideo(VideoMessage videoMessage) throws JsonProcessingException {
        // 레디스에서 현재 곡을 rightpop
        VideoDTO nextVideo = videoService.getNextVideo(videoMessage);
        // 웹소켓으로 다음 곡을 전달
        if (nextVideo != null) {
            redisPublisher.publishVideo(new ChannelTopic(getVideoPrefix(videoMessage.getChatroomId())), nextVideo);

        }
    }
    private String getVideoPrefix(UUID chatroomId) {
        return String.format("%s%s", VIDEO_PREFIX, chatroomId);
    }

}
