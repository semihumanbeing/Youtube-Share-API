package com.youtubeshareapi.chat.service;

import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.chat.entity.ChatroomRepository;
import com.youtubeshareapi.chat.model.ChatroomDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatroomServiceImpl implements ChatroomService{

  private final ChatroomRepository chatroomRepository;
  @Override
  public List<ChatroomDTO> findChatroomsOfUser(Long userId) {
    return chatroomRepository.findChatroomsByUserId(userId)
        .stream().map(Chatroom::toDTO).toList();
  }

  @Override
  public ChatroomDTO saveChatroom(ChatroomDTO chatroomDTO) {
    Chatroom savedChatroom = chatroomRepository.save(chatroomDTO.toEntity(chatroomDTO));
    return Chatroom.toDTO(savedChatroom);
  }

  @Override
  public int countChatroomByUserId(Long userId) {
    return chatroomRepository.countChatroomByUserId(userId);
  }

  @Override
  public ChatroomDTO findByChatroomId(Long userId, Long chatroomId) {
    Chatroom chatroom = chatroomRepository
        .findByChatroomId(chatroomId);
    return Chatroom.toDTO(chatroom);
  }

  @Override
  public void deleteChatroomByChatroomId(Long chatroomId) {
    chatroomRepository.deleteById(chatroomId);
  }

  @Override
  public Page<ChatroomDTO> findAllChatrooms(Pageable pageable) {
    return chatroomRepository.findAllChatrooms(pageable);
  }
}
