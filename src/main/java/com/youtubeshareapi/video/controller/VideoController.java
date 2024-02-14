package com.youtubeshareapi.video.controller;

import com.youtubeshareapi.common.CookieUtil;
import com.youtubeshareapi.common.ResponseDTO;
import com.youtubeshareapi.security.JwtTokenProvider;
import com.youtubeshareapi.video.model.VideoDTO;
import com.youtubeshareapi.video.model.VideoRequest;
import com.youtubeshareapi.video.service.PlaylistService;
import com.youtubeshareapi.video.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
public class VideoController {
    private final JwtTokenProvider tokenProvider;
    private final VideoService videoService;
    @PostMapping("/{chatroomId}/{playlistId}")
    public ResponseEntity<?> addVideoToPlaylist(@PathVariable(name = "chatroomId") String chatroomIdStr,
                                                @PathVariable(name = "playlistId") Long playlistId,
                                                @RequestBody VideoRequest videoRequest,
                                                HttpServletRequest request) {
        String token = CookieUtil.resolveToken(request);
        Long userId = getUserIdFromToken(token);
        UUID chatroomId = UUID.fromString(chatroomIdStr);

        VideoDTO videoDTO = VideoDTO.builder()
                .playlistId(playlistId)
                .userId(userId)
                .url(videoRequest.getUrl())
                .artist(videoRequest.getArtist())
                .title(videoRequest.getTitle())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .data(videoService.AddVideo(chatroomId, videoDTO))
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .build());
    }
    @DeleteMapping("/{chatroomId}/{playlistId}/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable(name = "chatroomId") String chatroomId,
                                         @PathVariable(name = "playlistId") Long playlistId,
                                         @PathVariable(name = "videoId") Long videoId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .data(videoService.deleteVideo(chatroomId, videoId))
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .build());
    }
    private Long getUserIdFromToken(String token){
        return Long.parseLong(tokenProvider.parseClaims(token).getSubject());
    }
}
