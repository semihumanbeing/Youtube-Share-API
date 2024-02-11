package com.youtubeshareapi.chat.service;

import com.youtubeshareapi.chat.model.ChatMessage;
import com.youtubeshareapi.video.model.VideoDTO;
import com.youtubeshareapi.video.model.VideoMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {

  private final RedisTemplate<String, Object> redisTemplate;

  public void publishMessage(ChannelTopic topic, ChatMessage message){
    redisTemplate.convertAndSend(topic.getTopic(), message);
    log.info("Published message to topic '{}': {}", topic.getTopic(), message);
  }
  public void publishVideo(ChannelTopic topic, VideoDTO videoDTO){
    redisTemplate.convertAndSend(topic.getTopic(), videoDTO);
    log.info("Published message to topic '{}': {}", topic.getTopic(), videoDTO);
  }

}


