package com.youtubeshareapi.chat.service;

import com.youtubeshareapi.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisChatService {

  private final RedisTemplate<String, Object> redisTemplate;

  public void publish(ChannelTopic topic, ChatMessage message){
    redisTemplate.convertAndSend(topic.getTopic(), message);
    log.info("Published message to topic '{}': {}", topic, message);
  }

}


