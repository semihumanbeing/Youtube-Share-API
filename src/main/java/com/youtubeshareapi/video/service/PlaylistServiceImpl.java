package com.youtubeshareapi.video.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.video.entity.Playlist;
import com.youtubeshareapi.video.entity.PlaylistRepository;
import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.model.VideoDTO;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.AprLifecycleListener;
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
    private static final Map<UUID, List<Map<Long, SseEmitter>>> chatroomSseEmitters = new ConcurrentHashMap<>();

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
        String playlistStr = stringRedisTemplate.opsForValue().get(getPlaylistPrefix(chatroomId));
        List<String> videoListStr = stringRedisTemplate.opsForList().range(getVideoPrefix(chatroomId), 0, -1);

        // redis에 플레이리스트가 없으면 DB에서 조회한 내용을 redis의 플레이리스트와 비디오목록에 할당한다음 반환한다.
        if (playlistStr == null || videoListStr == null) {
            return initPlaylistOnRedis(chatroomId);
        }

        // redis에 플레이리스트와 비디오 목록이 있으면 그 내용을 반환한다.
        PlaylistDTO playlistDTO = objectMapper.readValue(playlistStr, PlaylistDTO.class);
        List<VideoDTO> videoDTOS = videoListStr.stream()
                .map(video -> {
                    try {
                        return objectMapper.readValue(video, VideoDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
            .toList();
        playlistDTO.setVideos(videoDTOS);
        return playlistDTO;
    }

    public PlaylistDTO initPlaylistOnRedis(UUID chatroomId) {
        log.info("======== init playlist");
        PlaylistDTO playlistDTO = playlistRepository.findPlaylistByChatroomId(chatroomId);
        redisTemplate.opsForValue().set(getPlaylistPrefix(playlistDTO.getChatroomId()), playlistDTO);
        stringRedisTemplate.delete(getVideoPrefix(chatroomId));
        for(VideoDTO video : playlistDTO.getVideos()) {
            redisTemplate.opsForList().rightPush(getVideoPrefix(chatroomId), video);
        }
        return playlistDTO;
    }

    public SseEmitter subscribeSSE(UUID chatroomId, Long userId) {
        SseEmitter sseEmitter = new SseEmitter((long) (60000 * 5));
        String sseKey = getPlaylistPrefix(chatroomId);
        try {
            sseEmitter.send(SseEmitter.event().name(sseKey));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        List<Map<Long, SseEmitter>> userEmittersList = chatroomSseEmitters.computeIfAbsent(chatroomId, k -> new CopyOnWriteArrayList<>());
        Map<Long, SseEmitter> userEmitters = new ConcurrentHashMap<>();
        userEmitters.put(userId, sseEmitter);
        userEmittersList.add(userEmitters);

        sseEmitter.onCompletion(() -> {
            userEmitters.remove(userId);
            if (userEmitters.isEmpty()) {
                userEmittersList.remove(userEmitters);
            }
        });

        sseEmitter.onTimeout(() -> {
            userEmitters.remove(userId);
            if (userEmitters.isEmpty()) {
                userEmittersList.remove(userEmitters);
            }
        });

        sseEmitter.onError((e) -> {
            userEmitters.remove(userId);
            if (userEmitters.isEmpty()) {
                userEmittersList.remove(userEmitters);
            }
        });
        return sseEmitter;
    }
    public void sendPlaylistUpdateSSE(UUID chatroomId) throws IOException {
        PlaylistDTO playlistDTO = getByChatroomId(chatroomId);
        if (chatroomSseEmitters.containsKey(chatroomId)) {
            List<Map<Long, SseEmitter>> userEmittersList = chatroomSseEmitters.get(chatroomId);
            for (Map<Long, SseEmitter> userEmitters : userEmittersList) {
                for (Map.Entry<Long, SseEmitter> entry : userEmitters.entrySet()) {
                    Long userId = entry.getKey();
                    SseEmitter sseEmitter = entry.getValue();
                    try {
                        sseEmitter.send(SseEmitter.event()
                            .name(getSSEKey(chatroomId, userId))
                            .data(playlistDTO));
                    } catch (Exception e) {
                        userEmitters.remove(userId);
                    }
                }
            }
        }
    }

    private String getPlaylistPrefix(UUID chatroomId) {
        return String.format("%s%s", PLAYLIST_PREFIX, chatroomId);
    }
    private String getSSEKey(UUID chatroomId, Long userId) {
        return String.format("%s%s/%s", PLAYLIST_PREFIX, chatroomId, userId);
    }
    private String getVideoPrefix(UUID chatroomId) {
        return String.format("%s%s", VIDEO_PREFIX, chatroomId);
    }
}
