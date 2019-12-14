package viettel.com.sf.common;

import java.security.SecureRandom;

public class StringGeneration {

	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private static final String ALPHA_NUM_SPECIAL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*_=+-/";

	public static String randomAlphaNumeric(int count) {
		StringBuilder builder = new StringBuilder();
		SecureRandom rand = new SecureRandom();
		while (count-- != 0) {
			int character = (int) (rand.nextDouble() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}

	public static String generateNewPassword() {
		SecureRandom rand = new SecureRandom();
		int lengthPw = 8 + rand.nextInt(8);
		char[] pass = new char[lengthPw];
		int index = 0;

		// caplock
		do {
			index = rand.nextInt(lengthPw);
		} while (pass[index] != 0);

		pass[index] = ALPHA_NUM_SPECIAL.charAt(rand.nextInt(26));
		// numberic
		do {
			index = rand.nextInt(lengthPw);
		} while (pass[index] != 0);

		pass[index] = ALPHA_NUM_SPECIAL.charAt(52 + rand.nextInt(10));
		// special char
		do {
			index = rand.nextInt(lengthPw);
		} while (pass[index] != 0);

		pass[index] = ALPHA_NUM_SPECIAL.charAt(62 + rand.nextInt(13));
		// normal char
		do {
			index = rand.nextInt(lengthPw);
		} while (pass[index] != 0);

		pass[index] = ALPHA_NUM_SPECIAL.charAt(26 + rand.nextInt(26));
		//remain char
		for (int i = 0; i < lengthPw; i++) {
			if (pass[i] == 0) {
				pass[i] = ALPHA_NUM_SPECIAL.charAt(rand.nextInt(ALPHA_NUM_SPECIAL.length()));
			}
		}
		return new String(pass);
	}
}
