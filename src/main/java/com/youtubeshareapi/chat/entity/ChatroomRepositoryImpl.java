package com.youtubeshareapi.chat.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;

import static com.youtubeshareapi.chat.entity.QChatroom.chatroom;

public class ChatroomRepositoryImpl implements ChatroomRepositoryCustom {
  private final JPAQueryFactory query;
  public ChatroomRepositoryImpl(EntityManager entityManager) {
    this.query = new JPAQueryFactory(entityManager);
  }

  @Override
  public List<Chatroom> findChatroomsByUserId(Long userId) {
    return query.select(chatroom)
        .from(chatroom)
        .where(chatroom.user.userId.eq(userId))
        .orderBy(chatroom.chatroomId.desc())
        .fetch();
  }
}
