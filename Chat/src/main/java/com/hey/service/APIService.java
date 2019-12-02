package com.hey.service;

import com.hey.manager.UserWsChannelManager;
import com.hey.model.*;
import com.hey.util.HeyHttpStatusException;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.hey.util.ErrorCode.*;
import static com.hey.util.HttpStatus.BAD_REQUEST;

@Data
@EqualsAndHashCode(callSuper = true)
public class APIService extends BaseService {

    private UserWsChannelManager userWsChannelManager;

    public Future<User> registerUser(String jsonData) {
        Promise<User> promise = Promise.promise();

        final User user = Json.decodeValue(jsonData, User.class);

        if (StringUtils.isBlank(user.getUserName())) {
            promise.fail(new HeyHttpStatusException(BAD_REQUEST.code(), REGISTER_USERNAME_EMPTY.code(), "User Name cannot be empty"));
            return promise.future();
        }

        if (StringUtils.isBlank(user.getFullName())) {
            promise.fail(new HeyHttpStatusException(BAD_REQUEST.code(), REGISTER_FULLNAME_EMPTY.code(), "Full Name cannot be empty"));
            return promise.future();
        }

        if (StringUtils.isBlank(user.getPassword())) {
            promise.fail(new HeyHttpStatusException(BAD_REQUEST.code(), REGISTER_PASSWORD_EMPTY.code(), "Password cannot be empty"));
            return promise.future();
        }

        Future<UserAuth> getUserAuthFuture = dataRepository.getUserAuth(user.getUserName());

        getUserAuthFuture.compose(existedUserAuth -> {
            if (existedUserAuth != null) {
                throw new HeyHttpStatusException(BAD_REQUEST.code(), REGISTER_USERNAME_UNIQUED.code(), "User Name is duplicated");
            }

            Future<User> insertUserFuture = insertUser(user);
            insertUserFuture.compose(promise::complete, Future.future().setHandler(handler -> promise.fail(handler.cause())));
        }, Future.future().setHandler(handler -> promise.fail(handler.cause())));

        return promise.future();
    }

    public Future<ChatListResponse> getChatList(String userId) {

        Promise<ChatListResponse> promise = Promise.promise();

        Future<List<ChatList>> getChatListsFuture = getChatLists(userId);

        getChatListsFuture.compose(chatLists -> {

            List<Future> getUnSeenCountFutures = new ArrayList<>();
            for (ChatList chatList : chatLists) {
                getUnSeenCountFutures.add(dataRepository.getUnseenCount(userId, chatList.getSessionId()));
            }

            CompositeFuture cp = CompositeFuture.all(getUnSeenCountFutures);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {

                    List<ChatListItem> chatListItems = new ArrayList<>();

                    for (int index = 0; index < getUnSeenCountFutures.size(); ++index) {
                        Long unSeenCount = cp.resultAt(index);
                        ChatList chatList = chatLists.get(index);

                        ChatListItem chatListItem = new ChatListItem();

                        List<String> listFullNameExcludedCurrentUser = getListFullNameExcludedCurrentUser(userId, chatList.getUserHashes());
                        if (listFullNameExcludedCurrentUser.size() > 1) {
                            String groupName = listFullNameExcludedCurrentUser.stream()
                                .map(fullName -> fullName.split(" ")[0])
                                .collect(Collectors.joining(", "));
                            chatListItem.setName(groupName);
                            chatListItem.setGroupChat(true);
                        } else {
                            chatListItem.setName(listFullNameExcludedCurrentUser.get(0));
                        }
                        chatListItem.setSessionId(chatList.getSessionId());
                        chatListItem.setUnread(unSeenCount.intValue());
                        chatListItem.setLastMessage(chatList.getLastMessage());
                        chatListItem.setUpdatedDate(chatList.getUpdatedDate());

                        chatListItems.add(chatListItem);
                    }

                    ChatListResponse chatListResponse = new ChatListResponse();
                    chatListResponse.setItems(chatListItems);
                    promise.complete(chatListResponse);
                } else {
                    promise.fail(ar.cause());
                }
            });
        }, Future.future().setHandler(handler -> promise.fail(handler.cause())));

        return promise.future();
    }

    public Future<AddressBookResponse> getAddressBook(String userId) {
        Promise<AddressBookResponse> promise = Promise.promise();

        getFriendLists(userId).compose(friendLists -> {

            List<AddressBookItem> addressBookItems = new ArrayList<>();

            List<Future> getUserStatusFutures = new ArrayList<>();
            List<Future> getUserOnlineFutures = new ArrayList<>();
            List<Future> getUserStatusAndUserOnlineFutures = new ArrayList<>();

            for (FriendList friendList : friendLists) {
                getUserStatusFutures.add(dataRepository.getUserStatus(friendList.getFriendUserHashes().getUserId()));
                getUserOnlineFutures.add(isUserOnline(friendList.getFriendUserHashes().getUserId()));
            }
            getUserStatusAndUserOnlineFutures.addAll(getUserStatusFutures);
            getUserStatusAndUserOnlineFutures.addAll(getUserOnlineFutures);

            CompositeFuture cp = CompositeFuture.all(getUserStatusAndUserOnlineFutures);
            cp.setHandler(ar -> {
                if (!ar.failed()) {
                    promise.fail(ar.cause());
                    return;
                }

                for (int index = 0; index < friendLists.size(); ++index) {
                    AddressBookItem addressBookItem = new AddressBookItem();

                    UserHash friendUserHash = friendLists.get(index).getFriendUserHashes();
                    addressBookItem.setUserId(friendUserHash.getUserId());
                    addressBookItem.setName(friendUserHash.getFullName());

                    UserStatus friendUserStatus = cp.resultAt(index);
                    Boolean isUserOnline = cp.resultAt(index + friendLists.size());

                    addressBookItem.setStatus(friendUserStatus.getStatus());
                    addressBookItem.setOnline(isUserOnline);

                    addressBookItems.add(addressBookItem);
                }

                AddressBookResponse addressBookResponse = new AddressBookResponse();
                addressBookResponse.setItems(addressBookItems);
                promise.complete(addressBookResponse);
            });
        }, Future.future().setHandler(handler -> promise.fail(handler.cause())));

        return promise.future();
    }

    public Future<UsernameExistedResponse> checkUsernameExisted(UsernameExistedRequest usernameExistedRequest, String userId) {
        Promise<UsernameExistedResponse> promise = Promise.promise();

        dataRepository.getUserAuth(usernameExistedRequest.getUsername()).compose(userAuth -> {
            if (userAuth == null) {
                throw new HeyHttpStatusException(BAD_REQUEST.code(), START_GROUP_CHAT_USERNAME_NOT_EXISTED.code(),
                    "User Name is not existed");
            }

            isFriend(userId, userAuth.getUserId()).compose(isFriend -> {
                if (!isFriend) {
                    throw new HeyHttpStatusException(BAD_REQUEST.code(), START_GROUP_CHAT_USERNAME_NOT_FRIEND.code(),
                        "User Name is not friend");
                }

                UsernameExistedResponse usernameExistedResponse = new UsernameExistedResponse();
                usernameExistedResponse.setUsername(usernameExistedRequest.getUsername());
                usernameExistedResponse.setExisted(true);

                promise.complete(usernameExistedResponse);
                return promise.future();
            });
            return promise.future();
        });
        return promise.future();
    }

    public Future<GetSessionIdResponse> getSessionIdByUserId(GetSessionIdRequest getSessionIdRequest, String userId) {
        Promise<GetSessionIdResponse> promise = Promise.promise();

        String chatListKey = "chat:list:*" + userId + ":" + getSessionIdRequest.getUserId();
        String chatListKeyReverse = "chat:list:*" + getSessionIdRequest.getUserId() + ":" + userId;

        List<Future> getKeysByPatternFutures = new ArrayList<>();

        getKeysByPatternFutures.add(dataRepository.getKeysByPattern(chatListKey));
        getKeysByPatternFutures.add(dataRepository.getKeysByPattern(chatListKeyReverse));

        CompositeFuture cp = CompositeFuture.all(getKeysByPatternFutures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {
                GetSessionIdResponse getSessionIdResponse = new GetSessionIdResponse();

                List<String> keys = new ArrayList<>();
                for (int index = 0; index < getKeysByPatternFutures.size(); ++index) {
                    keys.addAll(cp.resultAt(index));
                }

                if (keys.size() > 0) {
                    getSessionIdResponse.setSessionId(keys.get(0).split(":")[2]);
                } else {
                    getSessionIdResponse.setSessionId("-1");
                }

                promise.complete(getSessionIdResponse);
            } else {
                promise.fail(ar.cause());
            }
        });

        return promise.future();
    }

    public Future<WaitingChatHeaderResponse> waitingChatHeader(WaitingChatHeaderRequest waitingChatHeaderRequest, String userId) {
        Promise<WaitingChatHeaderResponse> promise = Promise.promise();
        Future<List<UserAuth>> getUserAuthsFuture = getUserAuths(Arrays.asList(waitingChatHeaderRequest.getUsernames()));

        getUserAuthsFuture.compose(userAuths -> {
            List<String> userFriendIds = new ArrayList<>();
            for (UserAuth userAuth : userAuths) {
                userFriendIds.add(userAuth.getUserId());
            }

            Future<List<UserFull>> getUserFullFutures = getUserFulls(userFriendIds);

            getUserFullFutures.compose(userFulls -> {

                List<String> firstNames = new ArrayList<>();
                for (UserFull userFull : userFulls) {
                    firstNames.add(userFull.getFullName().split(" ")[0]);
                }

                WaitingChatHeaderResponse waitingChatHeaderResponse = new WaitingChatHeaderResponse();
                waitingChatHeaderResponse.setTitle(String.join(", ", firstNames));

                promise.complete(waitingChatHeaderResponse);
                return promise.future();
            });
            return promise.future();
        });

        return promise.future();
    }

    public Future<AddFriendResponse> addFriend(AddFriendRequest addFriendRequest, String userId) {
        Promise<AddFriendResponse> promise = Promise.promise();

        if (StringUtils.isBlank(addFriendRequest.getUsername())) {
            promise.fail(new HeyHttpStatusException(BAD_REQUEST.code(), ADD_FRIEND_USERNAME_EMPTY.code(),
                "User Name cannot be empty"));
        }

        Future<UserAuth> getUserAuthFuture = dataRepository.getUserAuth(addFriendRequest.getUsername());

        getUserAuthFuture.compose(userAuth -> {
            if (userAuth == null) {
                throw new HeyHttpStatusException(BAD_REQUEST.code(), ADD_FRIEND_USERNAME_NOT_EXISTED.code(),
                    "User Name is not existed");
            }

            Future<Boolean> isFriendFuture = isFriend(userId, userAuth.getUserId());

            isFriendFuture.compose(isFriend -> {
                if (isFriend) {
                    promise.fail(new HeyHttpStatusException(BAD_REQUEST.code(), ADD_FRIEND_USERNAME_ALREADY.code(),
                        "User Name was added as friend"));
                    return;
                }

                List<String> userIds = new ArrayList<>();
                userIds.add(userId);
                userIds.add(userAuth.getUserId());
                Future<List<UserFull>> getUserFullsFuture = getUserFulls(userIds);

                getUserFullsFuture.compose(userFulls -> {
                    UserFull currentUserFull = userFulls.get(0);
                    UserFull friendUserFull = userFulls.get(1);

                    FriendList friendList = new FriendList();
                    friendList.setCurrentUserHashes(new UserHash(currentUserFull.getUserId(), currentUserFull.getFullName()));
                    friendList.setFriendUserHashes(new UserHash(friendUserFull.getUserId(), friendUserFull.getFullName()));

                    Future<FriendList> insertFriendListFuture = dataRepository.insertFriendList(friendList);

                    insertFriendListFuture.compose(friendListRes -> {
                        List<Future> getUserStatusAndUserOnlineFuture = new ArrayList<>();
                        getUserStatusAndUserOnlineFuture.add(dataRepository.getUserStatus(friendUserFull.getUserId()));
                        getUserStatusAndUserOnlineFuture.add(isUserOnline(friendUserFull.getUserId()));

                        CompositeFuture cp = CompositeFuture.all(getUserStatusAndUserOnlineFuture);
                        cp.setHandler(ar -> {
                            if (ar.succeeded()) {
                                UserStatus friendUserStatus = cp.resultAt(0);
                                Boolean isUserOnline = cp.resultAt(1);

                                AddressBookItem addressBookItem = new AddressBookItem();
                                addressBookItem.setUserId(friendUserFull.getUserId());
                                addressBookItem.setName(friendUserFull.getFullName());
                                addressBookItem.setStatus(friendUserStatus.getStatus());
                                addressBookItem.setOnline(isUserOnline);

                                AddFriendResponse addFriendResponse = new AddFriendResponse();
                                addFriendResponse.setItem(addressBookItem);

                                promise.complete(addFriendResponse);
                            } else {
                                promise.fail(ar.cause());
                            }
                        });
                    }, Future.future().setHandler(handler -> promise.fail(handler.cause())));
                }, Future.future().setHandler(handler -> promise.fail(handler.cause())));
            }, Future.future().setHandler(handler -> { }));
        }, Future.future().setHandler(handler -> promise.fail(handler.cause())));

        return promise.future();
    }

    public Future<JsonObject> changeStatus(ChangeStatusRequest changeStatusRequest, String userId) {
        Promise<JsonObject> promise = Promise.promise();

        UserStatus userStatus = new UserStatus();
        userStatus.setUserId(userId);
        userStatus.setStatus(changeStatusRequest.getStatus());

        Future<UserStatus> insertUserStatusFuture = dataRepository.insertUserStatus(userStatus);

        insertUserStatusFuture.compose(userStatusRes -> {
            JsonObject obj = new JsonObject();
            obj.put("message", "success");
            promise.complete(obj);
        }, Future.future().setHandler(handler -> promise.fail(handler.cause())));

        return promise.future();
    }

    private List<String> getListFullNameExcludedCurrentUser(String currentUserId, List<UserHash> userHashes) {
        List<String> listFullNameExcludedCurrentUser = new ArrayList<>();
        for (UserHash userHash : userHashes) {
            if (!currentUserId.equals(userHash.getUserId())) {
                listFullNameExcludedCurrentUser.add(userHash.getFullName());
            }
        }

        return listFullNameExcludedCurrentUser;
    }

    public Future<List<UserAuth>> getUserAuths(List<String> userNames) {
        Promise<List<UserAuth>> promise = Promise.promise();
        List<Future> getUserAuthFutures = new ArrayList<>();

        for (String userName : userNames) {
            getUserAuthFutures.add(dataRepository.getUserAuth(userName));
        }

        CompositeFuture cp = CompositeFuture.all(getUserAuthFutures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {
                List<UserAuth> userAuths = new ArrayList<>();
                for (int index = 0; index < getUserAuthFutures.size(); ++index) {
                    userAuths.add(cp.resultAt(index));
                }

                promise.complete(userAuths);
            } else {
                promise.fail(ar.cause());
            }
        });

        return promise.future();
    }

    public Future<List<UserFull>> getUserFulls(List<String> userIds) {
        Promise<List<UserFull>> promise = Promise.promise();

        List<Future> getUserFullFutures = new ArrayList<>();

        for (String userId : userIds) {
            getUserFullFutures.add(dataRepository.getUserFull(userId));
        }

        CompositeFuture cp = CompositeFuture.all(getUserFullFutures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {

                List<UserFull> userFulls = new ArrayList<>();
                for (int index = 0; index < getUserFullFutures.size(); ++index) {
                    userFulls.add(cp.resultAt(index));
                }

                promise.complete(userFulls);
            } else {
                promise.fail(ar.cause());
            }
        });

        return promise.future();
    }

    public Future<List<FriendList>> getFriendLists(String userId) {
        Promise<List<FriendList>> promise = Promise.promise();

        if (StringUtils.isBlank(userId)) {
            promise.fail(new HeyHttpStatusException(BAD_REQUEST.code(), "", "User Id is empty"));
            return promise.future();
        }

        String keyPattern = "friend:list:*" + userId + "*";
        Future<List<String>> getKeysByPatternFuture = dataRepository.getKeysByPattern(keyPattern);

        getKeysByPatternFuture.compose(keys -> {
            List<Future> getFriendListFutures = new ArrayList<>();

            for (String friendListKey : keys) {
                getFriendListFutures.add(dataRepository.getFriendList(friendListKey, userId));
            }

            CompositeFuture cp = CompositeFuture.all(getFriendListFutures);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {
                    List<FriendList> friendLists = new ArrayList<>();
                    for (int index = 0; index < getFriendListFutures.size(); ++index) {
                        if (cp.resultAt(index) != null) {
                            friendLists.add(cp.resultAt(index));
                        }
                    }
                    promise.complete(friendLists);
                } else {
                    promise.fail(ar.cause());
                }
            });
        }, Future.future().setHandler(handler -> promise.fail(handler.cause())));

        return promise.future();
    }

    public Future<Boolean> isFriend(String currentUserId, String friendUserId) {
        Promise<Boolean> promise = Promise.promise();

        String friendListKey = "friend:list:" + currentUserId + ":" + friendUserId;
        String friendListKeyReverse = "friend:list:" + friendUserId + ":" + currentUserId;

        List<Future> getFriendListFutures = new ArrayList<>();

        getFriendListFutures.add(dataRepository.getFriendList(friendListKey, currentUserId));
        getFriendListFutures.add(dataRepository.getFriendList(friendListKeyReverse, friendUserId));

        CompositeFuture cp = CompositeFuture.all(getFriendListFutures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {

                List<FriendList> friendLists = new ArrayList<>();
                for (int index = 0; index < getFriendListFutures.size(); ++index) {
                    if (cp.resultAt(index) != null) {
                        friendLists.add(cp.resultAt(index));
                    }
                }

                promise.complete(friendLists.size() > 0);
            } else {
                promise.fail(ar.cause());
            }
        });

        return promise.future();
    }

    public Future<ChatList> getChatListBySessionId(String sessionId) {
        Promise<ChatList> promise = Promise.promise();

        String keyPattern = "chat:list:" + sessionId + "*";
        Future<List<String>> getKeysByPatternFuture = dataRepository.getKeysByPattern(keyPattern);

        getKeysByPatternFuture.compose(keys -> {
            String chatListKey = keys.get(0);
            Future<ChatList> getChatListFuture = dataRepository.getChatList(chatListKey);

            getChatListFuture.compose(promise::complete, Future.future().setHandler(handler -> promise.fail(handler.cause())));
        }, Future.future().setHandler(handler -> promise.fail(handler.cause())));

        return promise.future();
    }

    public Future<List<ChatList>> getChatLists(String userId) {
        Promise<List<ChatList>> promise = Promise.promise();

        String keyPattern = "chat:list:" + "*" + userId + "*";
        Future<List<String>> getKeysByPatternFuture = dataRepository.getKeysByPattern(keyPattern);

        getKeysByPatternFuture.compose(chatListKeys -> {
            List<Future> getChatListFutures = new ArrayList<>();

            for (String chatListKey : chatListKeys) {
                getChatListFutures.add(dataRepository.getChatList(chatListKey));
            }

            CompositeFuture cp = CompositeFuture.all(getChatListFutures);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {
                    List<ChatList> chatLists = new ArrayList<>();
                    for (int index = 0; index < getChatListFutures.size(); ++index) {
                        chatLists.add(cp.resultAt(index));
                    }

                    promise.complete(chatLists);
                } else {
                    promise.fail(ar.cause());
                }
            });
        }, Future.future().setHandler(handler -> promise.fail(handler.cause())));

        return promise.future();
    }

    public Future<List<ChatMessage>> getChatMessages(String sessionId) {
        Promise<List<ChatMessage>> promise = Promise.promise();

        String keyPattern = "chat:message:" + sessionId + ":*";
        Future<List<String>> getKeysByPatternFuture = dataRepository.getKeysByPattern(keyPattern);

        getKeysByPatternFuture.compose(chatMessageKeys -> {
            List<Future> getChatMessageFutures = new ArrayList<>();

            // Sort message by time
            Collections.sort(chatMessageKeys);

            for (String chatMessageKey : chatMessageKeys) {
                getChatMessageFutures.add(dataRepository.getChatMessage(chatMessageKey));
            }

            CompositeFuture cp = CompositeFuture.all(getChatMessageFutures);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {

                    List<ChatMessage> chatMessages = new ArrayList<>();
                    for (int index = 0; index < getChatMessageFutures.size(); ++index) {
                        chatMessages.add(cp.resultAt(index));
                    }

                    promise.complete(chatMessages);
                } else {
                    promise.fail(ar.cause());
                }
            });
        }, Future.future().setHandler(handler -> promise.fail(handler.cause())));

        return promise.future();
    }

    public Future<UserProfileResponse> getUserProfile(String userId) {
        Promise<UserProfileResponse> promise = Promise.promise();

        List<Future> getUserFullAndUserStatusFuture = new ArrayList<>();

        getUserFullAndUserStatusFuture.add(dataRepository.getUserFull(userId));
        getUserFullAndUserStatusFuture.add(dataRepository.getUserStatus(userId));

        CompositeFuture cp = CompositeFuture.all(getUserFullAndUserStatusFuture);
        cp.setHandler(ar -> {
            if (ar.failed()) {
                promise.fail(ar.cause());
                return;
            }

            UserFull userFull = cp.resultAt(0);
            UserStatus userStatus = cp.resultAt(1);

            UserProfileResponse userProfileResponse = new UserProfileResponse();
            userProfileResponse.setStatus(userStatus.getStatus());
            userProfileResponse.setUserFullName(userFull.getFullName());
            userProfileResponse.setUserName(userFull.getUserName());

            promise.complete(userProfileResponse);
        });

        return promise.future();
    }

    private Future<Boolean> isUserOnline(String userId) {
        Promise<Boolean> promise = Promise.promise();
        userWsChannelManager.getChannels(userId).setHandler(event -> {
            if (event.succeeded()) {
                promise.complete(event.result() != null && !event.result().isEmpty());
            }
        });
        return promise.future();
    }
}
