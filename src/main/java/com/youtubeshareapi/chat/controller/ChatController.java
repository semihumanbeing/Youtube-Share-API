package com.youtubeshareapi.chat.controller;

import com.youtubeshareapi.chat.model.ChatMessage;
import com.youtubeshareapi.chat.service.ChatroomService;
import com.youtubeshareapi.chat.service.RedisPublisher;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatroomService chatroomService;

    // Websocket에서 들어온 메시지를 redis로 보내준다
    @MessageMapping("/chat/message")
    public void message(ChatMessage msg) {
        redisPublisher.publishMessage(new ChannelTopic(msg.getChatroomId()), msg);
    }

    @MessageMapping("/chatroom/{chatroomId}/connect")
    public void handleConnect(@DestinationVariable(value = "chatroomId") String chatroomId,
                              @Payload Map<String, String> payload,
                              SimpMessageHeaderAccessor accessor) {
        String username = payload.get("username");
        Objects.requireNonNull(accessor.getSessionAttributes()).put("chatroomId", chatroomId);
        Objects.requireNonNull(accessor.getSessionAttributes()).put("username", username);

        chatroomService.incrementUserCount(UUID.fromString(chatroomId));
        redisPublisher.publishMessage(new ChannelTopic(chatroomId), ChatMessage.builder()
                .username(username)
                .chatroomId(chatroomId)
                .message(String.format("%s entered the room", username))
                .build());
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getSessionAttributes() != null) {
            String chatroomId = (String) accessor.getSessionAttributes().get("chatroomId");
            String username = (String) accessor.getSessionAttributes().get("username");
            if (chatroomId == null) return;
            chatroomService.decrementUserCount(UUID.fromString(chatroomId));
            redisPublisher.publishMessage(new ChannelTopic(chatroomId), ChatMessage.builder()
                    .username(username)
                    .chatroomId(chatroomId)
                    .message(String.format("%s left the room", username))
                    .build());
        }


    }

}
