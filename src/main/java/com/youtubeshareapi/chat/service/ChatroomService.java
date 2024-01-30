package com.youtubeshareapi.chat.service;

import com.youtubeshareapi.chat.model.ChatroomDTO;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatroomService {
  List<ChatroomDTO> findChatroomsOfUser(Long userId);

  ChatroomDTO saveChatroom(ChatroomDTO chatroomDTO);

  int countChatroomByUserId(Long userId);

  ChatroomDTO findByChatroomId(Long userId, UUID chatroomId);

  void deleteChatroomByChatroomId(UUID chatroomId);

  Page<ChatroomDTO> findAllChatrooms(Pageable pageable);

}
