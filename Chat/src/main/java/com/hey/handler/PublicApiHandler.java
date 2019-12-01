package com.hey.handler;

import com.hey.service.APIService;
import com.hey.util.HttpStatus;
import com.hey.util.JsonUtils;
import com.hey.util.LogUtils;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class PublicApiHandler extends BaseHandler {
    @Setter
    private APIService apiService;

    public void handle(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        HttpServerResponse response = rc.response();
        String requestPath = request.path();
        String path = StringUtils.substringAfter(requestPath, "/API/public");
        String json = rc.getBodyAsString();

        if ("/user".equals(path)) {
            LogUtils.log("New register request");
            registerUser(request, response, json);
        } else {
            response.end();
        }
    }

    public void registerUser(HttpServerRequest request, HttpServerResponse response, String jsonData) {
        apiService.registerUser(jsonData).compose(user -> response
                .setStatusCode(HttpStatus.OK.code())
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(JsonUtils.toSuccessJSON(user)),
            Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }
}
