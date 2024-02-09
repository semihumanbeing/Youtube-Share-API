package com.youtubeshareapi.video.entity;

import com.youtubeshareapi.chat.entity.Chatroom;
import com.youtubeshareapi.video.model.PlaylistDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> , PlaylistRepositoryCustom{
}
