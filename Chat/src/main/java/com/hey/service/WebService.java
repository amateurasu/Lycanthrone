package com.hey.service;

import com.hey.model.User;
import com.hey.model.UserAuth;
import com.hey.util.*;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;

public class WebService extends BaseService {

    public Future<JsonObject> signIn(String requestJson) {
        Promise<JsonObject> promise = Promise.promise();

        final User user = Json.decodeValue(requestJson, User.class);
        Future<UserAuth> getUserAuthFuture = dataRepository.getUserAuth(user.getUserName());

        getUserAuthFuture.compose(userAuthRes -> {
            if (userAuthRes == null) {
                throw new HeyHttpStatusException(HttpStatus.UNAUTHORIZED.code(), ErrorCode.AUTHORIZED_FAILED.code(), "Invalid Username or Password :(");
            }

            if (!BCrypt.checkpw(user.getPassword(), userAuthRes.getHashedPassword())) {
                throw new HeyHttpStatusException(HttpStatus.UNAUTHORIZED.code(), ErrorCode.AUTHORIZED_FAILED.code(), "Invalid Username or Password :(");
            }

            String jwt = jwtManager.generateToken(userAuthRes.getUserId());

            JsonObject obj = new JsonObject();
            obj.put("jwt", jwt);
            obj.put("userId", userAuthRes.getUserId());
            LogUtils.log("Signin granted with username " + user.getUserName());
            promise.complete(obj);
        }, Future.future().setHandler(handler -> {
            LogUtils.log("Signin failed with username " + user.getUserName());
            promise.fail(handler.cause());
        }));

        return promise.future();
    }

    public void signOut(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();

        // Extract the token from the Authorization header
        String token = request.headers().get(HttpHeaders.AUTHORIZATION).substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            JsonObject authObj = new JsonObject().put("jwt", token);
            jwtManager.authenticate(authObj, event -> {
                if (event.failed()) {
                    throw new HttpStatusException(HttpStatus.UNAUTHORIZED.code(), HttpStatus.UNAUTHORIZED.message());
                }

                String userId = event.result().principal().getString("userId");
                long expireTimeInMilliseconds = event.result().principal().getLong("exp") * 1000;
                long ttl = expireTimeInMilliseconds - new Date().getTime();
                if (ttl > 0) {
                    jwtManager.blacklistToken(token, userId, ttl);
                }
                LogUtils.log("Signout user " + userId + " with token " + token);
            });
            routingContext.response().end();
        } catch (HttpStatusException e) {
            JsonObject obj = new JsonObject();
            obj.put("code", ErrorCode.AUTHORIZED_FAILED.code());
            obj.put("message", e.getPayload());
            response.setStatusCode(e.getStatusCode())
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(JsonUtils.toErrorJSON(obj));
        }
    }
}
