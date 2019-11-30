package com.hey;

import com.hey.util.PropertiesUtils;
import com.hey.verticle.HeyVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

@Slf4j
public class Hey {
    private static final String PROP_FILE_NAME = "system.properties";
    public static String ENV;

    public static void main(String[] args) {
        ENV = System.getenv("env");
        log.info("Environment: {}", ENV);

        if (StringUtils.isBlank(ENV)) {
            log.error("Missing env");
            System.exit(1);
        }
        System.out.printf("> Got env:%s", ENV);
        System.setProperty("env", ENV);
        initSystemProperty();
        initVertx();
    }

    public static URL getResource(String name) {
        return Hey.class.getResource(name);
    }

    public static InputStream getResourceAsStream(String name) {
        return Hey.class.getResourceAsStream(name);
    }

    private static void initSystemProperty() {
        Properties p = new Properties();
        try {
            p.load(Hey.getResourceAsStream(ENV + "." + PROP_FILE_NAME));
        } catch (IOException e) {
            log.error("Cannot load System Property", e);
            System.exit(1);
        }
        for (String name : p.stringPropertyNames()) {
            String value = p.getProperty(name);
            System.setProperty(name, value);
        }
    }

    private static void initVertx() {
        VertxOptions vertxOptions = new VertxOptions();
        Integer workerSize = PropertiesUtils.getInstance().getIntValue("worker.size");
        log.info("worker.size: {}", workerSize);
        vertxOptions.setWorkerPoolSize(workerSize);
        vertxOptions.setMaxEventLoopExecuteTime(Long.MAX_VALUE);

        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(vertxOptions.getEventLoopPoolSize());

        Vertx.vertx().deployVerticle(HeyVerticle.class, deploymentOptions);
    }
}
