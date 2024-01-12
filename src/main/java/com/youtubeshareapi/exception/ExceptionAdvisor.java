package com.youtubeshareapi.exception;

import com.youtubeshareapi.common.ResponseDTO;
import java.sql.Timestamp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvisor {

  @ExceptionHandler(ChatroomLimitException.class)
  public ResponseEntity<?> handleDeviceException(ChatroomLimitException exception) {
    return setResponseFromException(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
  }

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<?> handleDeviceException(AuthException exception) {
    return setResponseFromException(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<?> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex){
    return setResponseFromException(HttpStatus.BAD_REQUEST, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
  }

  private <E extends Exception> ResponseEntity<?> setResponseFromException(HttpStatus httpStatus,
      E exception) {
    return ResponseEntity.status(httpStatus)
        .body(ResponseDTO.builder()
            .data(null)
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .errorCode(httpStatus.toString())
            .errorMsg(exception.getMessage())
            .build());
  }
  private <E extends Exception> ResponseEntity<?> setResponseFromException(HttpStatus httpStatus,
      String errorMsg) {
    return ResponseEntity.status(httpStatus)
        .body(ResponseDTO.builder()
            .data(null)
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .errorCode(httpStatus.toString())
            .errorMsg(errorMsg)
            .build());

  }

}
