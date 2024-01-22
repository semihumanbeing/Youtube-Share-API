package com.youtubeshareapi.chat.controller;

import com.youtubeshareapi.chat.model.ChatMessage;
import com.youtubeshareapi.chat.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

  private final RedisPublisher redisPublisher;
  // Websocket에서 들어온 메시지를 redis로 보내준다
  @MessageMapping("/chat/message")
  public void message(ChatMessage msg){
    log.info("message: {}", msg);
    redisPublisher.publish(new ChannelTopic(msg.getChatroomId()), msg);
  }

}
