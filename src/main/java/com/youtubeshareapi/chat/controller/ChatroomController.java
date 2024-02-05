package com.youtubeshareapi.chat.controller;

import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.chat.model.ChatroomDTO;
import com.youtubeshareapi.chat.model.CreateChatroomRequest;
import com.youtubeshareapi.chat.model.CreateChatroomResponse;
import com.youtubeshareapi.chat.model.PageRequest;
import com.youtubeshareapi.chat.model.UpdateChatroomRequest;
import com.youtubeshareapi.chat.service.ChatroomService;
import com.youtubeshareapi.common.CookieUtil;
import com.youtubeshareapi.common.EmojiUtil;
import com.youtubeshareapi.common.ResponseDTO;
import com.youtubeshareapi.exception.ChatroomLimitException;
import com.youtubeshareapi.security.JwtTokenProvider;
import com.youtubeshareapi.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@Slf4j
@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatroomController {

  private static final int MAX_USER_COUNT = 30;
  private final JwtTokenProvider tokenProvider;
  private final ChatroomService chatroomService;
  private final int CHATROOM_AMOUNT_LIMIT = 5;

  /**
   * 채팅방 제작
   * @param createChatroomRequest
   * @param request
   * @return
   */
  @PostMapping("")
  public ResponseEntity<?> createChatroom(@Valid @RequestBody CreateChatroomRequest createChatroomRequest,
      HttpServletRequest request) {
    String token = CookieUtil.resolveToken(request);
    Long userId = getUserIdFromToken(token);

    log.info("---------createChatroom");
    checkChatroomAmount(userId);
    String password = createChatroomRequest.getChatroomPassword().trim();

    ChatroomDTO chatroomDTO = ChatroomDTO.builder()
        .chatroomId(UUID.randomUUID())
        .userId(userId)
        .chatroomName(createChatroomRequest.getChatroomName())
        .chatroomPassword(createChatroomRequest.getChatroomPassword())
        .maxUserCount(MAX_USER_COUNT)
        .hasPwd(!password.isEmpty())
        .emoji(EmojiUtil.getRandomEmoji())
        .build();

    // 채팅방 생성
    ChatroomDTO savedChatroomData = chatroomService.saveChatroom(chatroomDTO);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.builder()
            .data(CreateChatroomResponse.builder()
                .chatroomId(savedChatroomData.getChatroomId())
                .userId(userId)
                .chatroomName(savedChatroomData.getChatroomName())
                .emoji(savedChatroomData.getEmoji())
                .chatroomPassword(savedChatroomData.getChatroomPassword())
                .userCount(savedChatroomData.getUserCount())
                .maxUserCount(savedChatroomData.getMaxUserCount())
                .hasPwd(savedChatroomData.isHasPwd())
                .createdAt(savedChatroomData.getCreatedAt())
                .build())
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .build());
  }

  private void checkChatroomAmount(Long userId) {
    int chatroomAmount = chatroomService.countChatroomByUserId(userId);
    if (chatroomAmount >= CHATROOM_AMOUNT_LIMIT) {
      throw new ChatroomLimitException("chatroom amount limit = "+ CHATROOM_AMOUNT_LIMIT);
    }
  }

  /**
   * 모든 채팅방 목록 조회
   * @param request
   * @return
   */
  @GetMapping("/all")
  public ResponseEntity<?> getAllChatrooms(HttpServletRequest request,
      PageRequest pageRequest) {

    log.info("---------getAllChatrooms");
    Pageable pageable = pageRequest.of();
    log.info("-----{}",pageable);
    Page<ChatroomDTO> chatrooms = chatroomService.findAllChatrooms(pageable);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.builder()
            .data(chatrooms)
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
    log.info("---------getChatroomsOfUser");
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
      @PathVariable String chatroomId,
      @RequestBody UpdateChatroomRequest updateChatroomRequest){
    log.info("---------updateChatroomName");
    String token = CookieUtil.resolveToken(request);
    Long userId = getUserIdFromToken(token);
    UUID chatroomUuid = UUID.fromString(chatroomId);
    ChatroomDTO chatroomDTO = chatroomService.findByChatroomId(userId, chatroomUuid);

    String chatroomName = updateChatroomRequest.getChatroomName();
    String chatroomPassword = updateChatroomRequest.getChatroomPassword();

    // 변경사항이 없으면 기존의 정보를 리턴한다.
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
      @PathVariable(name = "chatroomId") String chatroomId){

    log.info("---------deleteChatroom");
    // Todo 방을 만든 사람만 삭제할수 있음
    UUID chatroomUuid = UUID.fromString(chatroomId);
    chatroomService.deleteChatroomByChatroomId(chatroomUuid);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.builder()
            .data(chatroomId)
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .build());
  }

  private Long getUserIdFromToken(String token){
    return Long.parseLong(tokenProvider.parseClaims(token).getSubject());
  }



}
