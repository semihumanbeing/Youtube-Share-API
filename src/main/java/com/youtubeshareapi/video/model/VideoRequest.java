package com.youtubeshareapi.video.model;

import lombok.Data;

@Data
public class VideoRequest {
    private String url;
    private String title;
    private String artist;
    private String username;
    private String thumbnailImg;
    private int thumbnailWidth;
    private int thumbnailHeight;
}
