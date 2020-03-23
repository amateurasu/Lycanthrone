package vn.elite.core.utils;

import java.io.*;

public class FileUtils {
    public static String byteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

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
