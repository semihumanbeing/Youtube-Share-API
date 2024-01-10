package com.youtubeshareapi.user.controller;

import com.youtubeshareapi.common.ResponseDTO;
import com.youtubeshareapi.exception.AuthException;
import com.youtubeshareapi.user.model.LoginRequest;
import com.youtubeshareapi.user.model.RegisterRequest;
import com.youtubeshareapi.user.model.TokenDTO;
import com.youtubeshareapi.user.model.UserDTO;
import com.youtubeshareapi.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/login")
  public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response,
      @Valid @RequestBody LoginRequest loginRequest) {
    if (!userService.existsByEmail(loginRequest.getEmail())) {
      throw new AuthException("cannot find user");
    }
    TokenDTO tokenDTO = userService.login(loginRequest.getEmail(), loginRequest.getPassword());

    Cookie cookie = new Cookie("jwt", tokenDTO.getAccessToken());
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    response.addCookie(cookie);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.builder()
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .data(tokenDTO)
            .build());
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
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
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
    userService.register(UserDTO.builder()
        .email(registerRequest.getEmail())
        .username(registerRequest.getUsername())
        .password(registerRequest.getPassword())
        .build());
    return ResponseEntity.status(HttpStatus.OK).body("ok");

  }
}