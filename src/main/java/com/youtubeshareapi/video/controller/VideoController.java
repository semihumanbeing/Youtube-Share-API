package com.youtubeshareapi.video.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.youtubeshareapi.chat.service.RedisPublisher;
import com.youtubeshareapi.common.CookieUtil;
import com.youtubeshareapi.common.ResponseDTO;
import com.youtubeshareapi.security.JwtTokenProvider;
import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.model.VideoDTO;
import com.youtubeshareapi.video.model.VideoRequest;
import com.youtubeshareapi.video.service.PlaylistService;
import com.youtubeshareapi.video.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.sql.Timestamp;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
public class VideoController {
    private final JwtTokenProvider tokenProvider;
    private final VideoService videoService;
    private final PlaylistService playlistService;
    @Value("${google-api.key}")
    public String googleApiKey;

    // 유투브에서 검색 결과를 가져오는 api
    @GetMapping("/youtube")
    public ResponseEntity<?> getYoutubeSearchResult(@RequestParam(name = "search") String searchParam) {

        String uri = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&type=video&videoEmbeddable=true&maxResults=30&q=${searchParam}&key=${apiKey}"
                .replace("${searchParam}", searchParam)
                .replace("${apiKey}", googleApiKey);

        RestClient restClient = RestClient.create();
        String youtubeSearchResult = restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);

        return ResponseEntity.ok(youtubeSearchResult);
    }

    @PostMapping("/{chatroomId}/{playlistId}")
    public ResponseEntity<?> addVideoToPlaylist(@PathVariable(name = "chatroomId") String chatroomIdStr,
                                                @PathVariable(name = "playlistId") Long playlistId,
                                                @RequestBody VideoRequest videoRequest,
                                                HttpServletRequest request) throws JsonProcessingException {
        String token = CookieUtil.resolveToken(request);
        Long userId = getUserIdFromToken(token);
        UUID chatroomId = UUID.fromString(chatroomIdStr);

        VideoDTO videoDTO = VideoDTO.builder()
                .playlistId(playlistId)
                    .userId(userId)
                    .username(videoRequest.getUsername())
                    .url(videoRequest.getUrl())
                    .artist(videoRequest.getArtist())
                    .title(videoRequest.getTitle())
                    .thumbnailImg(videoRequest.getThumbnailImg())
                    .thumbnailWidth(videoRequest.getThumbnailWidth())
                    .thumbnailHeight(videoRequest.getThumbnailHeight())
                    .build();
        videoService.AddVideo(chatroomId, videoDTO);
        playlistService.sendPlaylistUpdateSSE(chatroomId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.builder()
                        .data(videoDTO)
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .build());
    }
    @DeleteMapping("/{chatroomId}/{playlistId}/{videoId}")
    public ResponseEntity<?> deleteVideoFromPlaylist(@PathVariable(name = "chatroomId") String chatroomIdStr,
                                         @PathVariable(name = "playlistId") Long playlistId,
                                         @PathVariable(name = "videoId") Long videoId)
        throws JsonProcessingException {
        UUID chatroomId = UUID.fromString(chatroomIdStr);
        VideoDTO videoDTO = videoService.deleteVideo(chatroomId, videoId);

        playlistService.sendPlaylistUpdateSSE(chatroomId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .data(videoDTO)
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .build());
    }
    private Long getUserIdFromToken(String token){
        return Long.parseLong(tokenProvider.parseClaims(token).getSubject());
    }
}
