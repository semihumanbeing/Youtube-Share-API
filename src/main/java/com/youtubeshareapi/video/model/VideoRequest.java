package com.youtubeshareapi.video.model;

import lombok.Data;

@Data
public class VideoRequest {
    private String url;
    private String title;
    private String artist;
}
