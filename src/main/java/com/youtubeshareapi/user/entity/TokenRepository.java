package com.youtubeshareapi.user.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

  @Query("select t from Token t where t.user.userId = ?1")
  Token findByUserId(Long userId);
}
