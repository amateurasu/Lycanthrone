package com.hey.api;

import com.hey.handler.ProtectedApiHandler;
import com.hey.handler.PublicApiHandler;
import com.hey.handler.WebHandler;
import com.hey.util.PropertiesUtils;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Data
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiServer {
    private HttpServer httpServer;
    private WebHandler webHandler;
    private PublicApiHandler publicApiHandler;
    private ProtectedApiHandler protectedApiHandler;

    public static ApiServer newInstance() {
        return new ApiServer();
    }

    public Future<Void> createHttpServer(Vertx vertx) {
        if (httpServer != null) {
            Future.succeededFuture();
        }
        log.info("Starting API Server ...");

        Router router = Router.router(vertx);

        router.route("/").handler(routingContext -> {
            HttpServerResponse httpServerResponse = routingContext.response();
            httpServerResponse.putHeader("content-type", "text/html").end("<h1>Helloworld</h1>");
        });

        router.route().handler(BodyHandler.create());

        Set<String> allowedHeaders = new HashSet<String>() {{
            add("x-requested-with");
            add("Access-Control-Allow-Origin");
            add("origin");
            add("accept");
            add("Content-Type");
            add("Authorization");
        }};

        Set<HttpMethod> allowedMethods = new HashSet<HttpMethod>() {{
            add(HttpMethod.GET);
            add(HttpMethod.POST);
            add(HttpMethod.OPTIONS);
            add(HttpMethod.DELETE);
            add(HttpMethod.PATCH);
            add(HttpMethod.PUT);
        }};

        router.route("/*").handler(
            CorsHandler.create("http://localhost:3000")
                .allowedHeaders(allowedHeaders)
                .allowedMethods(allowedMethods)
                .allowCredentials(true)
        ).handler(BodyHandler.create());

        router.get("/inittestdata").handler(webHandler::initTestData);

        router.post("/signin").handler(webHandler::signIn);
        router.post("/signout").handler(webHandler::signOut);
        router.post("/api/public/*").handler(publicApiHandler::handle);

        router.route("/api/protected/*").handler(protectedApiHandler::handle);

        Promise<Void> promise = Promise.promise();
        httpServer = vertx
            .createHttpServer()
            .requestHandler(router::handle)
            .exceptionHandler(exHandler -> log.error("Error", exHandler))
            .listen(PropertiesUtils.getInstance().getIntValue("api.port"), ar -> {
                if (ar.succeeded()) {
                    log.info("API Server start successfully !");
                    promise.complete();
                } else {
                    log.error("API Server start fail.", ar.cause());
                    promise.fail(ar.cause());
                }
            });

        return promise.future();
    }
}
http
