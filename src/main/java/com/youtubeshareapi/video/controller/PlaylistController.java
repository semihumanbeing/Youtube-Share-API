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
    private final JwtTokenProvider tokenProvider;


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
                        .data(playlistService.AddVideo(chatroomId, videoDTO))
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .build());
    }

    @GetMapping("/{chatroomId}/{playlistId}/next")
    public ResponseEntity<?> getNextVideo(@PathVariable Long chatroomId,
                                          @PathVariable(name = "playlistId") Long playlistId) {
        // 레디스에서 현재곡을 rightpop 하고
        // 그 곡의 iscurrent 를 false 로 변경한다 (비동기적으로 진행)

        // 확인된 마지막 곡을 iscurrent true 로 설정한다.
        // 레디스의 마지막 곡 정보를 반환한다.
        return null;
    }

    @DeleteMapping("/{chatroomId}/{playlistId}")
    public ResponseEntity<?> deleteVideo(@PathVariable(name = "chatroomId") String chatroomId,
                                         @PathVariable(name = "playlistId") Long playlistId) {
        // 레디스에서 chatroomId 를 찾는다
        // 플레이리스트 아이디 인 것을 찾아 지운다
        // 데이터베이스에서도 지운다

        return null;
    }
    private Long getUserIdFromToken(String token){
        return Long.parseLong(tokenProvider.parseClaims(token).getSubject());
    }
}
