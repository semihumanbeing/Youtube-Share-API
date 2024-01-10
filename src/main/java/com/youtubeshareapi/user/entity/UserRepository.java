package com.youtubeshareapi.user.entity;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query(value = "select exists(select u from User u where u.email = ?1)")
  boolean existsByEmail(String email);

  @Query(value = "select u.userId, u.username, u.email, "
      + "u.userRole, u.password, u.createdAt, u.updatedAt "
      + "from User u where u.email = ?1")
  Optional<User> findByEmail(String email);

  @Query(value = "SELECT u FROM User u "
      + "LEFT JOIN u.token t "
      + "WHERE u.email = ?1 AND u.password = ?2")
  Optional<User> findByEmailAndPassword(String email, String password);
}
