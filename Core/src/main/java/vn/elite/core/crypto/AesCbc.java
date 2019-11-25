package vn.elite.core.crypto;

import lombok.val;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public class AesCbc {
    private static final byte[] SALTED = "Salted__".getBytes();
    private static IvParameterSpec ivSpec;
    private static SecretKeySpec keySpec;

    /*
     * Decrypt the string data, using the password secret,
     * in an openssl-compatible way. This function provides the equivalent
     * of the command:
     * echo <data> | openssl aes-256-cbc -d -base64 -pass pass:<secret>
     */
    public static byte[] decrypt(String password, byte[] data) throws Exception {
        val salt = Arrays.copyOfRange(data, 8, 16);
        val encrypted = Arrays.copyOfRange(data, 16, data.length);
        deriveKeyAndIV(password, salt);

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(encrypted);
    }

    /*
     * Encrypt the string data, using the password secret.
     * This function provides the equivalent of the command:
     * echo <data> | openssl aes-256-cbc -base64 -pass pass:<secret>
     */
    public static byte[] encrypt(String password, byte[] data) throws Exception {
        val salt = deriveKeyAndIV(password);
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        val encrypted = cipher.doFinal(data);

        val finalCode = new byte[16 + encrypted.length];
        System.arraycopy(SALTED, 0, finalCode, 0, 8);
        System.arraycopy(salt, 0, finalCode, 8, 8);
        System.arraycopy(encrypted, 0, finalCode, 16, encrypted.length);

        return finalCode;
    }

    /*
     * Derive an key and IV from a password and a salt
     */
    private static byte[] deriveKeyAndIV(String password) throws Exception {
        val salt = new byte[8];
        new SecureRandom().nextBytes(salt);
        deriveKeyAndIV(password, salt);
        return salt;
    }

    private static void deriveKeyAndIV(String password, byte[] salt) throws Exception {
        val md5 = MessageDigest.getInstance("MD5");
        val secret = password.getBytes();

        val secretSalt = combine(secret, salt);
        md5.update(secretSalt);
        val hash1 = md5.digest();

        md5.reset();
        md5.update(combine(hash1, secretSalt));
        val hash2 = md5.digest();

        md5.reset();
        md5.update(combine(hash2, secretSalt));
        val iv = md5.digest();
        val key = combine(hash1, hash2);
        keySpec = new SecretKeySpec(key, "AES");
        ivSpec = new IvParameterSpec(iv);
    }

    private static byte[] combine(byte[] a1, byte[] a2) {
        byte[] combine = new byte[a1.length + a2.length];
        System.arraycopy(a1, 0, combine, 0, a1.length);
        System.arraycopy(a2, 0, combine, a1.length, a2.length);
        return combine;
    }

    private static byte[] combine(byte[]... arrays) {
        int count = 0;
        for (byte[] array : arrays) {
            count += array.length;
        }
        byte[] combine = new byte[count];
        int current = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, combine, current, array.length);
            current += array.length;
        }
        return combine;
    }
}
