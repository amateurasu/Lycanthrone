package vn.elite.fundamental.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class Crypto {
    public static String decrypt(String s) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, getKey("ems_vttek"));
            return new String(cipher.doFinal(Base64.getDecoder().decode(s)));
        } catch (Exception e) {
            return null;
        }
    }

    public static SecretKeySpec getKey(String myKey) {
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        // val bytes = encrypt("Le Minh Duc", "Lê Minh Đức".getBytes());
        // System.out.println(Arrays.toString(bytes));
        // System.out.println(new String(bytes));
        // System.out.println(new String(AesCbc.decrypt("Le Minh Duc", bytes)));
        System.out.println(decrypt("2VKeCZcz9em9BabMKzniPQ=="));
    }
}
