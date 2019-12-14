package viettel.com.sf.utils.config;

import java.io.IOException;

public class Config {
    private static Ini ini;

    private Config() {}

    public static Ini load(String path) throws IOException {
        return ini = new Ini(path);
    }

    public static Ini getInstance() {
        return ini;
    }
}
