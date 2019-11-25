package vn.elite.fundamental.java.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadPropFromClassPath {

    public static void main(String[] args) {

        Properties prop = new Properties();
        String filename = "config.properties";

        try (InputStream input = LoadPropFromClassPath.class.getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                throw new IOException("Sorry, unable to find " + filename);
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value and print it out
            System.out.println(prop.getProperty("database"));
            System.out.println(prop.getProperty("dbuser"));
            System.out.println(prop.getProperty("dbpassword"));
        } catch (IOException ex) {
            Logger.getLogger(LoadPropFromClassPath.class.getName()).log(Level.SEVERE, "Oopsie!", ex);
        }
    }
}
