package com.youtubeshareapi.chat.controller;

import com.youtubeshareapi.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

  // Websocket에서 들어온 메시지를 redis로 보내준다
  @MessageMapping("/message")
  public void message(ChatMessage msg){
    // TODO
  }
}
