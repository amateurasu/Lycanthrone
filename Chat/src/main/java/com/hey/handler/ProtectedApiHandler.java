package com.hey.handler;

import com.hey.manager.JwtManager;
import com.hey.model.*;
import com.hey.service.APIService;
import com.hey.util.ErrorCode;
import com.hey.util.HttpStatus;
import com.hey.util.JsonUtils;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.hey.util.LogUtils.log;

@Slf4j
public class ProtectedApiHandler extends BaseHandler {

    public static final String AUTHENTICATION_SCHEME = "Bearer";
    public JwtManager jwtManager;
    private APIService apiService;

    public void setJwtManager(JwtManager jwtManager) {
        this.jwtManager = jwtManager;
    }

    public APIService getApiService() {
        return apiService;
    }

    public void setApiService(APIService apiService) {
        this.apiService = apiService;
    }

    public void handle(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        HttpServerResponse response = rc.response();
        String requestPath = request.path();
        String path = StringUtils.substringAfter(requestPath, "/API/protected");
        try {
            String authorization = request.headers().get(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isBlank(authorization)) {
                throw new HttpStatusException(HttpStatus.UNAUTHORIZED.code(), HttpStatus.UNAUTHORIZED.message());
            }
            authorization = authorization.replace(AUTHENTICATION_SCHEME, "").trim();

            JsonObject authObj = new JsonObject().put("jwt", authorization);
            jwtManager.authenticate(authObj, event -> {
                if (!event.succeeded()) {
                    throw new HttpStatusException(HttpStatus.UNAUTHORIZED.code(), HttpStatus.UNAUTHORIZED.message());
                }

                String userId = event.result().principal().getString("userId");
                JsonObject jsonObject = null;
                if (rc.getBody() != null && rc.getBody().length() > 0) {
                    jsonObject = rc.getBodyAsJson();
                }
                switch (path) {
                    case "/ping":
                        ping(request, response);
                        break;
                    case "/chatlist":
                        log("User {} request chat list {}", userId);
                        getChatList(request, response, jsonObject, userId);
                        break;
                    case "/addressbook":
                        log("User {} request address book", userId);
                        getAddressBook(request, response, jsonObject, userId);
                        break;
                    case "/usernameexisted":
                        log("User {} check username {}", userId, jsonObject);
                        checkUsernameExisted(request, response, jsonObject, userId);
                        break;
                    case "/sessionidbyuserid":
                        log("User {} get session id {}", userId, jsonObject);
                        getSessionIdByUserId(request, response, jsonObject, userId);
                        break;
                    case "/waitingchatheader":
                        log("User {} get temporarily chat header {}", userId, jsonObject);
                        waitingChatHeader(request, response, jsonObject, userId);
                        break;
                    case "/addfriend":
                        log("User {} add new friend {}", userId, jsonObject);
                        addFriend(request, response, jsonObject, userId);
                        break;
                    case "/status":
                        log("User {} change status {}", userId, jsonObject);
                        changeStatus(request, response, jsonObject, userId);
                        break;
                    case "/user":
                        log("User {} get profile {}", userId, jsonObject);
                        getUserProfile(request, response, jsonObject, userId);
                        break;
                }
            });
        } catch (HttpStatusException e) {
            JsonObject obj = new JsonObject();
            obj.put("code", ErrorCode.AUTHORIZED_FAILED.code());
            obj.put("message", e.getPayload());
            response.setStatusCode(e.getStatusCode())
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(JsonUtils.toErrorJSON(obj));
        }
    }

    public void getChatList(HttpServerRequest request, HttpServerResponse response, JsonObject requestObject, String userId) {
        Future<ChatListResponse> getChatListFuture = apiService.getChatList(userId);

        getChatListFuture.compose(chatListResponse -> response
            .setStatusCode(HttpStatus.OK.code())
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JsonUtils.toSuccessJSON(chatListResponse)), Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }

    public void getAddressBook(HttpServerRequest request, HttpServerResponse response, JsonObject requestObject, String userId) {

        Future<AddressBookResponse> getAddressBookFuture = apiService.getAddressBook(userId);

        getAddressBookFuture.compose(addressBookResponse -> response
            .setStatusCode(HttpStatus.OK.code())
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JsonUtils.toSuccessJSON(addressBookResponse)), Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }

    public void checkUsernameExisted(HttpServerRequest request, HttpServerResponse response, JsonObject requestObject, String userId) {
        UsernameExistedRequest usernameExistedRequest = requestObject.mapTo(UsernameExistedRequest.class);

        Future<UsernameExistedResponse> checkUsernameExistedFuture = apiService.checkUsernameExisted(usernameExistedRequest, userId);

        checkUsernameExistedFuture.compose(usernameExistedResponse -> response
            .setStatusCode(HttpStatus.OK.code())
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JsonUtils.toSuccessJSON(usernameExistedResponse)), Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }

    public void getSessionIdByUserId(HttpServerRequest request, HttpServerResponse response, JsonObject requestObject, String userId) {
        GetSessionIdRequest getSessionIdRequest = requestObject.mapTo(GetSessionIdRequest.class);

        Future<GetSessionIdResponse> getSessionIdByUserIdFuture = apiService.getSessionIdByUserId(getSessionIdRequest, userId);

        getSessionIdByUserIdFuture.compose(getSessionIdResponse -> response
            .setStatusCode(HttpStatus.OK.code())
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JsonUtils.toSuccessJSON(getSessionIdResponse)), Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }

    public void waitingChatHeader(HttpServerRequest request, HttpServerResponse response, JsonObject requestObject, String userId) {
        WaitingChatHeaderRequest waitingChatHeaderRequest = requestObject.mapTo(WaitingChatHeaderRequest.class);

        Future<WaitingChatHeaderResponse> waitingChatHeaderFuture = apiService.waitingChatHeader(waitingChatHeaderRequest, userId);

        waitingChatHeaderFuture.compose(waitingChatHeaderResponse -> response
            .setStatusCode(HttpStatus.OK.code())
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JsonUtils.toSuccessJSON(waitingChatHeaderResponse)), Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }

    public void addFriend(HttpServerRequest request, HttpServerResponse response, JsonObject requestObject, String userId) {
        AddFriendRequest addFriendRequest = requestObject.mapTo(AddFriendRequest.class);

        Future<AddFriendResponse> addFriendFuture = apiService.addFriend(addFriendRequest, userId);

        addFriendFuture.compose(addFriendResponse -> response
            .setStatusCode(HttpStatus.OK.code())
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JsonUtils.toSuccessJSON(addFriendResponse)), Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }

    public void changeStatus(HttpServerRequest request, HttpServerResponse response, JsonObject requestObject, String userId) {
        ChangeStatusRequest changeStatusRequest = requestObject.mapTo(ChangeStatusRequest.class);

        Future<JsonObject> insertUserStatusFuture = apiService.changeStatus(changeStatusRequest, userId);

        insertUserStatusFuture.compose(jsonObject -> response
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JsonUtils.toSuccessJSON(jsonObject)), Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }

    public void getUserProfile(HttpServerRequest request, HttpServerResponse response, JsonObject requestObject, String userId) {
        Future<UserProfileResponse> getUserProfileFuture = apiService.getUserProfile(userId);

        getUserProfileFuture.compose(userProfileResponse -> response
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JsonUtils.toSuccessJSON(userProfileResponse)), Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }
}
