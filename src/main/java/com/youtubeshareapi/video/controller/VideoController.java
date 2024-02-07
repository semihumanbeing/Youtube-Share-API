package com.youtubeshareapi.video.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtubeshareapi.video.model.VideoRequest;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/{chatroomId}")
    public ResponseEntity<?> getPlaylistById(@PathVariable String chatroomId) {

        // 채팅방아이디를 가지고 플레이리스트를 레디스에서 검색한다
        // 레디스에 있으면 해당 내용을 반환한다.
        // 레디스에 없으면 데이터베이스에서 찾은다음 레디스에 저장하고 반환한다.
        return null;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addVideoToPlaylist(@RequestBody VideoRequest videoRequest) {
        // 리퀘스트를 비디오 리스트에 넣는다
        // 레디스에 leftpush 한다
        // 입력된 곡을 반환한다.
        return null;
    }

    @GetMapping("/{chatroomId}/next")
    public ResponseEntity<?> getNextVideo(@PathVariable Long chatroomId) {
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

}
