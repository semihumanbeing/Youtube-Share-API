package com.youtubeshareapi.user.service;

import com.youtubeshareapi.exception.AuthException;
import com.youtubeshareapi.exception.ErrorCode;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public TokenDTO login(String email, String password) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(()-> new AuthException(ErrorCode.USER_NOT_FOUND));
    boolean loginSucceed = passwordEncoder.matches(password, user.getPassword());
    if(!loginSucceed) throw new AuthException(ErrorCode.WRONG_PASSWORD);

    Token token = tokenRepository.findByUserId(user.getUserId());
    if (token == null) {
      token = jwtTokenProvider.generateToken(user);
    } else {
      Token generatedToken = jwtTokenProvider.generateToken(user);
      token.setAccessToken(generatedToken.getAccessToken());
      token.setRefreshToken(generatedToken.getRefreshToken());
    }
    token = tokenRepository.save(token);

    return TokenDTO.builder()
        .userId(user.getUserId())
        .username(user.getUsername())
        .accessToken(token.getAccessToken())
        .refreshToken(token.getRefreshToken())
        .createdAt(token.getCreatedAt())
        .build();
  }
  @Transactional
  @Override
  public void register(UserDTO userDTO) {
    if(userRepository.existsByEmail(userDTO.getEmail())){
      throw new AuthException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    User user = User.builder()
        .email(userDTO.getEmail())
        .username(userDTO.getUsername())
        .password(passwordEncoder.encode(userDTO.getPassword()))
        .userRole("USER")
        .build();
    userRepository.save(user);
  }

  @Transactional
  @Override
  public void updateUser(UserDTO userDTO) {
    User user = userRepository.getReferenceById(userDTO.getUserId());
    user.setUsername(userDTO.getUsername());
    user.setPassword(userDTO.getPassword());
  }

  @Override
  public UserDTO findUserByUserId(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
    return UserDTO.of(user);

  }

  @Override
  public UserDTO findUserByEmail(String email) {
    User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
    return UserDTO.of(user);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public TokenDTO refreshAccessToken(String refreshToken) {
    Long userId = getUserIdFromToken(refreshToken);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new AuthException(ErrorCode.TOKEN_USER_NOT_FOUND));

    Token token = tokenRepository.findByUserId(userId);
    Token newToken = jwtTokenProvider.generateToken(user);

    token.setAccessToken(newToken.getAccessToken());
    token.setRefreshToken(newToken.getRefreshToken());

    return TokenDTO.builder()
        .userId(user.getUserId())
        .username(user.getUsername())
        .accessToken(token.getAccessToken())
        .refreshToken(token.getRefreshToken())
        .createdAt(token.getCreatedAt())
        .build();
  }
  private Long getUserIdFromToken(String token){
    return Long.parseLong(jwtTokenProvider.parseClaims(token).getSubject());
  }
}
