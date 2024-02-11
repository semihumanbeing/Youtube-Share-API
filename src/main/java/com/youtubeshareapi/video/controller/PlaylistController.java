package com.youtubeshareapi.video.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.youtubeshareapi.common.CookieUtil;
import com.youtubeshareapi.common.ResponseDTO;
import com.youtubeshareapi.security.JwtTokenProvider;
import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.model.VideoDTO;
import com.youtubeshareapi.video.model.VideoRequest;
import com.youtubeshareapi.video.service.PlaylistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.UUID;

@RestController
@RequestMapping("/api/playlist")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;

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



}
