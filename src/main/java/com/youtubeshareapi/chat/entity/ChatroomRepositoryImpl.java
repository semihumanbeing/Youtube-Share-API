package com.youtubeshareapi.chat.entity;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.youtubeshareapi.chat.model.ChatroomDTO;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static com.youtubeshareapi.chat.entity.QChatroom.chatroom;
import static com.youtubeshareapi.user.entity.QUser.user;

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
        .orderBy(chatroom.createdAt.desc())
        .fetch();
  }
  @Override
  public Chatroom findByChatroomId(UUID chatroomId) {
    return query.select(chatroom)
        .from(chatroom)
        .where(chatroom.chatroomId.eq(chatroomId))
        .fetchOne();
  }
  @Override
  public int countChatroomByUserId(Long userId) {
    return Math.toIntExact(query.select(chatroom.count())
        .from(chatroom)
        .where(chatroom.user.userId.eq(userId))
        .fetchFirst());
  }

  @Override
  public Page<ChatroomDTO> findAllChatrooms(Pageable pageable) {
    List<ChatroomDTO> result = query
        .select(chatroom)
        .from(chatroom)
        .join(chatroom.user, user)
        .orderBy(chatroom.chatroomId.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch().stream().map(Chatroom::toDTO).collect(Collectors.toList());
    int count = Math.toIntExact(query.select(chatroom.count())
        .from(chatroom)
        .fetchFirst());
    return new PageImpl<>(result, pageable, count);

  }
}
