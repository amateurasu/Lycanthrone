package com.hey.service;

import com.hey.manager.JwtManager;
import com.hey.model.*;
import com.hey.repository.DataRepository;
import com.hey.util.GenerationUtils;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.ajbrown.namemachine.Gender;
import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
import org.apache.commons.lang3.RandomUtils;
import org.mindrot.jbcrypt.BCrypt;
import se.emirbuc.randomsentence.RandomSentences;

import java.util.*;

public abstract class BaseService {
    public static final String AUTHENTICATION_SCHEME = "Bearer";

    @Getter
    @Setter
    protected DataRepository dataRepository;
    protected JwtManager jwtManager;

    public void setJwtManager(JwtManager jwtManager) {
        this.jwtManager = jwtManager;
    }

    public Future<User> insertUser(User user) {
        Promise<User> promise = Promise.promise();

        UserAuth userAuth = new UserAuth();
        user.setUserId(user.getUserId() != null ? user.getUserId() : GenerationUtils.generateId());
        userAuth.setUserName(user.getUserName());
        userAuth.setUserId(user.getUserId());
        userAuth.setHashedPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(5)));

        UserFull userFull = new UserFull();
        userFull.setUserId(user.getUserId());
        userFull.setUserName(user.getUserName());
        userFull.setFullName(user.getFullName());

        List<Future> insertUserAuthAndUserFullFuture = new ArrayList<>();
        insertUserAuthAndUserFullFuture.add(dataRepository.insertUserAuth(userAuth));
        insertUserAuthAndUserFullFuture.add(dataRepository.insertUserFull(userFull));

        CompositeFuture.all(insertUserAuthAndUserFullFuture).setHandler(asyncResult -> {
            if (asyncResult.succeeded()) {
                promise.complete(user);
            } else {
                promise.fail(asyncResult.cause());
            }
        });

        return promise.future();
    }

    public Future<ChatMessage> insertChatMessagesAndUpdateChatListAndUpdateUnseenCount(ChatMessage chatMessage) {
        Promise<ChatMessage> promise = Promise.promise();

        // Find chat list key by session id
        String keyPattern = "chat:list:" + chatMessage.getSessionId() + "*";
        Future<List<String>> getKeysByPatternFuture = dataRepository.getKeysByPattern(keyPattern);
        getKeysByPatternFuture.compose(chatListKeys -> {

            // Get current chat list for update later
            Future<ChatList> getChatListFuture = dataRepository.getChatList(chatListKeys.get(0));
            getChatListFuture.compose(chatList -> {

                // Insert new chat message
                Future<ChatMessage> insertChatMessageFuture = dataRepository.insertChatMessage(chatMessage);

                // Update chat list
                chatList.setLastMessage(chatMessage.getMessage());
                chatList.setUpdatedDate(chatMessage.getCreatedDate());
                Future<ChatList> insertChatListFuture = dataRepository.insertChatList(chatList);

                // increase unseen count
                List<String> userFriendIds = new ArrayList<>();
                for (UserHash userHash : chatList.getUserHashes()) {
                    if (!chatMessage.getUserHash().getUserId().equals(userHash.getUserId())) {
                        userFriendIds.add(userHash.getUserId());
                    }
                }
                Future<HashMap<String, Long>> increaseUnseenCountFuture = increaseUnseenCount(userFriendIds, chatList.getSessionId());

                CompositeFuture cp = CompositeFuture.all(insertChatListFuture, insertChatMessageFuture, increaseUnseenCountFuture);
                cp.setHandler(ar -> {
                    if (ar.failed()) {
                        throw new RuntimeException(ar.cause());
                    }

                    promise.complete(chatMessage);
                });
            }, Future.future().setHandler(handler -> promise.fail(handler.cause())));
        }, Future.future().setHandler(handler -> promise.fail(handler.cause())));

        return promise.future();
    }

    public Future<HashMap<String, Long>> increaseUnseenCount(List<String> userIds, String sessionId) {
        Promise<HashMap<String, Long>> promise = Promise.promise();
        List<Future> increaseUnseenCountFutures = new ArrayList<>();

        for (String userId : userIds) {
            increaseUnseenCountFutures.add(dataRepository.increaseUnseenCount(userId, sessionId));
        }

        CompositeFuture cp = CompositeFuture.all(increaseUnseenCountFutures);
        cp.setHandler(ar -> {
            if (ar.failed()) {
                promise.fail(ar.cause());
                return;
            }

            HashMap<String, Long> userIdToUnseenCountMap = new HashMap<>();

            for (int index = 0; index < increaseUnseenCountFutures.size(); ++index) {
                Long unSeenCount = cp.resultAt(index);
                userIdToUnseenCountMap.put(userIds.get(index), unSeenCount);
            }

            promise.complete(userIdToUnseenCountMap);
        });

        return promise.future();
    }

    public Future<JsonObject> initTestData() {
        JsonObject jsonObject = new JsonObject();
        Promise<JsonObject> promise = Promise.promise();
        List<Future> insertAllFutures = new ArrayList<>();
        Future<List<User>> createUsersFuture = createUsers();

        createUsersFuture.compose(users -> {
            jsonObject.put("users", users);
            List<Future> insertUserFutures = new ArrayList<>();
            for (User user : users) {
                insertUserFutures.add(insertUser(user));
            }
            insertAllFutures.addAll(insertUserFutures);

            Future<List<UserStatus>> createUserStatusesFuture = createUserStatuses(users);
            Future<List<FriendList>> createFriendListsFuture = createFriendLists(users);
            Future<List<ChatList>> createChatListsFuture = createChatLists(users);

            CompositeFuture cp = CompositeFuture.all(createUserStatusesFuture, createFriendListsFuture, createChatListsFuture);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {
                    List<UserStatus> userStatuses = cp.resultAt(0);
                    jsonObject.put("userStatuses", userStatuses);
                    List<Future> insertUserStatusFutures = new ArrayList<>();
                    for (UserStatus userStatus : userStatuses) {
                        insertUserStatusFutures.add(dataRepository.insertUserStatus(userStatus));
                    }
                    insertAllFutures.addAll(insertUserStatusFutures);

                    List<FriendList> friendLists = cp.resultAt(1);
                    jsonObject.put("friendLists", friendLists);
                    List<Future> insertFriendListFutures = new ArrayList<>();
                    for (FriendList friendList : friendLists) {
                        insertFriendListFutures.add(dataRepository.insertFriendList(friendList));
                    }
                    insertAllFutures.addAll(insertFriendListFutures);

                    List<ChatList> chatLists = cp.resultAt(2);
                    jsonObject.put("chatLists", chatLists);
                    List<Future> insertChatListFutures = new ArrayList<>();
                    for (ChatList chatList : chatLists) {
                        insertChatListFutures.add(dataRepository.insertChatList(chatList));
                    }
                    insertAllFutures.addAll(insertChatListFutures);

                    Future<List<ChatMessage>> createChatMessagesFuture = createChatMessages(chatLists);
                    createChatMessagesFuture.compose(chatMessages -> {

                        List<Future> insertChatMessageFutures = new ArrayList<>();
                        for (ChatMessage chatMessage : chatMessages) {
                            insertChatMessageFutures.add(insertChatMessagesAndUpdateChatListAndUpdateUnseenCount(chatMessage));
                        }
                        insertAllFutures.addAll(insertChatMessageFutures);

                        CompositeFuture cp2 = CompositeFuture.all(insertAllFutures);
                        cp.setHandler(ar2 -> {
                            if (ar2.succeeded()) {
                                promise.complete(jsonObject);
                            } else {
                                promise.fail(ar2.cause());
                            }
                        });
                    }, Future.future().setHandler(handler -> promise.fail(handler.cause())));
                } else {
                    promise.fail(ar.cause());
                }
            });
        }, Future.future().setHandler(handler -> promise.fail(handler.cause())));

        return promise.future();
    }

    private Future<List<User>> createUsers() {
        Promise<List<User>> promise = Promise.promise();
        List<User> users = new ArrayList<>();

        User vcthanh24 = new User();
        vcthanh24.setUserId(GenerationUtils.generateId());
        vcthanh24.setUserName("vcthanh24");
        vcthanh24.setPassword("123");
        vcthanh24.setFullName("Vo Cong Thanh");

        UserAuth userAuthForThanh = new UserAuth();
        userAuthForThanh.setUserId(vcthanh24.getUserId());
        userAuthForThanh.setHashedPassword(BCrypt.hashpw(vcthanh24.getPassword(), BCrypt.gensalt()));

        users.add(vcthanh24);

        User lvhung = new User();
        lvhung.setUserId(GenerationUtils.generateId());
        lvhung.setUserName("lvhung");
        lvhung.setPassword("123");
        lvhung.setFullName("Le Vu Hung");

        UserAuth userAuthForHung = new UserAuth();
        userAuthForHung.setUserId(lvhung.getUserId());
        userAuthForHung.setHashedPassword(BCrypt.hashpw(lvhung.getPassword(), BCrypt.gensalt()));

        users.add(lvhung);

        User nthnhung = new User();
        nthnhung.setUserId(GenerationUtils.generateId());
        nthnhung.setUserName("nthnhung");
        nthnhung.setPassword("123");
        nthnhung.setFullName("Nguyen Thi Hong Nhung");

        UserAuth userAuthForNhung = new UserAuth();
        userAuthForNhung.setUserId(nthnhung.getUserId());
        userAuthForNhung.setHashedPassword(BCrypt.hashpw(nthnhung.getPassword(), BCrypt.gensalt()));

        users.add(nthnhung);

        User forTesting = new User();
        forTesting.setUserId(GenerationUtils.generateId());
        forTesting.setUserName("utest");
        forTesting.setPassword("123");
        forTesting.setFullName("Unit Test");

        UserAuth userAuthForU = new UserAuth();
        userAuthForU.setUserId(forTesting.getUserId());
        userAuthForU.setHashedPassword(BCrypt.hashpw(forTesting.getPassword(), BCrypt.gensalt()));

        users.add(forTesting);

        List<Name> listRandomName = new NameGenerator().generateNames(60, Gender.FEMALE);
        for (int i = 0; i < listRandomName.size(); i++) {
            Name name = listRandomName.get(i);

            User user = new User();
            user.setUserId(GenerationUtils.generateId());
            String userName = name.toString().trim().toLowerCase().replace(" ", "_");
            user.setUserName("user" + i);
            user.setPassword("123");
            user.setFullName(name.toString());

            UserAuth userAuth = new UserAuth();
            userAuth.setUserId(user.getUserId());
            userAuth.setHashedPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

            users.add(user);
        }

        promise.complete(users);

        return promise.future();
    }

    private Future<List<UserStatus>> createUserStatuses(List<User> users) {
        Promise<List<UserStatus>> promise = Promise.promise();
        List<UserStatus> userStatuses = new ArrayList<>();

        for (User user : users) {
            UserStatus userStatus = new UserStatus();
            userStatus.setUserId(user.getUserId());
            int ran = RandomUtils.nextInt(1, 4);
            switch (ran) {
                case 1:
                    userStatus.setStatus(RandomSentences.generateRandomSentence(RandomSentences.Length.LONG));
                    break;
                case 2:
                    userStatus.setStatus(RandomSentences.generateRandomSentence(RandomSentences.Length.MEDIUM));
                    break;
                case 3:
                    userStatus.setStatus(RandomSentences.generateRandomSentence(RandomSentences.Length.SHORT));
                    break;
            }

            userStatuses.add(userStatus);
        }

        promise.complete(userStatuses);

        return promise.future();
    }

    private Future<List<FriendList>> createFriendLists(List<User> users) {

        Promise<List<FriendList>> promise = Promise.promise();

        List<FriendList> friendLists = new ArrayList<>();

        FriendList friendListThanhHung = new FriendList();
        friendListThanhHung.setCurrentUserHashes(new UserHash(users.get(0).getUserId(), users.get(0).getFullName()));
        friendListThanhHung.setFriendUserHashes(new UserHash(users.get(2).getUserId(), users.get(2).getFullName()));
        friendLists.add(friendListThanhHung);

        FriendList friendListThanhNhung = new FriendList();
        friendListThanhNhung.setCurrentUserHashes(new UserHash(users.get(0).getUserId(), users.get(0).getFullName()));
        friendListThanhNhung.setFriendUserHashes(new UserHash(users.get(1).getUserId(), users.get(1).getFullName()));
        friendLists.add(friendListThanhNhung);

        for (int i = 17; i < 47; i++) {
            FriendList friendListForThanh = new FriendList();
            friendListForThanh.setCurrentUserHashes(new UserHash(users.get(0).getUserId(), users.get(0).getFullName()));
            friendListForThanh.setFriendUserHashes(new UserHash(users.get(i).getUserId(), users.get(i).getFullName()));

            FriendList friendListForNhung = new FriendList();
            friendListForNhung.setCurrentUserHashes(new UserHash(users.get(1).getUserId(), users.get(1).getFullName()));
            friendListForNhung.setFriendUserHashes(new UserHash(users.get(i).getUserId(), users.get(i).getFullName()));

            FriendList friendListForHung = new FriendList();
            friendListForHung.setCurrentUserHashes(new UserHash(users.get(2).getUserId(), users.get(2).getFullName()));
            friendListForHung.setFriendUserHashes(new UserHash(users.get(i).getUserId(), users.get(i).getFullName()));

            friendLists.add(friendListForThanh);
            friendLists.add(friendListForNhung);
            friendLists.add(friendListForHung);
        }

        promise.complete(friendLists);

        return promise.future();
    }

    private Future<List<ChatList>> createChatLists(List<User> users) {

        Promise<List<ChatList>> promise = Promise.promise();

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        Date previousYear = cal.getTime();

        List<ChatList> chatLists = new ArrayList<>();

        ChatList chatListForThanhHung = new ChatList();
        List<UserHash> userHashesForThanhHung = new ArrayList<>();
        userHashesForThanhHung.add(new UserHash(users.get(0).getUserId(), users.get(0).getFullName()));
        userHashesForThanhHung.add(new UserHash(users.get(1).getUserId(), users.get(1).getFullName()));
        chatListForThanhHung.setUserHashes(userHashesForThanhHung);
        chatListForThanhHung.setSessionId(GenerationUtils.generateId());
        chatListForThanhHung.setUpdatedDate(previousYear);
        chatLists.add(chatListForThanhHung);

        ChatList chatListForThanhNhung = new ChatList();
        List<UserHash> userHashesForThanhNhung = new ArrayList<>();
        userHashesForThanhNhung.add(new UserHash(users.get(0).getUserId(), users.get(0).getFullName()));
        userHashesForThanhNhung.add(new UserHash(users.get(2).getUserId(), users.get(2).getFullName()));
        chatListForThanhNhung.setUserHashes(userHashesForThanhNhung);
        chatListForThanhNhung.setSessionId("test-1234-id");
        chatListForThanhNhung.setUpdatedDate(previousYear);
        chatLists.add(chatListForThanhNhung);

        for (int i = 4; i < 32; i++) {
            ChatList chatListForThanh = new ChatList();
            List<UserHash> userHashesForThanh = new ArrayList<>();
            userHashesForThanh.add(new UserHash(users.get(0).getUserId(), users.get(0).getFullName()));
            userHashesForThanh.add(new UserHash(users.get(i).getUserId(), users.get(i).getFullName()));
            chatListForThanh.setUserHashes(userHashesForThanh);
            chatListForThanh.setSessionId(GenerationUtils.generateId());
            chatListForThanh.setUpdatedDate(previousYear);

            ChatList chatListForHung = new ChatList();
            List<UserHash> userHashesForHung = new ArrayList<>();
            userHashesForHung.add(new UserHash(users.get(1).getUserId(), users.get(1).getFullName()));
            userHashesForHung.add(new UserHash(users.get(i).getUserId(), users.get(i).getFullName()));
            chatListForHung.setUserHashes(userHashesForHung);
            chatListForHung.setSessionId(GenerationUtils.generateId());
            chatListForHung.setUpdatedDate(previousYear);

            chatLists.add(chatListForThanh);
            chatLists.add(chatListForHung);
        }

        promise.complete(chatLists);

        return promise.future();
    }

    private Future<List<ChatMessage>> createChatMessages(List<ChatList> chatLists) {
        Promise<List<ChatMessage>> promise = Promise.promise();
        List<ChatMessage> chatMessages = new ArrayList<>();

        for (ChatList chatList : chatLists) {
            chatMessages.addAll(createChatMessages(
                chatList.getSessionId(), chatList.getUserHashes(), chatList.getUpdatedDate().getTime()
            ));
        }

        promise.complete(chatMessages);

        return promise.future();
    }

    private List<ChatMessage> createChatMessages(String sessionId, List<UserHash> userHashes, long createdDate) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        int numMessage = 60;
        for (int i = 0; i < numMessage; i++) {
            int randomUserId = RandomUtils.nextInt(0, userHashes.size());

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUserHash(userHashes.get(randomUserId));
            chatMessage.setSessionId(sessionId);
            chatMessage.setCreatedDate(new Date(createdDate + i * 1000 * 60 * 5));
            int ran = RandomUtils.nextInt(1, 4);
            switch (ran) {
                case 1:
                    chatMessage.setMessage(RandomSentences.generateRandomSentence(RandomSentences.Length.LONG));
                    break;
                case 2:
                    chatMessage.setMessage(RandomSentences.generateRandomSentence(RandomSentences.Length.MEDIUM));
                    break;
                case 3:
                    chatMessage.setMessage(RandomSentences.generateRandomSentence(RandomSentences.Length.SHORT));
                    break;
            }

            chatMessages.add(chatMessage);
        }

        return chatMessages;
    }
}
