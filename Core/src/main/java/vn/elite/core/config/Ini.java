package vn.elite.core.config;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static vn.elite.core.utils.StringUtils.escape;
import static vn.elite.core.utils.StringUtils.unescape;

/**
 * @author duclm
 */
public class Ini {

    private static final Pattern sectionPattern;
    private static final Pattern keyValuePattern;
    private static final HashSet<Character> comments;

    static {
        sectionPattern = Pattern.compile("\\s*\\[([^]]*)]\\s*");
        keyValuePattern = Pattern.compile("\\s*([^=]*)=(.*)");
        comments = new HashSet<Character>() {{
            add('#');
            add(';');
        }};
    }

    private final Map<String, Map<String, String>> entries = new LinkedHashMap<>();

    public Ini() {}

    public Ini(String path) throws IOException {
        load(path);
    }

    public Ini(InputStream stream) throws IOException {
        load(stream);
    }

    public static void defineCommentChars(Character... chars) {
        comments.clear();
        comments.addAll(Arrays.asList(chars));
    }

    public void save(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        entries.forEach((key, value) -> {
            sb.append('[').append(key).append(']').append('\n');
            value.forEach((k, v) -> sb.append(escape(k)).append('=').append(escape(v)).append('\n'));
            sb.append('\n');
        });

        try (OutputStream stream = new FileOutputStream(path)) {
            stream.write(sb.toString().getBytes());
        }
    }

    public void load(String path) throws IOException {
        entries.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            parse(br);
        }
    }

    public void load(InputStream stream) throws IOException {
        entries.clear();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            parse(br);
        }
    }

    private void parse(BufferedReader br) throws IOException {
        String line;
        String section = null;
        while ((line = br.readLine()) != null) {
            if (line.length() < 1 || comments.contains(line.trim().charAt(0))) {
                continue;
            }
            Matcher checker = sectionPattern.matcher(line);
            if (checker.matches()) {
                section = checker.group(1).trim();
            } else {
                checker = keyValuePattern.matcher(line);
                if (!checker.matches()) {continue;}

                String key = unescape(checker.group(1).trim());
                String value = unescape(checker.group(2).trim());
                put(section, key, value);
            }
        }
    }

    public void put(String section, String key, String value) {
        entries.computeIfAbsent(section, k -> new LinkedHashMap<>()).put(key, value);
    }

    public String get(String section, String key) {
        Map<String, String> map = entries.get(section);
        return map == null ? null : map.get(key);
    }

    public String get(String section, String key, String defaultValue) {
        String s = get(section, key);
        return s != null ? s : defaultValue;
    }

    public int getInt(String section, String key, int defaultValue) {
        String s = get(section, key);
        try {
            return s != null ? Integer.parseInt(s) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float getFloat(String section, String key, float defaultValue) {
        String s = get(section, key);
        try {
            return s != null ? Float.parseFloat(s) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double getDouble(String section, String key, double defaultValue) {
        String s = get(section, key);
        try {
            return s != null ? Double.parseDouble(s) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean contains(String key) {
        return entries.containsKey(key);
    }

    public Map<String, String> get(String key) {
        return entries.get(key);
    }
}
