package vn.elite.fundamental.java.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

public class TraverseProperties {

    public static void main(String[] args) {
        TraverseProperties app = new TraverseProperties();
        app.printThemAll();
    }

    private void printThemAll() {
        String filename = "config.properties";

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                throw new IOException("Sorry, unable to find " + filename);
            }

            Properties prop = new Properties();
            prop.load(input);

            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                System.out.printf("Key : %15s, Value : %15s%n", key, value);
            }
        } catch (IOException ex) {
            Logger.getLogger(LoadPropFromClassPath.class.getName()).log(SEVERE, "Oopsie!", ex);
        }
    }
}
