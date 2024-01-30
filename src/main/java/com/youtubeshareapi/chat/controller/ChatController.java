package com.youtubeshareapi.chat.controller;

import com.youtubeshareapi.chat.model.ChatMessage;
import com.youtubeshareapi.chat.service.ChatroomService;
import com.youtubeshareapi.chat.service.RedisPublisher;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

  private final RedisPublisher redisPublisher;
  private final ChatroomService chatroomService;

  // Websocket에서 들어온 메시지를 redis로 보내준다
  @MessageMapping("/chat/message")
  public void message(ChatMessage msg){
    log.info("message: {}", msg);
    redisPublisher.publish(new ChannelTopic(msg.getChatroomId()), msg);
  }

  @MessageMapping("/chatroom/{chatroomId}/connect")
  public void handleConnect(@DestinationVariable String chatroomId, SimpMessageHeaderAccessor accessor) {
    if (accessor.getSessionAttributes() != null) {
      accessor.getSessionAttributes().put("chatroomId", chatroomId);
    }
    chatroomService.incrementUserCount(UUID.fromString(chatroomId));
  }

  @EventListener
  public void handleDisconnect(SessionDisconnectEvent event) {
    log.info("disconnect event=== {}", event);
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    if (accessor.getSessionAttributes() != null) {
      String chatroomId = (String) accessor.getSessionAttributes().get("chatroomId");
      chatroomService.decrementUserCount(UUID.fromString(chatroomId));
    }

  }

}
