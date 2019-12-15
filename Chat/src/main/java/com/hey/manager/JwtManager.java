package com.hey.manager;

import com.hey.Hey;
import com.hey.util.PropertiesUtils;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.jwt.JWTOptions;

public class JwtManager {
    private static final String JWT_ASYNC_MAP = "jwt-async-map";
    private static final String JWT_BLACKLIST_KEY = "blacklist-key";
    private SharedData sharedData;
    private JWTAuth authProvider;
    private JWTOptions jwtOptions;

    public JwtManager(Vertx vertx) {
        this.sharedData = vertx.sharedData();

        PropertiesUtils props = PropertiesUtils.getInstance();

        authProvider = JWTAuth.create(vertx, new JWTAuthOptions()
            .setKeyStore(new KeyStoreOptions()
                .setType(props.getValue("jwt.keystore.type"))
                .setPassword(props.getValue("jwt.keystore.password"))
                .setPath(Hey.getResource(props.getValue("jwt.keystore")).getPath())));

        jwtOptions = new JWTOptions()
            .setIssuer(props.getValue("jwt.iss"))
            .addAudience(props.getValue("jwt.aud"))
            .setExpiresInSeconds(props.getIntValue("jwt.expire"));
    }

    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        String jwt = authInfo.getString("jwt");
        checkForExistingAsynMap(jwt).setHandler(event -> {
            if (event.result()) {
                resultHandler.handle(Future.failedFuture("Token has been blacklist"));
            } else {
                authProvider.authenticate(authInfo, resultHandler);
            }
        });
    }

    public void blacklistToken(String token, String userId, long ttl) {
        putToAsyncMap(token, userId, ttl);
    }

    public String generateToken(String userId) {
        JsonObject userObj = new JsonObject().put("userId", userId);
        return authProvider.generateToken(userObj, jwtOptions);
    }

    private void putToAsyncMap(String token, String userId, long ttl) {
        sharedData.getAsyncMap(JWT_ASYNC_MAP, event -> {
            AsyncMap<Object, Object> aMap = event.result();
            aMap.put(token, userId, ttl, AsyncResult::mapEmpty);
        });
    }

    private Future<Boolean> checkForExistingAsynMap(String token) {
        Promise<Boolean> promise = Promise.promise();
        sharedData.getAsyncMap(JWT_ASYNC_MAP, event -> {
            AsyncMap<Object, Object> aMap = event.result();
            aMap.get(token, event2 -> promise.complete(event2.result() != null));
        });
        return promise.future();
    }

    public void setSharedData(SharedData sharedData) {
        this.sharedData = sharedData;
    }

    public JWTAuth getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(JWTAuth authProvider) {
        this.authProvider = authProvider;
    }
}
