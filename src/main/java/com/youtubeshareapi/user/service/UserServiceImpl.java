package com.youtubeshareapi.user.service;

import com.youtubeshareapi.exception.AuthException;
import com.youtubeshareapi.security.JwtTokenProvider;
import com.youtubeshareapi.user.entity.Token;
import com.youtubeshareapi.user.entity.TokenRepository;
import com.youtubeshareapi.user.entity.User;
import com.youtubeshareapi.user.entity.UserRepository;
import com.youtubeshareapi.user.model.TokenDTO;
import com.youtubeshareapi.user.model.UserDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Transactional
  @Override
  public TokenDTO login(String email, String password) {
    User user = userRepository.findByEmailAndPassword(email, password)
        .orElseThrow(()-> new AuthException("wrong password"));

    Token token = user.getToken();
    if(token == null){
      token = jwtTokenProvider.generateToken(user);
      token = tokenRepository.save(token);
    }
    return TokenDTO.builder()
        .userId(user.getUserId())
        .accessToken(token.getAccessToken())
        .refreshToken(token.getRefreshToken())
        .createdAt(token.getCreatedAt())
        .build();
  }
  @Override
  public void register(UserDTO userDTO) {
    User user = User.builder()
        .email(userDTO.getEmail())
        .username(userDTO.getUsername())
        .password(userDTO.getPassword())
        .userRole("USER")
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
  public UserDTO findUserByUserId(Long userId) {
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
