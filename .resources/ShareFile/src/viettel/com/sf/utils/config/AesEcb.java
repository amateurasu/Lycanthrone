package viettel.com.sf.utils.config;

import lombok.val;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class AesEcb {
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String DEFAULT_KEY = "ems_vttek";

    public static String encrypt(String strToEncrypt) {
        return encrypt(strToEncrypt, DEFAULT_KEY);
    }

    public static String encrypt(String strToEncrypt, String key) {
        try {
            val cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, setKey(key));
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            return null;
        }
    }

    public static String decrypt(String strToDecrypt) {
        return decrypt(strToDecrypt, DEFAULT_KEY);
    }

    public static String decrypt(String strToDecrypt, String key) {
        try {
            val cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, setKey(key));
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            return null;
        }
    }

    private static SecretKeySpec setKey(String key) {
        try {
            val keyBytes = key.getBytes(StandardCharsets.UTF_8);
            val sha = MessageDigest.getInstance("SHA-256");
            return new SecretKeySpec(Arrays.copyOf(sha.digest(keyBytes), 16), "AES");
        } catch (Exception ignored) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(encrypt(""));
        System.out.println(decrypt("vx8f93hUWXAA75GDFutYyg=="));
    }
}
