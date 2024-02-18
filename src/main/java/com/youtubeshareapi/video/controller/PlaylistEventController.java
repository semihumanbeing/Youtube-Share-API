package com.youtubeshareapi.video.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.youtubeshareapi.chat.service.RedisPublisher;
import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.model.VideoMessage;
import com.youtubeshareapi.video.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;
@Slf4j
@Controller
@RequiredArgsConstructor
public class PlaylistEventController {
    private final PlaylistService playlistService;
    private final RedisPublisher redisPublisher;
    private static final String PLAYLIST_PREFIX = "/playlist/";

    @MessageMapping("/playlist")
    public void getPlaylistById(VideoMessage videoMessage) throws JsonProcessingException {
        // 채팅방아이디를 가지고 activate 된 플레이리스트를 레디스에서 검색한다
        PlaylistDTO playlistDTO = playlistService.getByChatroomId(videoMessage.getChatroomId());
        if (playlistDTO != null) {
            redisPublisher.publishPlaylist(new ChannelTopic(getPlaylistPrefix(videoMessage.getChatroomId())), playlistDTO);
        }
    }
    private String getPlaylistPrefix(UUID chatroomId) {
        return String.format("%s%s", PLAYLIST_PREFIX, chatroomId);
    }


}
