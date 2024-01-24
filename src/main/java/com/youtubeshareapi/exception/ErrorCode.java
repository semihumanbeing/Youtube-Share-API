package com.youtubeshareapi.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  USER_NOT_FOUND("001", "cannot find user"),
  WRONG_PASSWORD("002", "password is wrong"),
  EMAIL_ALREADY_EXISTS("003", "email is already in use"),
  TOKEN_USER_NOT_FOUND("004", "no user found for the provided token");

  private final String code;
  private final String message;

  ErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }


}
