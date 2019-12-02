package com.hey.handler;

import com.hey.repository.DataRepository;
import com.hey.util.HeyHttpStatusException;
import com.hey.util.HttpStatus;
import com.hey.util.JsonUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class BaseHandler {

    protected DataRepository dataRepository;

    public void handleException(Throwable throwable, HttpServerResponse response) {
        if (throwable instanceof HeyHttpStatusException) {
            HeyHttpStatusException e = (HeyHttpStatusException) throwable;
            JsonObject obj = new JsonObject();
            obj.put("code", e.getCode());
            obj.put("message", e.getPayload());
            response.setStatusCode(e.getStatusCode())
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(JsonUtils.toErrorJSON(obj));
            return;
        }

        if (throwable instanceof Exception) {
            Exception e = (Exception) throwable;
            log.error("Error", e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.code())
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(JsonUtils.toError500JSON());
        }
    }

    public void ping(HttpServerRequest request, HttpServerResponse response) {
        response.setStatusCode(HttpStatus.OK.code())
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JsonUtils.toSuccessJSON("Pong"));
    }
}
