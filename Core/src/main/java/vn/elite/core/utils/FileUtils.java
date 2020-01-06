package vn.elite.core.utils;

import java.io.*;

public class FileUtils {
    public static boolean mkdirs(File file) {
        return invalidCreation(file);
    }

    public static boolean save(String content, File file) {
        if (invalidCreation(file)) return false;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void save(byte[] content, File file) throws IOException {
        save(content, file, false);
    }

    public static void save(byte[] content, File file, boolean append) throws IOException {
        if (invalidCreation(file)) return;

        try (OutputStream stream = new FileOutputStream(file, append)) {
            stream.write(content);
        }
    }

    public static boolean remove(File dir) {
        if (dir == null || !dir.exists()) return true;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (valid(files)) {
                for (File file : files) {
                    remove(file);
                }
            }
        }

        return dir.delete();
    }

    public static <T> boolean valid(T[] array) {
        return array != null && array.length > 0;
    }

    private static boolean invalidCreation(File file) {
        File folder = file.getParentFile();
        return !(folder.exists() || folder.mkdirs());
    }
}
