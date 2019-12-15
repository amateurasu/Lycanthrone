package viettel.com.sf.common;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class AES {
	private SecretKeySpec secretKey;

	private String myKey = "file_sharing_vpt";

	public AES() {
		setKey();
	}

	public AES(String myKey) {
		this.myKey = myKey;
		setKey();
	}

	public void setKey() {
		try {
			byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
			key = MessageDigest.getInstance("SHA-256").digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
			// System.out.println("key:" + secretKey);
		} catch (Exception e) {
			AppLog.getOtherLog().error("AES setkey: " + e.getMessage());
		}
	}

	public void setKey(String salt) {
		try {
			byte[] key = (myKey + salt).getBytes(StandardCharsets.UTF_8);
			key = MessageDigest.getInstance("SHA-256").digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
			// System.out.println("key:" + secretKey);
		} catch (Exception e) {
			AppLog.getOtherLog().error("AES setkey: " + e.getMessage());
		}
	}

	public String encrypt(String strToEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			AppLog.getOtherLog().error("AES encrypt: " + e.getMessage());
		}
		return null;
	}

	public String decrypt(String strToDecrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			AppLog.getOtherLog().error("AES decrypt: " + e.getMessage());
		}
		return null;
	}

	public static void main(String[] args) {
		AES aes = new AES();
		System.out.println(aes.decrypt("AH1L+8TvI16ifXLBacZqWA==") + "file_server_addr");
		System.out.println(aes.decrypt("o/1p5QBxYif+TxbWAmMpMQ==") + "email_host");
		System.out.println(aes.decrypt("+0g38B2o7Gh6tliaCcBijismlT/nryQCdwNBdZYJ7EU=") + "email_pass");
		System.out.println(aes.decrypt("GGW1ZXESR4A8E4FQ2scTiA==") + "username");
		System.out.println(aes.decrypt("5Bz1QvvJ85RiDcJNWqUpGQ==") + "password");
	}
}
