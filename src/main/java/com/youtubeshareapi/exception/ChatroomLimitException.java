package com.youtubeshareapi.exception;

import lombok.Getter;

@Getter
public class ChatroomLimitException extends RuntimeException {

  public ChatroomLimitException(String message) {
    super(message);
  }
}
