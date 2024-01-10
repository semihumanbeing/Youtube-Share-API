package com.youtubeshareapi.common;

import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
  private T data;
  private Timestamp timestamp;
  private String errorCode;
  private String errorMsg;

}
