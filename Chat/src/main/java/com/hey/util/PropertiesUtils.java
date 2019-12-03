package com.hey.util;

import com.hey.Hey;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public final class PropertiesUtils {

    private static final String PROP_FILE = "application.properties";
    private static PropertiesUtils instance;
    private Properties properties;

    private PropertiesUtils() {
        String env = Hey.ENV;
        try {
            properties = new Properties();
            properties.load(Hey.getResourceAsStream(env + "." + PROP_FILE));
        } catch (IOException ioe) {
            log.error("Error reading config properties.", ioe);
            throw new RuntimeException(ioe);
        }
    }

    public static PropertiesUtils getInstance() {
        if (instance == null) {
            instance = new PropertiesUtils();
        }
        return instance;
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

    public Integer getIntValue(String key) {
        try {
            return Integer.valueOf(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
