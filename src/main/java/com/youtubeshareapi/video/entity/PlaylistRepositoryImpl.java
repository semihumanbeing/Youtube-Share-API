package com.youtubeshareapi.video.entity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.youtubeshareapi.video.model.PlaylistDTO;
import com.youtubeshareapi.video.model.VideoDTO;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.youtubeshareapi.chat.entity.QChatroom.chatroom;
import static com.youtubeshareapi.video.entity.QPlaylist.playlist;
import static com.youtubeshareapi.video.entity.QVideo.video;

public class PlaylistRepositoryImpl implements PlaylistRepositoryCustom {

    private final JPAQueryFactory query;

    public PlaylistRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public PlaylistDTO findPlaylistByChatroomId(UUID chatroomId) {
        List<VideoDTO> videos = query
                .select(Projections.constructor(
                        VideoDTO.class,
                        video.videoId,
                        video.playlist.playlistId,
                        video.userId,
                        video.url,
                        video.title,
                        video.artist,
                        video.isCurrent
                ))
                .from(video)
                .leftJoin(video.playlist, playlist)
                .leftJoin(playlist.chatroomId, chatroom)
                .where(chatroom.chatroomId.eq(chatroomId))
                .orderBy(video.videoId.desc())
                .fetch();

        PlaylistDTO playlistDTO = query
                .select(Projections.constructor(
                        PlaylistDTO.class,
                        playlist.playlistId,
                        playlist.chatroomId.chatroomId,
                        playlist.playlistName,
                        playlist.isActive
                ))
                .from(playlist)
                .leftJoin(playlist.chatroomId, chatroom)
                .where(chatroom.chatroomId.eq(chatroomId))
                .fetchOne();

        if (playlistDTO != null) {
            playlistDTO.setVideos(videos);
        }

        return playlistDTO;
    }
}
