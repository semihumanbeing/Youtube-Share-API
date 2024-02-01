package com.youtubeshareapi.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1908739632L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final ListPath<com.youtubeshareapi.chat.entity.Chatroom, com.youtubeshareapi.chat.entity.QChatroom> chatrooms = this.<com.youtubeshareapi.chat.entity.Chatroom, com.youtubeshareapi.chat.entity.QChatroom>createList("chatrooms", com.youtubeshareapi.chat.entity.Chatroom.class, com.youtubeshareapi.chat.entity.QChatroom.class, PathInits.DIRECT2);

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final StringPath email = createString("email");

    public final StringPath password = createString("password");

    public final QToken token;

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath username = createString("username");

    public final StringPath userRole = createString("userRole");

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.token = inits.isInitialized("token") ? new QToken(forProperty("token"), inits.get("token")) : null;
    }

}

