package com.youtubeshareapi.chat.service;

import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.chat.entity.ChatroomRepository;
import com.youtubeshareapi.chat.model.ChatroomDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
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
  public ChatroomDTO findByChatroomId(Long userId, UUID chatroomId) {
    Chatroom chatroom = chatroomRepository
        .findByChatroomId(chatroomId);
    return Chatroom.toDTO(chatroom);
  }

  @Override
  public void deleteChatroomByChatroomId(UUID chatroomId) {
    chatroomRepository.deleteById(chatroomId);
  }

  @Override
  public Page<ChatroomDTO> findAllChatrooms(Pageable pageable) {
    return chatroomRepository.findAllChatrooms(pageable);
  }
  @Transactional
  public void incrementUserCount(UUID chatRoomId) {
    Chatroom chatroom = chatroomRepository.findById(chatRoomId)
        .orElseThrow(() -> new RuntimeException("Chatroom not found"));
    chatroom.setUserCount(chatroom.getUserCount() + 1);
    chatroomRepository.save(chatroom);
  }

  @Transactional
  public void decrementUserCount(UUID chatRoomId) {
    Chatroom chatroom = chatroomRepository.findById(chatRoomId)
        .orElseThrow(() -> new RuntimeException("Chatroom not found"));
    chatroom.setUserCount(chatroom.getUserCount() - 1);
    chatroomRepository.save(chatroom);
  }
}
