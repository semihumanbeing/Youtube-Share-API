package com.youtubeshareapi.user.controller;

import com.youtubeshareapi.common.ResponseDTO;
import com.youtubeshareapi.exception.AuthException;
import com.youtubeshareapi.exception.ErrorCode;
import com.youtubeshareapi.user.model.LoginRequest;
import com.youtubeshareapi.user.model.LoginResponse;
import com.youtubeshareapi.user.model.RefreshRequest;
import com.youtubeshareapi.user.model.RegisterRequest;
import com.youtubeshareapi.user.model.TokenDTO;
import com.youtubeshareapi.user.model.UserDTO;
import com.youtubeshareapi.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.sql.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/login")
  public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response,
      @Valid @RequestBody LoginRequest loginRequest) {

    log.info("---------login");

    if (!userService.existsByEmail(loginRequest.getEmail())) {
      throw new AuthException(ErrorCode.USER_NOT_FOUND);
    }
    TokenDTO tokenDTO = userService.login(loginRequest.getEmail(), loginRequest.getPassword());

    Cookie cookie = new Cookie("jwt", tokenDTO.getAccessToken());
    cookie.setHttpOnly(true);
    // cookie.setSecure(true);
    cookie.setPath("/");
    response.addCookie(cookie);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.builder()
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .data(LoginResponse.builder()
                .userId(tokenDTO.getUserId())
                .username(tokenDTO.getUsername())
                .accessToken(tokenDTO.getAccessToken())
                .refreshToken(tokenDTO.getRefreshToken())
                .createdAt(tokenDTO.getCreatedAt())
                .build())
            .build());
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

    log.info("---------logout");
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("jwt".equals(cookie.getName())) {
          cookie.setValue("");
          cookie.setPath("/");
          cookie.setMaxAge(0);
          response.addCookie(cookie);
        }
      }
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.builder()
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .data(null)
            .build());
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest)
      throws IllegalAccessException {

    log.info("---------register");
    if(registerRequest.getUsername().length() > 10) {
      throw new IllegalAccessException("username is too long");
    }
    userService.register(UserDTO.builder()
        .email(registerRequest.getEmail())
        .username(registerRequest.getUsername())
        .password(registerRequest.getPassword())
        .build());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResponseDTO.builder()
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .data(null)
            .build());
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest refreshRequest) {
    log.info("---------refreshToken");
    TokenDTO refreshTokenDTO = userService.refreshAccessToken(refreshRequest.getRefreshToken());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResponseDTO.builder()
            .data(refreshTokenDTO)
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .build());
  }
}
