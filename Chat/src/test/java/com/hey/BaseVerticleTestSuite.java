package com.hey;

import com.hey.model.User;
import com.hey.model.UserAuth;
import com.hey.model.UserFull;
import com.hey.repository.DataRepository;
import com.hey.service.APIService;
import com.hey.service.WebService;
import com.hey.verticle.HeyVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.Properties;

@Data
@Slf4j
public class BaseVerticleTestSuite {
    private static final String PROP_FILE_NAME = "system.properties";
    private static HeyVerticle heyVerticle;

    @Getter private static APIService apiService;
    @Getter private static WebService webService;
    @Getter private static String userIdToTest;
    @Getter private static String anotherUserIdToTest;
    @Getter private static String yetAnotherUserIdToTest;
    @Getter private static UserFull userFullToTest;
    @Getter private static UserFull anotherUserFullToTest;
    @Getter private static String sessionIdToTest;
    @Getter private static String jwtToTest;
    @Getter private static String jwtAuthHeader;
    @Getter private static String host = "localhost";
    @Getter private static int port = 8080;
    @Getter private static DataRepository dataRepository;
    @Getter private static HttpClient client;

    private static String env;

    /**
     * This is a method description that is long enough to exceed right margin.
     * <p/>
     * Another paragraph of the description placed after blank line.
     * <p/>
     * Line with manual line feed.
     */
    @BeforeClass
    public static void setUp(TestContext context) {
        if (heyVerticle != null) return;

        env = System.getenv("env");
        if (StringUtils.isBlank(env)) {
            log.error("Missing env");
            System.exit(1);
        }
        initSystemProperty(context);
        initVertx(context);
    }

    private static void initSystemProperty(TestContext context) {
        Properties p = new Properties();
        try {
            p.load(Hey.getResourceAsStream(env + "." + PROP_FILE_NAME));
        } catch (IOException e) {
            log.error("Cannot load System Property");
            System.exit(1);
        }
        for (String name : p.stringPropertyNames()) {
            String value = p.getProperty(name);
            System.setProperty(name, value);
        }
    }

    private static void initData() {

    }

    private static void initVertx(TestContext context) {
        final Async async = context.async();
        Vertx vertx = Vertx.vertx();
        heyVerticle = new HeyVerticle();
        vertx.deployVerticle(
            heyVerticle,
            ar -> {
                apiService = heyVerticle.getApiServer().getProtectedApiHandler().getApiService();
                webService = heyVerticle.getApiServer().getWebHandler().getWebService();
                dataRepository = heyVerticle.getApiServer().getProtectedApiHandler().getDataRepository();
                client = vertx.createHttpClient();
                Future<UserAuth> getUserAuthFuture = getApiService().getDataRepository().getUserAuth("vcthanh24");
                Future<UserAuth> getUserAuthFuture2 = getApiService().getDataRepository().getUserAuth("nthnhung");
                Future<UserAuth> getUserAuthFuture3 = getApiService().getDataRepository().getUserAuth("utest");
                CompositeFuture compositeFuture = CompositeFuture.all(getUserAuthFuture, getUserAuthFuture2, getUserAuthFuture3);
                compositeFuture.setHandler(res -> {
                    userIdToTest = ((UserAuth) compositeFuture.resultAt(0)).getUserId();
                    anotherUserIdToTest = ((UserAuth) compositeFuture.resultAt(1)).getUserId();
                    yetAnotherUserIdToTest = ((UserAuth) compositeFuture.resultAt(2)).getUserId();
                    sessionIdToTest = "test-1234-id";
                    User user = new User();
                    user.setUserName("vcthanh24");
                    user.setPassword("123");
                    getWebService().signIn(Json.encodePrettily(user)).setHandler(res2 -> {
                        jwtToTest = res2.result().getString("jwt");
                        jwtAuthHeader = "Bearer " + jwtToTest;
                        Future<UserFull> getUserFullFuture = getApiService().getDataRepository().getUserFull(userIdToTest);
                        Future<UserFull> getUserFullFuture2 = getApiService().getDataRepository().getUserFull(anotherUserIdToTest);
                        CompositeFuture compositeFuture2 = CompositeFuture.all(getUserFullFuture, getUserFullFuture2);
                        compositeFuture2.setHandler(res3 -> {
                            userFullToTest = compositeFuture2.resultAt(0);
                            anotherUserFullToTest = compositeFuture2.resultAt(1);
                            async.complete();
                        });
                    });
                });
            });
    }

    public HeyVerticle getHeyVerticle() {
        return heyVerticle;
    }
}
