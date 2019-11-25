package vn.elite.core.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Aes {
    private SecretKeySpec spec;

    private String key;

    public Aes() {
        this("default_key");
    }

    public Aes(String key) {
        this.key = key;
        setKey();
    }

    public static void main(String[] args) {
        Aes aes = new Aes();

        System.out.println(aes.encrypt("1"));
        String decrypt = aes.decrypt("2VKeCZcz9em9BabMKzniPQ==");
        System.out.println(decrypt);

    }

    private void setKey() {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = Arrays.copyOf(sha256.digest(this.key.getBytes(UTF_8)), 16);
            spec = new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String s) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, spec);
            return Base64.getEncoder().encodeToString(c.doFinal(s.getBytes(UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String s) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, spec);
            return new String(c.doFinal(Base64.getDecoder().decode(s.getBytes(UTF_8))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
