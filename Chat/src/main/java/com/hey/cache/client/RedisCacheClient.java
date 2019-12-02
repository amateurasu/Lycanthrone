package com.hey.cache.client;

import com.hey.model.*;
import com.hey.repository.DataRepository;
import com.hey.util.PropertiesUtils;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.op.ScanOptions;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class RedisCacheClient implements DataRepository {
    private static int numScanCount;
    private RedisClient client;

    public RedisCacheClient(RedisClient client) {
        this.client = client;
        numScanCount = Integer.parseInt(PropertiesUtils.getInstance().getValue("scan.count"));
    }

    @Override
    public Future<List<String>> getKeysByPattern(String keyPattern) {
        Promise<List<String>> promise = Promise.promise();
        List<String> keys = new ArrayList<>();

        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setCount(numScanCount);
        scanOptions.setMatch(keyPattern);
        client.scan("0", scanOptions, res -> {
            if (res.failed()) {
                promise.fail(res.cause());
                return;
            }

            JsonArray jsonArray = (JsonArray) res.result().getList().get(1);
            jsonArray.forEach(object -> {
                if (object instanceof String) {
                    keys.add((String) object);
                }
            });

            promise.complete(keys);
        });

        return promise.future();
    }

    @Override
    public Future<UserAuth> insertUserAuth(UserAuth userAuth) {
        Promise<UserAuth> promise = Promise.promise();

        JsonObject userAuthJsonObject = new JsonObject();
        userAuthJsonObject.put("user_id", userAuth.getUserId());
        userAuthJsonObject.put("hashed_password", userAuth.getHashedPassword());

        client.hmset(generateUserAuthKey(userAuth.getUserName()), userAuthJsonObject, resInsertUserAuth -> {
            if (resInsertUserAuth.succeeded()) {
                promise.complete(userAuth);
            } else {
                promise.fail(resInsertUserAuth.cause());
            }
        });

        return promise.future();
    }

    @Override
    public Future<UserAuth> getUserAuth(String userName) {
        Promise<UserAuth> promise = Promise.promise();

        client.hgetall(generateUserAuthKey(userName), res -> {
            if (!res.succeeded()) {
                promise.fail(res.cause());
                return;
            }

            if (res.result().getString("user_id") == null) {
                promise.complete(null);
                return;
            }

            UserAuth userAuth = new UserAuth();
            userAuth.setUserName(userName);
            userAuth.setUserId(res.result().getString("user_id"));
            userAuth.setHashedPassword(res.result().getString("hashed_password"));
            promise.complete(userAuth);
        });

        return promise.future();
    }

    @Override
    public Future<UserFull> insertUserFull(UserFull userFull) {
        Promise<UserFull> promise = Promise.promise();

        JsonObject userFullJsonObject = new JsonObject();
        userFullJsonObject.put("user_name", userFull.getUserName());
        userFullJsonObject.put("full_name", userFull.getFullName());

        client.hmset(generateUserFullKey(userFull.getUserId()), userFullJsonObject, resInsertUserFull -> {
            if (resInsertUserFull.succeeded()) {
                promise.complete(userFull);
            } else {
                promise.fail(resInsertUserFull.cause());
            }
        });

        return promise.future();
    }

    @Override
    public Future<UserFull> getUserFull(String userId) {

        Promise<UserFull> promise = Promise.promise();

        client.hgetall(generateUserFullKey(userId), res -> {
            if (res.succeeded()) {
                if (res.result().getString("user_name") != null) {
                    UserFull userFull = new UserFull();
                    userFull.setUserId(userId);
                    userFull.setUserName(res.result().getString("user_name"));
                    userFull.setFullName(res.result().getString("full_name"));

                    promise.complete(userFull);
                } else {
                    promise.complete(null);
                }
            } else {
                promise.fail(res.cause());
            }
        });

        return promise.future();
    }

    @Override
    public Future<UserStatus> insertUserStatus(UserStatus userStatus) {

        Promise<UserStatus> promise = Promise.promise();

        client.set(generateUserStatusKey(userStatus.getUserId()), userStatus.getStatus(), res -> {
            if (res.succeeded()) {
                promise.complete(userStatus);
            } else {
                promise.fail(res.cause());
            }
        });

        return promise.future();
    }

    @Override
    public Future<UserStatus> getUserStatus(String userId) {

        Promise<UserStatus> promise = Promise.promise();

        client.get(generateUserStatusKey(userId), res -> {
            if (!res.succeeded()) {
                promise.fail(res.cause());
                return;
            }

            if (res.result() == null) {
                promise.complete(null);
                return;
            }

            UserStatus userStatus = new UserStatus();
            userStatus.setUserId(userId);
            userStatus.setStatus(res.result());

            promise.complete(userStatus);
        });

        return promise.future();
    }

    @Override
    public Future<FriendList> insertFriendList(FriendList friendList) {

        Promise<FriendList> promise = Promise.promise();

        JsonObject friendListJsonObject = new JsonObject();
        List<String> userIds = new ArrayList<>();
        userIds.add(friendList.getCurrentUserHashes().getUserId());
        userIds.add(friendList.getFriendUserHashes().getUserId());
        friendListJsonObject.put(friendList.getCurrentUserHashes().getUserId(), friendList.getCurrentUserHashes().getFullName());
        friendListJsonObject.put(friendList.getFriendUserHashes().getUserId(), friendList.getFriendUserHashes().getFullName());

        client.hmset(generateFriendListKey(userIds), friendListJsonObject, res -> {
            if (res.succeeded()) {
                promise.complete(friendList);
            } else {
                promise.fail(res.cause());
            }
        });

        return promise.future();
    }

    @Override
    public Future<FriendList> getFriendList(String friendListKey, String currentUserId) {

        Promise<FriendList> promise = Promise.promise();

        client.hgetall(friendListKey, res -> {
            if (!res.succeeded()) {
                promise.fail(res.cause());
                return;
            }

            Set<String> fieldNames = res.result().fieldNames();

            if (fieldNames.size() != 2) {
                promise.complete(null);
                return;
            }

            FriendList friendList = new FriendList();

            for (String fieldName : fieldNames) {
                if (currentUserId.equals(fieldName)) {
                    friendList.setCurrentUserHashes(new UserHash(fieldName, res.result().getString(fieldName)));
                } else {
                    friendList.setFriendUserHashes(new UserHash(fieldName, res.result().getString(fieldName)));
                }
            }

            promise.complete(friendList);
        });

        return promise.future();
    }

    @Override
    public Future<ChatList> insertChatList(ChatList chatList) {

        Promise<ChatList> promise = Promise.promise();

        JsonObject chatListJsonObject = new JsonObject();
        String updatedDate = String.valueOf(chatList.getUpdatedDate() != null ? chatList.getUpdatedDate().getTime() : new Date().getTime());
        chatListJsonObject.put("updated_date", updatedDate);
        List<String> userIds = new ArrayList<>();
        for (UserHash userHash : chatList.getUserHashes()) {
            chatListJsonObject.put(userHash.getUserId(), userHash.getFullName());
            userIds.add(userHash.getUserId());
        }

        chatListJsonObject.put("last_message", StringUtils.isEmpty(chatList.getLastMessage()) ? "no message" : chatList.getLastMessage());

        client.hmset(generateChatListKey(chatList.getSessionId(), userIds), chatListJsonObject, res -> {
            if (res.succeeded()) {
                promise.complete(chatList);
            } else {
                promise.fail(res.cause());
            }
        });

        return promise.future();
    }

    @Override
    public Future<ChatList> getChatList(String chatListKey) {

        Promise<ChatList> promise = Promise.promise();

        client.hgetall(chatListKey, res -> {
            if (res.failed()) {
                promise.fail(res.cause());
                return;
            }

            ChatList chatList = convertJsonObjectToChatList(res.result(), chatListKey);
            if (chatList.getUserHashes().size() >= 2) {
                promise.complete(chatList);
            } else {
                promise.complete(null);
            }
        });

        return promise.future();
    }

    @Override
    public Future<ChatMessage> insertChatMessage(ChatMessage chatMessage) {

        Promise<ChatMessage> promise = Promise.promise();

        // Insert new message
        JsonObject chatMessageJsonObject = new JsonObject();
        chatMessageJsonObject.put(chatMessage.getUserHash().getUserId(), chatMessage.getUserHash().getFullName());
        chatMessageJsonObject.put("message", chatMessage.getMessage());
        chatMessageJsonObject.put("created_date", String.valueOf(chatMessage.getCreatedDate().getTime()));

        client.hmset(generateChatMessageKey(chatMessage.getSessionId(), String.valueOf(chatMessage.getCreatedDate().getTime())), chatMessageJsonObject, resInsertChatMessage -> {
            if (resInsertChatMessage.succeeded()) {

                promise.complete(chatMessage);
            } else {
                promise.fail(resInsertChatMessage.cause());
            }
        });

        return promise.future();
    }

    @Override
    public Future<ChatMessage> getChatMessage(String chatMessageKey) {
        Promise<ChatMessage> promise = Promise.promise();

        client.hgetall(chatMessageKey, res -> {
            if (res.failed()) {
                promise.fail(res.cause());
                return;
            }

            if (res.result().getString("message") == null) {
                promise.complete(null);
                return;
            }

            ChatMessage chatMessage = new ChatMessage();
            Set<String> fieldNames = res.result().fieldNames();
            for (String fieldName : fieldNames) {
                switch (fieldName) {
                    case "message":
                        chatMessage.setMessage(res.result().getString(fieldName));
                        break;
                    case "created_date":
                        chatMessage.setCreatedDate(new Date(Long.parseLong(res.result().getString(fieldName))));
                        break;
                    default:
                        chatMessage.setUserHash(new UserHash(fieldName, res.result().getString(fieldName)));
                        break;
                }
            }

            String[] componentKey = chatMessageKey.split(":");
            if (componentKey.length > 3) {
                chatMessage.setSessionId(componentKey[2]);
            }

            promise.complete(chatMessage);
        });

        return promise.future();
    }

    @Override
    public Future<Long> increaseUnseenCount(String userId, String sessionId) {

        Promise<Long> promise = Promise.promise();

        client.exists(generateUnSeenKey(userId, sessionId), checkExistsKey -> {
            if (checkExistsKey.succeeded()) {

                client.incr(generateUnSeenKey(userId, sessionId), increaseKey -> {
                    if (increaseKey.succeeded()) {

                        promise.complete(increaseKey.result());
                    } else {
                        promise.fail(increaseKey.cause());
                    }
                });
            } else {
                promise.fail(checkExistsKey.cause());
            }
        });

        return promise.future();
    }

    @Override
    public Future<Long> getUnseenCount(String userId, String sessionId) {
        Promise<Long> promise = Promise.promise();

        client.get(generateUnSeenKey(userId, sessionId), getUnseenCountRes -> {
            if (getUnseenCountRes.succeeded()) {
                if (getUnseenCountRes.result() != null) {
                    promise.complete(Long.parseLong(getUnseenCountRes.result()));
                } else {
                    promise.complete(Long.parseLong("0"));
                }
            } else {
                promise.fail(getUnseenCountRes.cause());
            }
        });

        return promise.future();
    }

    @Override
    public Future<Long> deleteUnseenCount(String userId, String sessionId) {
        Promise<Long> promise = Promise.promise();

        client.del(generateUnSeenKey(userId, sessionId), deleteUnseenCountRes -> {
            if (deleteUnseenCountRes.succeeded()) {
                promise.complete(deleteUnseenCountRes.result());
            } else {
                promise.fail(deleteUnseenCountRes.cause());
            }
        });

        return promise.future();
    }

    @Override
    public Future<Long> deleteFriend(String userId, String friendId) {
        Promise<Long> promise = Promise.promise();
        client.del(generateFriendListKey(userId, friendId), deleteFriendRes -> {
            if (deleteFriendRes.succeeded()) {
                promise.complete(deleteFriendRes.result());
            } else {
                promise.fail(deleteFriendRes.cause());
            }
        });

        return promise.future();
    }

    private String generateUserAuthKey(String userName) {
        return "user:" + userName;
    }

    private String generateUserFullKey(String userId) {
        return "user_full:" + userId;
    }

    private String generateFriendListKey(List<String> userIds) {
        return "friend:list:" + String.join(":", userIds);
    }

    private String generateFriendListKey(String userId1, String userId2) {
        return "friend:list:" + userId1 + ":" + userId2;
    }

    private String generateUserStatusKey(String userId) {
        return "user_status:" + userId;
    }

    private String generateChatListKey(String sessionId, List<String> userIds) {
        return "chat:list:" + sessionId + ":" + String.join(":", userIds);
    }

    private String generateChatMessageKey(String sessionId, String createdDate) {

        return "chat:message:" + sessionId + ":" + createdDate;
    }

    private String generateUnSeenKey(String userId, String sessionId) {

        return "unseen:" + userId + ":" + sessionId;
    }

    private ChatList convertJsonObjectToChatList(JsonObject jsonObject, String chatListkey) {
        ChatList chatList = new ChatList();
        List<UserHash> userHashes = new ArrayList<>();
        Set<String> fieldNames = jsonObject.fieldNames();
        for (String fieldName : fieldNames) {
            switch (fieldName) {
                case "updated_date":
                    chatList.setUpdatedDate(new Date(Long.parseLong(jsonObject.getString(fieldName))));
                    break;
                case "last_message":
                    chatList.setLastMessage(jsonObject.getString(fieldName));
                    break;
                default:
                    UserHash userHash = new UserHash(fieldName, jsonObject.getString(fieldName));
                    userHashes.add(userHash);
                    break;
            }
        }

        chatList.setUserHashes(userHashes);
        String[] componentKey = chatListkey.split(":");
        if (componentKey.length > 3) {
            chatList.setSessionId(componentKey[2]);
        }

        return chatList;
    }
}
