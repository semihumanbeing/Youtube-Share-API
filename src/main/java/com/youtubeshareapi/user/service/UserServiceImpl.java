package com.youtubeshareapi.user.service;

import com.youtubeshareapi.security.JwtTokenProvider;
import com.youtubeshareapi.user.entity.Token;
import com.youtubeshareapi.user.entity.User;
import com.youtubeshareapi.user.entity.UserRepository;
import com.youtubeshareapi.user.model.TokenDTO;
import com.youtubeshareapi.user.model.UserDTO;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final JwtTokenProvider jwtTokenProvider;
  @Override
  public TokenDTO login(String email, String password) {
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
    Token token = jwtTokenProvider.generateToken(authentication);
    return TokenDTO.builder()
        .accessToken(token.getAccessToken())
        .refreshToken(token.getRefreshToken())
        .user(userRepository.findByEmail(email).orElseThrow(RuntimeException::new))
        .build();
  }
  @Override
  public void register(UserDTO userDTO) {
    User user = User.builder()
        .email(userDTO.getEmail())
        .username(userDTO.getUsername())
        .password(userDTO.getPassword())
        .build();
    userRepository.save(user);
  }

  @Override
  public void updateUser(UserDTO userDTO) {
    User user = userRepository.getReferenceById(userDTO.getUserId());
    user.setUsername(userDTO.getUsername());
    user.setPassword(userDTO.getPassword());
    userRepository.save(user);
  }

  @Override
  public UserDTO findUserByUserId(UUID userId) {
    User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
    return UserDTO.builder()
        .userId(user.getUserId())
        .username(user.getUsername())
        .password(user.getPassword())
        .email(user.getEmail())
        .createdAt(user.getCreatedAt())
        .build();
  }

  @Override
  public UserDTO findUserByEmail(String email) {
    User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
    return UserDTO.builder()
        .userId(user.getUserId())
        .username(user.getUsername())
        .password(user.getPassword())
        .email(user.getEmail())
        .createdAt(user.getCreatedAt())
        .build();
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

}
