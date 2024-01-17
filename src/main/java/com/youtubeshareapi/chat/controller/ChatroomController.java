package com.youtubeshareapi.chat.controller;

import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.chat.model.ChatroomDTO;
import com.youtubeshareapi.chat.model.CreateChatroomResponse;
import com.youtubeshareapi.chat.model.UpdateChatroomRequest;
import com.youtubeshareapi.chat.service.ChatroomService;
import com.youtubeshareapi.common.CookieUtil;
import com.youtubeshareapi.common.ResponseDTO;
import com.youtubeshareapi.exception.ChatroomLimitException;
import com.youtubeshareapi.security.JwtTokenProvider;
import com.youtubeshareapi.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatroomController {
  private final JwtTokenProvider tokenProvider;
  private final ChatroomService chatroomService;
  private final int CHATROOM_AMOUNT_LIMIT = 5;

  /**
   * 채팅방 제작
   * @param chatroomDTO
   * @param request
   * @return
   */
  @PostMapping("")
  public ResponseEntity<?> createChatroom(@Valid @RequestBody ChatroomDTO chatroomDTO,
      HttpServletRequest request) {
    String token = CookieUtil.resolveToken(request);
    Long userId = getUserIdFromToken(token);

    int chatroomAmount = chatroomService.countChatroomByUserId(userId);
    if (chatroomAmount >= CHATROOM_AMOUNT_LIMIT) {
      throw new ChatroomLimitException("chatroom amount limit = "+ CHATROOM_AMOUNT_LIMIT);
    }

    chatroomDTO.setUser(User.builder().userId(userId).build());
    ChatroomDTO savedChatroomData = chatroomService.saveChatroom(chatroomDTO);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.builder()
            .data(CreateChatroomResponse.builder()
                .chatroomId(savedChatroomData.getChatroomId())
                .userId(userId)
                .chatroomName(savedChatroomData.getChatroomName())
                .chatroomPassword(savedChatroomData.getChatroomPassword())
                .createdAt(savedChatroomData.getCreatedAt())
                .build())
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .build());
  }
  /**
   * 유저가 소유한 채팅방 목록 조회
   * @param request
   * @return
   */
  @GetMapping("/all")
  public ResponseEntity<?> getAllChatrooms(HttpServletRequest request) {
    String token = CookieUtil.resolveToken(request);
    Long userId = getUserIdFromToken(token);

    List<ChatroomDTO> chatroomsOfUser = chatroomService.findChatroomsOfUser(userId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.builder()
            .data(chatroomsOfUser)
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .build());
  }
  /**
   * 유저가 소유한 채팅방 목록 조회
   * @param request
   * @return
   */
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

  @PutMapping("/{chatroomId}")
  public ResponseEntity<?> updateChatroomName(HttpServletRequest request,
      @PathVariable Long chatroomId,
      @RequestBody UpdateChatroomRequest updateChatroomRequest){
    String token = CookieUtil.resolveToken(request);
    Long userId = getUserIdFromToken(token);
    ChatroomDTO chatroomDTO = chatroomService.findByChatroomId(userId, chatroomId);

    String chatroomName = updateChatroomRequest.getChatroomName();
    String chatroomPassword = updateChatroomRequest.getChatroomPassword();

    if(chatroomName.equals(chatroomDTO.getChatroomName()) &&
    chatroomPassword.equals(chatroomDTO.getChatroomPassword())){
      return ResponseEntity.status(HttpStatus.OK)
          .body(ResponseDTO.builder()
              .data(chatroomDTO)
              .timestamp(new Timestamp(System.currentTimeMillis()))
              .build());
    }

    chatroomDTO.setChatroomName(
        (chatroomName.isBlank()) ? chatroomDTO.getChatroomName() : chatroomName);
    chatroomDTO.setChatroomPassword(
        (chatroomPassword.isBlank()) ? chatroomDTO.getChatroomPassword() : chatroomPassword);

    ChatroomDTO result = chatroomService.saveChatroom(chatroomDTO);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.builder()
            .data(result)
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .build());

  }

  @DeleteMapping("/{chatroomId}")
  public ResponseEntity<?> deleteChatroom(HttpServletRequest request,
      @PathVariable Long chatroomId){
    // Todo 방을 만든 사람만 삭제할수 있음
    chatroomService.deleteChatroomByChatroomId(chatroomId);

    return null;
  }

  private Long getUserIdFromToken(String token){
    return Long.parseLong(tokenProvider.parseClaims(token).getSubject());
  }


}
