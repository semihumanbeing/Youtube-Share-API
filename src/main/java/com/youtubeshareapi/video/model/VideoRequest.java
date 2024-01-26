package com.youtubeshareapi.video.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoRequest {
  private String chatroomId;
  private String playlistId;
  private int videoId;
  private Long userId;
  private String url;
}
