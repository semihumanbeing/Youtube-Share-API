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

  @PostMapping("/add")
  public ResponseEntity<?> addVideo(@RequestBody VideoRequest videoRequest)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    String videoInfoJson = objectMapper.writeValueAsString(videoRequest);
    String key = String.format("/%s/%s", videoRequest.getChatroomId(), videoRequest.getPlaylistId());

    redisTemplate.opsForList().leftPush(key, videoInfoJson);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/next")
  public ResponseEntity<?> getNextVideo(@RequestBody VideoRequest videoRequest)
      throws JsonProcessingException {
    String playlistKey = videoRequest.getPlaylistId();
    String videoJson = (String) redisTemplate.opsForList().rightPop(playlistKey);

    if (videoJson == null) {
      return ResponseEntity.notFound().build();
    }

    ObjectMapper objectMapper = new ObjectMapper();
    VideoRequest videoData = objectMapper.readValue(videoJson, VideoRequest.class);
    return ResponseEntity.ok(videoData);
  }

  @DeleteMapping("/delete/{index}")
  public ResponseEntity<?> deleteVideo(@RequestBody VideoRequest videoRequest, @PathVariable int index) {
    List<Object> fullPlaylist = redisTemplate.opsForList().range(videoRequest.getPlaylistId(), 0, -1);

    if (index < 0 || index >= fullPlaylist.size()) {
      return ResponseEntity.badRequest().body("Invalid index");
    }

    Object videoToDelete = fullPlaylist.get(index);
    redisTemplate.opsForList().remove(videoRequest.getUrl(), 0, videoToDelete);

    List<Object> updatedPlaylist = redisTemplate.opsForList().range(videoRequest.getPlaylistId(), 0, -1);
    return ResponseEntity.ok(updatedPlaylist);
  }


}
