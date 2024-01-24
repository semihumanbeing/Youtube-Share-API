package com.youtubeshareapi.exception;

import com.youtubeshareapi.common.ResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
import java.sql.Timestamp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvisor {
  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<?> ExpiredJwtException(ExpiredJwtException exception) {
    return setResponseFromException(HttpStatus.UNAUTHORIZED, exception.getLocalizedMessage());
  }
  @ExceptionHandler(ChatroomLimitException.class)
  public ResponseEntity<?> ChatroomLimitException(ChatroomLimitException exception) {
    return setResponseFromException(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
  }

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<?> AuthException(AuthException exception) {
    return setResponseFromException(HttpStatus.BAD_REQUEST, exception, exception.getErrorCode());
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<?> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex){
    return setResponseFromException(HttpStatus.BAD_REQUEST, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
  }

  private <E extends Exception> ResponseEntity<?> setResponseFromException(HttpStatus httpStatus,
      E exception, String errorCode) {
    return ResponseEntity.status(httpStatus)
        .body(ResponseDTO.builder()
            .data(null)
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .errorCode(errorCode)
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
