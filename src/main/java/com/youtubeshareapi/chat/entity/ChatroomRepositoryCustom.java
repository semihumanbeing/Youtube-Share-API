package com.youtubeshareapi.chat.entity;

import java.util.List;

public interface ChatroomRepositoryCustom {
  List<Chatroom> findChatroomsByUserId(Long userId);

  Chatroom findByChatroomId(Long chatroomId);

  int countChatroomByUserId(Long userId);
}
