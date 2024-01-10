package com.youtubeshareapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {

  public AuthException(String message) {
    super(message);
  }
}
