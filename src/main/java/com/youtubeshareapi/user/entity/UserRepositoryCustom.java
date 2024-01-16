package com.youtubeshareapi.user.entity;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface UserRepositoryCustom {
  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
