package com.youtubeshareapi.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtubeshareapi.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageListenerService implements MessageListener {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;
  private final SimpMessageSendingOperations messageTemplate;

  private final String topic = "/sub/chat/room/";

  // Websocket에서 redis로 메시지를 보내면 MessageListener가 보고있다가 Websocket 구독자들에게 보내준다
  // TODO 왜 이부분이 실행을 안할까?????
  @Override
  public void onMessage(Message message, byte[] pattern) {
    String messageToPublish = redisTemplate.getStringSerializer().deserialize(message.getBody());
    String channel = new String(message.getChannel());
    String body = new String(message.getBody());

    log.info("Received message from channel '{}': {}", channel, body);
    try {
      ChatMessage msg = objectMapper.readValue(messageToPublish, ChatMessage.class);
      messageTemplate.convertAndSend(topic + msg.getRoomId(), msg);

    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
