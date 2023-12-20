package com.youtubeshareapi;

import com.youtubeshareapi.chat.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebSocketTest {

    @Test
    public void testWebSocketClient() throws Exception {
        String url = "ws://127.0.0.1:8080/stomp";  // WebSocket 서버 주소

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        CountDownLatch latch = new CountDownLatch(1);

        StompSessionHandlerAdapter sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                // 연결 성공 후에 실행되는 부분
                System.out.println("Connected to the WebSocket server");

                // 메시지 전송
                ChatMessage chatMessage = ChatMessage.builder()
                        .roomId("room1")
                        .sender("user1")
                        .message("Hello, WebSocket!")
                        .build();

                session.send("/pub/chat/message", chatMessage);
                latch.countDown();
            }
        };

        // WebSocket 연결
        StompSession stompSession = stompClient.connectAsync(url, sessionHandler).get(5, TimeUnit.SECONDS);

        // 기다리기
        assertTrue(latch.await(3, TimeUnit.SECONDS));

        // 연결 종료
        stompSession.disconnect();
    }
}
