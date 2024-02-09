package com.youtubeshareapi.video.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.chat.service.RedisSubscriber;
import com.youtubeshareapi.video.entity.Playlist;
import com.youtubeshareapi.video.entity.PlaylistRepository;
import com.youtubeshareapi.video.entity.Video;
import com.youtubeshareapi.video.entity.VideoRepository;
import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.model.VideoDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final VideoRepository videoRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PLAYLIST_PREFIX = "/playlist/";

    @Override
    public PlaylistDTO createPlaylist(PlaylistDTO playlistDTO) {
        Playlist savedPlaylist = playlistRepository.save(Playlist.builder()
                .chatroomId(Chatroom.builder()
                        .chatroomId(playlistDTO.getChatroomId()).build())
                .playlistName(playlistDTO.getPlaylistName())
                .isActive(false)
                .build());
        return PlaylistDTO.of(savedPlaylist);
    }

    @Transactional
    @Override
    public VideoDTO AddVideo(UUID chatroomId, VideoDTO videoDTO) {
        // db에 비디오 저장
        Video savedVideo = videoRepository.save(Video.of(videoDTO));
        // redis 에 비디오 저장
        redisTemplate.opsForList().leftPush(getRedisKey(chatroomId), VideoDTO.of(savedVideo));
        return VideoDTO.of(savedVideo);
    }

    @Override
    public PlaylistDTO getByChatroomId(UUID chatroomId) {
        // redis에서 플레이리스트 정보 조회
        List<String> rawVideoList = stringRedisTemplate.opsForList().range(getRedisKey(chatroomId), 0, -1);
        if (rawVideoList != null && rawVideoList.size() != 0) {
            // 레디스에 값이 있으면 해당 내용을 반환한다.
            return PlaylistDTO.builder()
                    .chatroomId(chatroomId)
                    .videos(rawVideoList.stream()
                            .map(video -> {
                                try {
                                    return objectMapper.readValue(video, VideoDTO.class);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .toList())
                    .build();
        }
        // chatroomid 를 통해 플레이리스트와 비디오 리스트를 조인해서 가지고온다.
        PlaylistDTO playlistDTO = playlistRepository.findPlaylistByChatroomId(chatroomId);

        // 레디스에 없으면 데이터베이스에서 찾은다음 레디스에 저장하고 반환한다.
        // 데이터베이스도 없으면 그냥 빈 배열이 들어있는 플레이리스트DTO를 반환한다.
        if (!playlistDTO.getVideos().isEmpty()) {
            for(VideoDTO video : playlistDTO.getVideos()) {
                redisTemplate.opsForList().rightPush(getRedisKey(chatroomId), video);
            }
        }

        return playlistDTO;
    }


    private String getRedisKey(UUID chatroomId) {
        return String.format("%s%s", PLAYLIST_PREFIX, chatroomId);
    }
}