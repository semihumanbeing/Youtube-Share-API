package com.youtubeshareapi.video.entity;

import com.youtubeshareapi.video.model.PlaylistDTO;

import java.util.UUID;

public interface PlaylistRepositoryCustom {
    PlaylistDTO findPlaylistByChatroomId(UUID chatroomId);
}
