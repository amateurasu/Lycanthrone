package vn.elite.core.crypto;

import lombok.val;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public static byte[] digest(byte[] bin) throws IOException, NoSuchAlgorithmException {
        return digest(new ByteArrayInputStream(bin));
    }

    public static byte[] digest(InputStream is) throws IOException, NoSuchAlgorithmException {
        val buffer = new byte[1024];
        val md5 = MessageDigest.getInstance("MD5");

        int numRead;
        while ((numRead = is.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }

        return md5.digest();
    }

    public static String getChecksum(File file) throws IOException, NoSuchAlgorithmException {
        val bytes = digest(new FileInputStream(file));
        return toString(bytes);
    }

    public static String hash(byte[] bytes) throws IOException, NoSuchAlgorithmException {
        return toString(digest(bytes));
    }

    private static String toString(byte[] bytes) {
        val result = new StringBuilder();

        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}
