package com.youtubeshareapi.chat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatroom is a Querydsl query type for Chatroom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatroom extends EntityPathBase<Chatroom> {

    private static final long serialVersionUID = -1538261595L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatroom chatroom = new QChatroom("chatroom");

    public final ComparablePath<java.util.UUID> chatroomId = createComparable("chatroomId", java.util.UUID.class);

    public final StringPath chatroomName = createString("chatroomName");

    public final StringPath chatroomPassword = createString("chatroomPassword");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final StringPath emoji = createString("emoji");

    public final BooleanPath hasPwd = createBoolean("hasPwd");

    public final NumberPath<Integer> maxUserCount = createNumber("maxUserCount", Integer.class);

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public final com.youtubeshareapi.user.entity.QUser user;

    public final NumberPath<Integer> userCount = createNumber("userCount", Integer.class);

    public QChatroom(String variable) {
        this(Chatroom.class, forVariable(variable), INITS);
    }

    public QChatroom(Path<? extends Chatroom> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatroom(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatroom(PathMetadata metadata, PathInits inits) {
        this(Chatroom.class, metadata, inits);
    }

    public QChatroom(Class<? extends Chatroom> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.youtubeshareapi.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

