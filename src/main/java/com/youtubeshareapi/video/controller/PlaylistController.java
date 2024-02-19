package com.youtubeshareapi.video.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtubeshareapi.common.ResponseDTO;
import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@RestController
@RequestMapping("/api/playlist")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;
    private static final String PLAYLIST_PREFIX = "/playlist/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("")
    public ResponseEntity<?> createPlaylist(@RequestBody PlaylistDTO playlistDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .data(playlistService.createPlaylist(playlistDTO))
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .build());
    }

    @GetMapping("/{chatroomId}")
    public ResponseEntity<?> getPlaylistById(@PathVariable(name = "chatroomId") String chatroomIdStr) throws JsonProcessingException {
        // 채팅방아이디를 가지고 activate 된 플레이리스트를 레디스에서 검색한다
        UUID chatroomId = UUID.fromString(chatroomIdStr);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .data(playlistService.getByChatroomId(chatroomId))
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .build());
    }


    @GetMapping("/sse/{chatroomId}")
    public SseEmitter subscribeSSE(@PathVariable(name = "chatroomId") String chatroomIdStr)
        throws IOException {
        UUID chatroomId = UUID.fromString(chatroomIdStr);
        return playlistService.subscribeSSE(chatroomId);
    }

    private String getPlaylistPrefix(UUID chatroomId) {
        return String.format("%s%s", PLAYLIST_PREFIX, chatroomId);
    }
}
