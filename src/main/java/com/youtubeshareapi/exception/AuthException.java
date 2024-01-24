package com.youtubeshareapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {
  private final String errorCode;

  public AuthException(ErrorCode errorCodeEnum) {
    super(errorCodeEnum.getMessage());
    this.errorCode = errorCodeEnum.getCode();
  }
}
