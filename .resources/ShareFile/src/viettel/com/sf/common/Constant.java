package viettel.com.sf.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constant {
	public static final int SESSION_TIMEOUT_IN_SECONDS = 900;

	public static final String ERR_PLEASE_ENTER_INFO = "Please fill login information";
	public static final String ERR_INVALID_USERNAME = "Username or Password invalid";
	public static final String ERR_INVALID_PASSWORD = "Username or Password invalid";
	public static final String ERR_INVALID_USERNAME_EMAIL = "Username or Email is not correct!";
	public static final String ERR_INVALID_PASSWORD_THREE_TIME = "Username or password is incorrect! You have failed to login 3 times!";
	public static final String ERR_INVALID_PASSWORD_FIVE_TIME = "You have failed to login 5 times! Please try again in 30 minutes!";

	public static final String EMAIL_SUBJECT = "[ShareFile System] have reseted password of your account: ";
	public static final String NEW_USER_EMAIL_SUBJECT = "[ShareFile System] Your account was created: ";
	public static final String EMAIL_APPROVE_SUBJECT = "[ShareFile System] Approve request";

	public static final String YOU_MUST_INPUT_CAPTCHA_CAUSE_BY_FAIL = "You have to input captcha because you have failed to login 3 times before!";

	public static final List<String> IP_HEADER_CANDIDATES = Collections.unmodifiableList(Arrays.asList(
		"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED",
		"HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"));
	public static final List<String> FILE_TYPE_CANDIDATES = Collections
		.unmodifiableList(Arrays.asList("pdf", "xlsx", "docx", "pptx", "xls", "doc", "ppt"));

	public static final List<String> USER_ROLE = Collections.unmodifiableList(Arrays.asList("ADM", "NRM"));

	public static final List<String> USER_PERMISSIONS = Collections.unmodifiableList(Arrays.asList("ONE_WAY", "TWO_WAY"));

	public static final String AVAILABLE = "AVAILABLE";
	public static final String PENDING = "PENDING";
	public static final String REJECT = "REJECT";
	public static final String DISABLED = "DISABLED";

	public static final int MAX_RETRY_DB = 5;
	public static final int MAX_LOG_IN_FAIL = 6;

	public static final char LOG_IN_STATUS = '0';
	public static final char LOG_OUT_STATUS = '2';

	public static final char ACTION_LOG_IN_SUSCESS = '0';
	public static final char ACTION_LOG_IN_FAIL = '1';
	public static final char ACTION_LOG_OUT = '2';
	public static final char ACTION_UPLOAD = '3';
	public static final char ACTION_DOWNLOAD = '4';
	public static final char ACTION_SEARCH = '5';
}
