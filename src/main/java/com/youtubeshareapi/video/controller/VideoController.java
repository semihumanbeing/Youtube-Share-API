package com.youtubeshareapi.video.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.youtubeshareapi.common.CookieUtil;
import com.youtubeshareapi.common.ResponseDTO;
import com.youtubeshareapi.security.JwtTokenProvider;
import com.youtubeshareapi.video.model.VideoDTO;
import com.youtubeshareapi.video.model.VideoRequest;
import com.youtubeshareapi.video.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.sql.Timestamp;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
public class VideoController {
    private final JwtTokenProvider tokenProvider;
    private final VideoService videoService;
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
                                                HttpServletRequest request) {
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

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.builder()
                        .data(videoService.AddVideo(chatroomId, videoDTO))
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .build());
    }
    @DeleteMapping("/{chatroomId}/{playlistId}/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable(name = "chatroomId") String chatroomId,
                                         @PathVariable(name = "playlistId") Long playlistId,
                                         @PathVariable(name = "videoId") Long videoId)
        throws JsonProcessingException {
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
