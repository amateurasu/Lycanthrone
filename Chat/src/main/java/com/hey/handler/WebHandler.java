package com.hey.handler;

import com.hey.service.WebService;
import com.hey.util.HttpStatus;
import com.hey.util.JsonUtils;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class WebHandler extends BaseHandler {

    private WebService webService;

    public WebService getWebService() {
        return webService;
    }

    public void setWebService(WebService webService) {
        this.webService = webService;
    }

    public void signIn(RoutingContext routingContext) {
        String requestJson = routingContext.getBodyAsString();
        Future<JsonObject> signInFuture = webService.signIn(requestJson);

        signInFuture.compose(jsonObject -> routingContext.response()
                .setStatusCode(HttpStatus.OK.code())
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(JsonUtils.toSuccessJSON(jsonObject)),
            Future.future().setHandler(handler -> handleException(handler.cause(), routingContext.response())));
    }

    public void signOut(RoutingContext routingContext) {
        webService.signOut(routingContext);
    }

    public void initTestData(RoutingContext routingContext) {
        webService.initTestData()
            .compose(jsonObject -> routingContext.response()
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(jsonObject)),
                Future.future().setHandler(handler -> handleException(handler.cause(), routingContext.response())));
    }
}
