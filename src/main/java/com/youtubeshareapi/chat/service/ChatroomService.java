package com.youtubeshareapi.chat.service;

import com.youtubeshareapi.chat.model.ChatroomDTO;
import java.util.List;

public interface ChatroomService {
  List<ChatroomDTO> findChatroomsOfUser(Long userId);

  ChatroomDTO saveChatroom(ChatroomDTO chatroomDTO);

  int countChatroomByUserId(Long userId);

  ChatroomDTO findByChatroomId(Long userId, Long chatroomId);

  void deleteChatroomByChatroomId(Long chatroomId);
}
