package edu.fpt.comp1640.database;

import lombok.val;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

class Security {
    private static final int DEFAULT_ITERATIONS = 1000;

    boolean validatePassword(String originalPass, String storedPass) throws NoSuchAlgorithmException, InvalidKeySpecException {
        val parts = storedPass.split(":");
        val iterations = Integer.parseInt(parts[0]);
        val salt = fromHex(parts[1]);
        val hash = fromHex(parts[2]);

        val spec = new PBEKeySpec(originalPass.toCharArray(), salt, iterations, hash.length * 8);
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        val testHash = skf.generateSecret(spec).getEncoded();
        var different = hash.length ^ testHash.length;
        for (var i = 0; i < hash.length && i < testHash.length; i++) {
            different |= hash[i] ^ testHash[i];
        }
        return different == 0;
    }

    String createPassword(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return createPassword(DEFAULT_ITERATIONS, password);
    }

    String createPassword(int iterations, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        val chars = password.toCharArray();
        val salt = getSalt();
        val spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        val hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private byte[] fromHex(String hex) {
        val bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    private String toHex(byte[] array) {
        val big = new BigInteger(1, array);
        val hex = big.toString(16);
        val paddingLength = array.length * 2 - hex.length();
        return paddingLength > 0 ? String.format("%0" + paddingLength + "d", 0) + hex : hex;
    }

    private byte[] getSalt() throws NoSuchAlgorithmException {
        val salt = new byte[16];
        SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
        return salt;
    }
}
