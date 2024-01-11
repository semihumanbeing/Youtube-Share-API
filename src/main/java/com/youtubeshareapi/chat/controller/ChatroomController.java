package com.youtubeshareapi.chat.controller;

import com.youtubeshareapi.chat.model.ChatroomDTO;
import com.youtubeshareapi.chat.service.ChatroomService;
import com.youtubeshareapi.common.CookieUtil;
import com.youtubeshareapi.common.ResponseDTO;
import com.youtubeshareapi.security.JwtTokenProvider;
import com.youtubeshareapi.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatroomController {
  private final JwtTokenProvider tokenProvider;
  private final ChatroomService chatroomService;

    // 로그인이 되면 리퀘스트에서 토큰을 가져와서 유저아이디를 추출
    // 유저아이디, 방 이름을 통해 채팅방을 DB에 저장
    // 프론트에 채팅방에 대한 정보를 전달
  @PostMapping("")
  public ResponseEntity<?> createChatroom(@Valid @RequestBody ChatroomDTO chatroomDTO, HttpServletRequest request) {
    String token = CookieUtil.resolveToken(request);
    Long userId = getUserIdFromToken(token);
    chatroomDTO.setUser(User.builder().userId(userId).build());
    ChatroomDTO result = chatroomService.saveChatroom(chatroomDTO);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.builder()
            .data(result)
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .build());
  }

  @GetMapping("")
  public ResponseEntity<?> getChatroomsOfUser(HttpServletRequest request) {
    String token = CookieUtil.resolveToken(request);
    Long userId = getUserIdFromToken(token);

    List<ChatroomDTO> chatroomsOfUser = chatroomService.findChatroomsOfUser(userId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.builder()
            .data(chatroomsOfUser)
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .build());
  }

  private Long getUserIdFromToken(String token){
    return Long.parseLong(tokenProvider.parseClaims(token).getSubject());
  }


}
