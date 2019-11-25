package vn.elite.fundamental.java.properties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class WriteProperties {
    public static void main(String[] args) {
        try (OutputStream output = new FileOutputStream("config.properties")) {
            Properties properties = new Properties();

            // set the properties value
            properties.setProperty("database", "localhost");
            properties.setProperty("dbuser", "duclm");
            properties.setProperty("dbpassword", "password");

            // save properties to project root folder
            properties.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
