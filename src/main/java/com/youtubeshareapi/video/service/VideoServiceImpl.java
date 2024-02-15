package com.youtubeshareapi.video.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtubeshareapi.video.entity.Video;
import com.youtubeshareapi.video.entity.VideoRepository;
import com.youtubeshareapi.video.model.VideoDTO;
import com.youtubeshareapi.video.model.VideoMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final VideoRepository videoRepository;
    private static final String VIDEO_PREFIX = "/video/";

    @Transactional
    @Override
    public VideoDTO AddVideo(UUID chatroomId, VideoDTO videoDTO) {
        // db에 비디오 저장
        Video savedVideo = videoRepository.save(Video.of(videoDTO));
        // redis 에 비디오 저장
        redisTemplate.opsForList().leftPush(getVideoPrefix(chatroomId), VideoDTO.of(savedVideo));
        return VideoDTO.of(savedVideo);
    }

    @Override
    public VideoDTO deleteVideo(String chatroomId, Long videoId) throws JsonProcessingException {
        String redisKey = getVideoPrefix(UUID.fromString(chatroomId));
        ListOperations<String, String> listOps = stringRedisTemplate.opsForList();
        List<String> videoList = listOps.range(redisKey, 0, -1);

        if (videoList == null) {
            throw new EntityNotFoundException();
        }
        VideoDTO videoDTO = null;
        for (String videoStr : videoList) {
            videoDTO = objectMapper.readValue(videoStr, new TypeReference<VideoDTO>() {});
            if (videoDTO.getVideoId().equals(videoId)) {
                // 레디스에서 지운다
                listOps.remove(redisKey, 1, videoStr);
                break;
            }
        }
        // 데이터베이스에서도 지운다
        videoRepository.deleteById(videoId);
        return videoDTO;
    }
    @Override
    public VideoDTO getCurrentVideo(UUID chatroomId) throws JsonProcessingException {
        String redisKey = getVideoPrefix(chatroomId);
        VideoDTO currentVideo = null;

        // 레디스에서 현재 곡 가져오기
        String currentVideoJson = stringRedisTemplate.opsForList().index(redisKey, -1);

        if (currentVideoJson != null) {
            currentVideo = objectMapper.readValue(currentVideoJson, VideoDTO.class);
        }
        // todo db에서도 확인하기

        return currentVideo;
    }

    // 비디오 목록에서 가장 끝에있는 비디오를 레디스에서 꺼낸다
    // 현재 비디오가 null이 아니면 다음 비디오도 꺼낸다
    // 다음 비디오가 있으면 현재 비디오를 삭제하고 다음 비디오를 현재 비디오로 설정한다
    // 다음 비디오가 없으면 현재 비디오를 삭제한다
    @Override
    @Transactional
    public VideoDTO getNextVideo(VideoMessage videoMessage) throws JsonProcessingException {
        String redisKey = getVideoPrefix(videoMessage.getChatroomId());
        ListOperations<String, String> listOps = stringRedisTemplate.opsForList();

        VideoDTO currentVideo = objectMapper.readValue(listOps.rightPop(redisKey), VideoDTO.class);

        if (currentVideo != null) {
            VideoDTO nextVideo = objectMapper.readValue(listOps.index(redisKey, -1), VideoDTO.class);

            if (nextVideo != null) {
                nextVideo.setCurrent(true);
                listOps.set(redisKey, -1, objectMapper.writeValueAsString(nextVideo));
                updateCurrentVideoOnDatabase(currentVideo.getVideoId(), nextVideo.getVideoId());
            } else {
                videoRepository.deleteById(currentVideo.getVideoId());
            }
            return nextVideo;
        }
        return null;
    }

    @Transactional
    public void updateCurrentVideoOnDatabase(Long currentVideoId, Long nextVideoId) {
        videoRepository.deleteById(currentVideoId);
        Optional<Video> nextVideoOptional = videoRepository.findById(nextVideoId);
        nextVideoOptional.ifPresent(v -> v.setCurrent(true));
    }

    private String getVideoPrefix(UUID chatroomId) {
        return String.format("%s%s", VIDEO_PREFIX, chatroomId);
    }

}
