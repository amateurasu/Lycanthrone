package com.hey.repository;

import com.hey.model.*;
import io.vertx.core.Future;

import java.util.List;

public interface DataRepository {

    Future<List<String>> getKeysByPattern(String keyPattern);

    Future<UserAuth> insertUserAuth(UserAuth userAuth);

    Future<UserAuth> getUserAuth(String userName);

    Future<UserFull> insertUserFull(UserFull userFull);

    Future<UserFull> getUserFull(String userId);

    Future<UserStatus> insertUserStatus(UserStatus userStatus);

    Future<UserStatus> getUserStatus(String userId);

    Future<FriendList> insertFriendList(FriendList friendList);

    Future<FriendList> getFriendList(String friendListKey, String currentUserId);

    Future<ChatList> insertChatList(ChatList chatList);

    Future<ChatList> getChatList(String chatListKey);

    Future<ChatMessage> insertChatMessage(ChatMessage chatMessage);

    Future<ChatMessage> getChatMessage(String chatMessageKey);

    Future<Long> increaseUnseenCount(String userId, String sessionId);

    Future<Long> getUnseenCount(String userId, String sessionId);

    Future<Long> deleteUnseenCount(String userId, String sessionId);

    Future<Long> deleteFriend(String userId, String friendId);
}
