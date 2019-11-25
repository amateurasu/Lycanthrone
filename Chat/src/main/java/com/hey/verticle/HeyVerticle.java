package com.hey.verticle;

import com.hey.api.ApiServer;
import com.hey.api.WebsocketServer;
import com.hey.cache.client.RedisCacheClient;
import com.hey.handler.ProtectedApiHandler;
import com.hey.handler.PublicApiHandler;
import com.hey.handler.WebHandler;
import com.hey.handler.WsHandler;
import com.hey.manager.JwtManager;
import com.hey.manager.UserWsChannelManager;
import com.hey.repository.DataRepository;
import com.hey.service.APIService;
import com.hey.service.WebService;
import com.hey.util.PropertiesUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeyVerticle extends AbstractVerticle {
    @Getter
    private ApiServer apiServer;
    @Getter
    private WebsocketServer websocketServer;

    @Override
    public void start(Future<Void> future) {
        log.info("{} verticle {} start", deploymentID(), Thread.currentThread().getName());

        this.apiServer = ApiServer.newInstance();
        this.websocketServer = WebsocketServer.newInstance();

        // Create a JWT Auth Provider
        log.info("Initial JWT for verticle {}", Thread.currentThread().getName());
        JwtManager jwtManager = new JwtManager(vertx);

        //Inject dependency
        log.info("Starting Inject Dependency for verticle {}", Thread.currentThread().getName());
        RedisClient client = RedisClient.create(vertx, new RedisOptions().setHost(PropertiesUtils.getInstance().getValue("redis.host")));
        DataRepository repository = new RedisCacheClient(client);

        UserWsChannelManager userWsChannelManager = new UserWsChannelManager();
        userWsChannelManager.setEventBus(vertx.eventBus());
        userWsChannelManager.setSharedData(vertx.sharedData());

        APIService apiService = new APIService();
        apiService.setDataRepository(repository);
        apiService.setUserWsChannelManager(userWsChannelManager);

        WebService webService = new WebService();
        webService.setDataRepository(repository);
        webService.setJwtManager(jwtManager);

        ProtectedApiHandler protectedApiHandler = new ProtectedApiHandler();
        protectedApiHandler.setDataRepository(repository);
        protectedApiHandler.setJwtManager(jwtManager);
        protectedApiHandler.setApiService(apiService);

        PublicApiHandler publicApiHandler = new PublicApiHandler();
        publicApiHandler.setDataRepository(repository);
        publicApiHandler.setApiService(apiService);

        WebHandler webHandler = new WebHandler();
        webHandler.setDataRepository(repository);
        webHandler.setWebService(webService);

        WsHandler wsHandler = new WsHandler();
        wsHandler.setDataRepository(repository);
        wsHandler.setApiService(apiService);

        apiServer.setProtectedApiHandler(protectedApiHandler);
        apiServer.setPublicApiHandler(publicApiHandler);
        apiServer.setWebHandler(webHandler);

        wsHandler.setUserWsChannelManager(userWsChannelManager);

        websocketServer.setWsHandler(wsHandler);
        websocketServer.setUserWsChannelManager(userWsChannelManager);
        websocketServer.setJwtManager(jwtManager);

        log.info("Inject Dependency successfully for verticle {}", Thread.currentThread().getName());

        Future.succeededFuture()
            .compose(v -> apiServer.createHttpServer(vertx))
            .compose(v -> websocketServer.createWsServer(vertx))
            .setHandler(future);
    }

    @Override
    public void stop() {
        log.info("Shutting down application");
    }
}
