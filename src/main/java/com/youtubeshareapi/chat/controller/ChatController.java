package com.youtubeshareapi.chat.controller;

import com.youtubeshareapi.chat.model.ChatMessage;
import com.youtubeshareapi.chat.model.ChatRoom;
import com.youtubeshareapi.chat.service.RedisPublisher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller("/chat")
@RequiredArgsConstructor
public class ChatController {
  private final RedisPublisher redisPublisher;

  // Websocket에서 들어온 메시지를 redis로 보내준다
  @MessageMapping("/message")
  public void message(ChatMessage msg){
    log.info("message: {}", msg);
    redisPublisher.publish(new ChannelTopic(msg.getRoomId()), msg);
  }

  @PostMapping("/room")
  public ResponseEntity<?> createRoom(@RequestBody ChatRoom chatRoom, HttpServletRequest request) {
    return null;
  }
}
