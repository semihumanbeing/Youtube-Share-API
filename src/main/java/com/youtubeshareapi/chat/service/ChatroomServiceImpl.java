package com.youtubeshareapi.chat.service;

import com.youtubeshareapi.chat.model.ChatroomDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatroomServiceImpl implements ChatroomService{

  @Override
  public List<ChatroomDTO> findChatroomsOfUser(String userId) {
    return null;
  }
}
