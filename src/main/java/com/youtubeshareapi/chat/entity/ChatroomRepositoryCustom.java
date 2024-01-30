package com.youtubeshareapi.chat.entity;

import com.youtubeshareapi.chat.model.ChatroomDTO;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatroomRepositoryCustom {
  List<Chatroom> findChatroomsByUserId(Long userId);

  Chatroom findByChatroomId(UUID chatroomId);

  int countChatroomByUserId(Long userId);
  Page<ChatroomDTO> findAllChatrooms(Pageable pageable);
}
