package com.youtubeshareapi.chat.service;

import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.chat.entity.ChatroomRepository;
import com.youtubeshareapi.chat.model.ChatroomDTO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatroomServiceImpl implements ChatroomService{

  private final ChatroomRepository chatroomRepository;
  @Override
  public List<ChatroomDTO> findChatroomsOfUser(Long userId) {
    return chatroomRepository.findChatroomsByUserId(userId)
        .stream().map(ChatroomDTO::toDTO).toList();
  }

  @Override
  public ChatroomDTO saveChatroom(ChatroomDTO chatroomDTO) {
    Chatroom savedChatroom = chatroomRepository.save(chatroomDTO.toEntity(chatroomDTO));
    return ChatroomDTO.toDTO(savedChatroom);
  }
}
