package com.youtubeshareapi.user.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import static com.youtubeshareapi.user.entity.QUser.user;

public class UserRepositoryImpl implements UserRepositoryCustom {
  private final JPAQueryFactory query;
  public  UserRepositoryImpl (EntityManager entityManager) {
    this.query = new JPAQueryFactory(entityManager);
  }

  @Override
  public boolean existsByEmail(String email) {
    return query.select(user)
        .from(user)
        .where(user.email.eq(email))
        .fetchFirst() != null;
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return Optional.ofNullable(query.select(user)
        .from(user)
        .where(user.email.eq(email))
        .fetchOne());
  }
}
