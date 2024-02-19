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
    private static final String PLAYLIST_PREFIX = "/playlist/";
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
    public VideoDTO deleteVideo(UUID chatroomId, Long videoId) throws JsonProcessingException {
        String redisKey = getVideoPrefix(chatroomId);
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
    @Transactional
    @Override
    public VideoDTO getCurrentVideo(UUID chatroomId) throws JsonProcessingException {
        log.info("========= getCurrentVideo");
        String redisKey = getVideoPrefix(chatroomId);
        VideoDTO currentVideo = null;
        ListOperations<String, String> listOps = stringRedisTemplate.opsForList();

        // 레디스에서 현재 곡 가져오기
        String currentVideoJson = listOps.index(redisKey, -1);
        log.info("currentVideo: {}", currentVideoJson);
        if (currentVideoJson != null) {
            currentVideo = objectMapper.readValue(currentVideoJson, new TypeReference<VideoDTO>() {});
            currentVideo.setCurrent(true);
            listOps.set(redisKey, -1, objectMapper.writeValueAsString(currentVideo));

            videoRepository.findById(currentVideo.getVideoId()).ifPresent(video -> video.setCurrent(true));

        }

        return currentVideo;
    }

    // 비디오 목록에서 가장 끝에있는 비디오를 레디스에서 꺼낸다
    // 현재 비디오가 null이 아니면 다음 비디오도 꺼낸다
    // 다음 비디오가 있으면 현재 비디오를 삭제하고 다음 비디오를 현재 비디오로 설정한다
    // 다음 비디오가 없으면 현재 비디오를 삭제한다
    @Override
    @Transactional
    public VideoDTO getNextVideo(VideoMessage videoMessage) throws JsonProcessingException {
      log.info("========= getNextVideo");
      String redisKey = getVideoPrefix(videoMessage.getChatroomId());
      ListOperations<String, String> listOps = stringRedisTemplate.opsForList();

      String currentVideoJson = listOps.rightPop(redisKey);
      // 데이터가 없으면 빈 값 반환
      if (currentVideoJson == null) {
        return null;
      }
      VideoDTO currentVideo = objectMapper.readValue(currentVideoJson, VideoDTO.class);
      String nextVideoJson = listOps.index(redisKey, -1);
      log.info("current: {} / next: {}",currentVideo, nextVideoJson);
      // 현재 비디오만 있고 다음 비디오가 없으면 현재 비디오를 삭제
      if (nextVideoJson == null) {
        videoRepository.deleteById(currentVideo.getVideoId());
        listOps.rightPop(redisKey);
        return null;
      }

      // 현재비디오와 다음 비디오가 전부 있으면 다음 비디오를 현재 비디오로 설정
      VideoDTO nextVideo = objectMapper.readValue(nextVideoJson, VideoDTO.class);
      nextVideo.setCurrent(true);
      listOps.set(redisKey, -1, objectMapper.writeValueAsString(nextVideo));
      updateCurrentVideoOnDatabase(currentVideo.getVideoId(), nextVideo.getVideoId());
      return nextVideo;
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
