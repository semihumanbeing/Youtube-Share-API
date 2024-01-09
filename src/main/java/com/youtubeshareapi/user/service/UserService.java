package com.youtubeshareapi.user.service;

import com.youtubeshareapi.user.model.TokenDTO;
import com.youtubeshareapi.user.model.UserDTO;
import java.util.UUID;

public interface UserService {


  TokenDTO login(String email, String password);

  void register(UserDTO userDTO);
  void updateUser(UserDTO userDTO);
  UserDTO findUserByUserId(UUID userId);
  UserDTO findUserByEmail(String email);
  boolean existsByEmail(String email);

}
