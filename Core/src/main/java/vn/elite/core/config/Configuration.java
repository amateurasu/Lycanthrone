package vn.elite.core.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final String USER = "cfg/user.properties";
    private static final String DFLT = "/cfg/config.ini";
    //    private static final Logger logger = Logger.getLogger();
    private static Configuration instance = null;

    private Properties user;
    private Ini dflt;

    private Configuration() {
        try {
            user = loadUserConfig();
            dflt = new Ini(Configuration.class.getResourceAsStream(DFLT));
            instance = this;
        } catch (IOException e) { }
    }

    private static Properties loadUserConfig() {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(USER)) {
            prop.load(input);
        } catch (IOException ignored) { }

        return prop;
    }

    public static Configuration getInstance() {
        return instance == null ? new Configuration() : instance;
    }

    public String get(String key) {
        return getProp(user.getProperty(key));
    }

    public String get(String section, String key) {
        return dflt.get(section, key, "");
    }

    private String getProp(String s) {
        return s == null ? "" : s;
    }

    public void set(String key, String value) {
        user.setProperty(key, value);
    }

    //    public void saveConfig() {
    //        if (!Checker.invalidCreation(new File(USER))) {
    //            logger.log("Saving user config...");
    //            try (OutputStream output = new FileOutputStream(USER)) {
    //                user.store(output, null);
    //            } catch (IOException e) {            }
    //        } else {
    //            logger.error("Cannot save user config!");
    //        }
    //    }
}
