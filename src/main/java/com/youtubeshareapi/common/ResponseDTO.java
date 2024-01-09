package com.youtubeshareapi.common;

import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
  private int httpStatus;
  private List<T> data;
  private Timestamp timestamp;
  private String errorCode;
  private String errorMsg;

}
