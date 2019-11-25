package vn.elite.fundamental.java.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadProperties {
    public static void main(String[] args) {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("config.properties")) {
            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("database"));
            System.out.println(prop.getProperty("dbuser"));
            System.out.println(prop.getProperty("dbpassword"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
