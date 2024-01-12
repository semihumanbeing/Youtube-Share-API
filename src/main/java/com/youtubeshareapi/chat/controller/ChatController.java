package com.youtubeshareapi.chat.controller;

import com.youtubeshareapi.chat.model.ChatMessage;
import com.youtubeshareapi.chat.model.ChatroomDTO;
import com.youtubeshareapi.chat.service.ChatroomService;
import com.youtubeshareapi.chat.service.RedisPublisher;
import com.youtubeshareapi.common.CookieUtil;
import com.youtubeshareapi.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

  private final RedisPublisher redisPublisher;
  // Websocket에서 들어온 메시지를 redis로 보내준다
  @MessageMapping("/chat/message")
  public void message(ChatMessage msg){
    log.info("message: {}", msg);
    redisPublisher.publish(new ChannelTopic(msg.getRoomId()), msg);
  }

}
