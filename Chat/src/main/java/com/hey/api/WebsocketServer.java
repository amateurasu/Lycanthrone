package com.hey.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.handler.WsHandler;
import com.hey.manager.JwtManager;
import com.hey.manager.UserWsChannelManager;
import com.hey.model.*;
import com.hey.util.PropertiesUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import static com.hey.util.LogUtils.log;

@Data
@Slf4j
public class WebsocketServer {
    private WsHandler wsHandler;
    private UserWsChannelManager userWsChannelManager;
    private JwtManager jwtManager;

    private WebsocketServer() {
    }

    public static WebsocketServer newInstance() {
        return new WebsocketServer();
    }

    public Future<Void> createWsServer(Vertx vertx) {
        Future<Void> future = Future.succeededFuture();
        vertx.createHttpServer().websocketHandler(ws -> {
            final String id = ws.textHandlerID();
            String query = ws.query();
            if (StringUtils.isBlank(query)) {
                log.info("Authentication Failed for id: {}", id);
                ws.reject();
                return;
            }

            String jwt = query.substring(query.indexOf('=') + 1);
            if (StringUtils.isBlank(jwt)) {
                log.info("Authentication Failed for id: {}", id);
                ws.reject();
                return;
            }

            JsonObject authObj = new JsonObject().put("jwt", jwt);
            jwtManager.authenticate(authObj, event -> {
                if (!event.succeeded()) {
                    log.info("Authentication Failed for id: {}", id);
                    ws.reject();
                    return;
                }

                String userId = event.result().principal().getString("userId");
                log("User {} registering new connection with id: {}", userId, id);
                log.info("registering new connection with id: {} for user: {}", id, userId);
                userWsChannelManager.registerChannel(userId, id).setHandler(ar -> handleNotificationCase(ar, userId, true));

                ws.closeHandler(e -> {
                    log.info("un-registering connection with id: {} for user: {}", id, userId);
                    userWsChannelManager.removeChannel(userId, id).setHandler(ar -> handleNotificationCase(ar, userId, false));
                });

                ws.handler(data -> {
                    try {
                        JsonObject json = new JsonObject(data.toString());
                        String type = json.getString("type");
                        ObjectMapper mapper = new ObjectMapper();
                        switch (type) {
                            case IWsMessage.TYPE_CHAT_ITEM_REQUEST:
                                ChatContainerRequest chatContainerRequest = mapper.readValue(data.toString(), ChatContainerRequest.class);
                                log("User {} load chat container {}", userId, chatContainerRequest.getSessionId());
                                wsHandler.handleChatContainerRequest(chatContainerRequest, id, userId);
                                break;
                            case IWsMessage.TYPE_CHAT_MESSAGE_REQUEST:
                                ChatMessageRequest chatMessageRequest = mapper.readValue(data.toString(), ChatMessageRequest.class);
                                if (!"-1".equals(chatMessageRequest.getSessionId())) {
                                    log("User {} send a chat message to {}", userId, chatMessageRequest.getSessionId());
                                } else {
                                    log("User {} start a chat message to {}", userId, ArrayUtils.toString(chatMessageRequest.getUsernames()));
                                }
                                wsHandler.handleChatMessageRequest(chatMessageRequest, id, userId);
                                break;
                        }
                    } catch (IOException e) {
                        log.error(data.toString(), e);
                    }
                });
            });
        }).listen(PropertiesUtils.getInstance().getIntValue("ws.port"));

        return future;
    }

    private void handleNotificationCase(AsyncResult<Boolean> ar, String userId, boolean state) {
        if (ar.succeeded()) {
            boolean shouldNotify = ar.result();
            if (shouldNotify) {
                wsHandler.getUserFull(userId).setHandler(ar2 -> {
                    if (ar2.succeeded()) {
                        String fullName = ar2.result().getFullName();
                        wsHandler.getFriendLists(userId).setHandler(ar3 -> {
                            if (ar3.succeeded()) {
                                List<FriendList> friendLists = ar3.result();
                                if (friendLists.size() > 0) {
                                    for (FriendList friendList : friendLists) {
                                        String friendId = friendList.getFriendUserHashes().getUserId();
                                        if (state) {
                                            UserOnlineResponse userOnlineResponse = new UserOnlineResponse();
                                            userOnlineResponse.setType(IWsMessage.TYPE_NOTIFICATION_FRIEND_ONLINE);
                                            userOnlineResponse.setUserId(userId);
                                            userOnlineResponse.setFullName(fullName);
                                            userWsChannelManager.sendMessage(userOnlineResponse, friendId);
                                        } else {
                                            UserOfflineResponse userOfflineResponse = new UserOfflineResponse();
                                            userOfflineResponse.setType(IWsMessage.TYPE_NOTIFICATION_FRIEND_OFFLINE);
                                            userOfflineResponse.setUserId(userId);
                                            userOfflineResponse.setFullName(fullName);
                                            userWsChannelManager.sendMessage(userOfflineResponse, friendId);
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private SockJSHandler sockJSHandler(Vertx vertx) {
        return SockJSHandler.create(vertx).socketHandler(sockJSSocket -> {
            String authorization = sockJSSocket.headers().get(HttpHeaders.AUTHORIZATION);
            String userId = "1";
            final String id = sockJSSocket.writeHandlerID();

            log.info("registering new connection with id: {} for user: {}", id, userId);
            userWsChannelManager.registerChannel(userId, id);

            sockJSSocket.endHandler(event -> {
                log.info("un-registering connection with id: {} for user: {}", id, userId);
                userWsChannelManager.removeChannel(userId, id);
            });

            //JsonObject json = new JsonObject(data.toString());
            //ObjectMapper m = new ObjectMapper();
            //sockJSSocket.close();
            sockJSSocket.handler(sockJSSocket::write);
        });
    }
}
