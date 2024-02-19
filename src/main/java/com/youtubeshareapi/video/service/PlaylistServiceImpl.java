package com.youtubeshareapi.video.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.video.entity.Playlist;
import com.youtubeshareapi.video.entity.PlaylistRepository;
import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.model.VideoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PLAYLIST_PREFIX = "/playlist/";
    private static final String VIDEO_PREFIX = "/video/";
    public static Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @Override
    public PlaylistDTO createPlaylist(PlaylistDTO playlistDTO) {
        Playlist savedPlaylist = playlistRepository.save(Playlist.builder()
                .chatroomId(Chatroom.builder()
                        .chatroomId(playlistDTO.getChatroomId()).build())
                .playlistName(playlistDTO.getPlaylistName())
                .isActive(true)
                .build());
        redisTemplate.opsForValue().set(getPlaylistPrefix(playlistDTO.getChatroomId()), PlaylistDTO.of(savedPlaylist));
        return PlaylistDTO.of(savedPlaylist);
    }

    @Override
    public PlaylistDTO getByChatroomId(UUID chatroomId) throws JsonProcessingException {
        // redis에서 플레이리스트 정보 조회
        List<String> videoListStr = stringRedisTemplate.opsForList().range(getVideoPrefix(chatroomId), 0, -1);
        String playlistStr = stringRedisTemplate.opsForValue().get(getPlaylistPrefix(chatroomId));
        if (videoListStr != null && playlistStr != null) {
            // 레디스에 값이 있으면 해당 내용을 반환한다.
            List<VideoDTO> videoDTOS = videoListStr.stream()
                    .map(video -> {
                        try {
                            return objectMapper.readValue(video, VideoDTO.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                .sorted(Comparator.comparingLong(VideoDTO::getVideoId))
                .toList();
            PlaylistDTO playlistDTO = objectMapper.readValue(playlistStr, PlaylistDTO.class);
            playlistDTO.setVideos(videoDTOS);
            return playlistDTO;
        }
        // 값이 없으면 DB에서 조회한다.
        PlaylistDTO playlistDTO = playlistRepository.findPlaylistByChatroomId(chatroomId);

        // 레디스에 없으면 데이터베이스에서 찾은다음 레디스에 저장하고 반환한다.
        if (playlistDTO != null && !playlistDTO.getVideos().isEmpty()) {
            for(VideoDTO video : playlistDTO.getVideos()) {
                redisTemplate.opsForList().rightPush(getVideoPrefix(chatroomId), video);
            }
        }

        // 데이터베이스도 없으면 그냥 빈 배열이 들어있는 플레이리스트DTO를 반환한다.
        return playlistDTO;
    }

    public SseEmitter subscribeSSE(UUID chatroomId) {
        SseEmitter sseEmitter = new SseEmitter((long) (60000 * 5));
        String sseKey = getPlaylistPrefix(chatroomId);
        try {
            sseEmitter.send(SseEmitter.event().name(sseKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sseEmitters.put(getPlaylistPrefix(chatroomId), sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitters.remove(sseKey));
        sseEmitter.onTimeout(() -> sseEmitters.remove(sseKey));
        sseEmitter.onError((e) -> sseEmitters.remove(sseKey));
        return sseEmitter;
    }
    public void sendPlaylistUpdateSSE(UUID chatroomId) throws JsonProcessingException {
        String sseKey = getPlaylistPrefix(chatroomId);
        PlaylistDTO playlistDTO = getByChatroomId(chatroomId);
        if (sseEmitters.containsKey(sseKey)) {
            SseEmitter sseEmitter = sseEmitters.get(sseKey);
            try {
                sseEmitter.send(SseEmitter.event().name(sseKey).data(playlistDTO));
            } catch (Exception e) {
                sseEmitters.remove(sseKey);
            }
        }

    }

    private String getPlaylistPrefix(UUID chatroomId) {
        return String.format("%s%s", PLAYLIST_PREFIX, chatroomId);
    }
    private String getVideoPrefix(UUID chatroomId) {
        return String.format("%s%s", VIDEO_PREFIX, chatroomId);
    }
}
