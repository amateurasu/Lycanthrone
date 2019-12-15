package viettel.com.sf.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;
import viettel.com.sf.authenticate.ADAuthenticator;
import viettel.com.sf.common.AppLog;
import viettel.com.sf.common.Constant;
import viettel.com.sf.common.StringGeneration;
import viettel.com.sf.common.Utils;
import viettel.com.sf.communicate.EmailSender;
import viettel.com.sf.dao.LoginoutDao;
import viettel.com.sf.entity.UserInformation;

import java.sql.Timestamp;
import java.util.UUID;

public class LoginoutController extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// private static
	static {
		EventQueues.lookup("loginQueue", EventQueues.APPLICATION, true);
	}

	private String clientIP;
	@Wire
	private Textbox userName, password, captchaTextBox, resetLoginName, resetEmail;
	@Wire
	private Label loginMess, resetMess;
	@Wire
	private Hlayout captchaId;
	@Wire
	private Row captchaInput;
	@Wire
	private Captcha myCaptcha;
	@Wire
	private Grid loginGrid, rsPwdGrid;
	private String sessionID, userNameLog;

	@SuppressWarnings({"unchecked", "rawtypes"})
	public LoginoutController() {
		AppLog.initialize();
		clientIP = Executions.getCurrent().getRemoteAddr();
		for (String ipHeader : Constant.IP_HEADER_CANDIDATES) {
			String ip = Executions.getCurrent().getHeader(ipHeader);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				clientIP = ip;
			}
		}
		Sessions.getCurrent().setAttribute("clientIP", clientIP);
		userNameLog = (String) Sessions.getCurrent().getAttribute("userNameLog");
		sessionID = (String) Sessions.getCurrent().getAttribute("sessionID");

		EventQueues.lookup("loginQueue", EventQueues.APPLICATION, true).subscribe(new EventListener() {
			public void onEvent(Event evt) {
				// handle the event just like any other event listener
				executeDuplicateLogin(evt);
			}
		});
	}

	protected void executeDuplicateLogin(Event evt) {
		// TODO Auto-generated method stub
		// System.out.println("duppppppp");
		if (evt.getName().equals("LoginEvent")) {
			String[] strArr = (String[]) evt.getData();
			// System.out.println(clientIP+ ": " +strArr[0] + " - " + strArr[1]
			// + " - " + strArr[2]);
			if (userNameLog != null && userNameLog.equals(strArr[0])) {
				if (clientIP == null || !clientIP.equals(strArr[1])) {
					Messagebox.show(
						"Your account login in IP: " + strArr[1] + ", if not you, please contact administrator!",
						"Warning", Messagebox.OK, Messagebox.EXCLAMATION);
					Sessions.getCurrent().invalidate();
				}
			}
		}
	}

	@Listen("onClick=#btnLogin;onOK=#userName,#password,#captchaTextBox,#btnLogin")
	public void loginCheck() {
		String username = userName.getValue().trim().toLowerCase();
		String pwd = password.getValue().trim();
		if (username.equals("") || pwd.equals("")) {
			loginMess.setValue(Constant.ERR_PLEASE_ENTER_INFO);
			return;
		}
		LoginoutDao loginDao = new LoginoutDao();
		Session session = Sessions.getCurrent();
		session.setAttribute("client_ip", clientIP);
		session.setAttribute("logInTime", System.currentTimeMillis());
		if (!loginDao.havePermitIP(clientIP)) {
			Messagebox.show("Something wrong. Please contact administrator!",
				"Error", Messagebox.OK, Messagebox.ERROR);
			userName.setValue("");
			password.setValue("");
			return;
		}
		UserInformation user = loginDao.findUserByUserName(username);
		if (user != null) {
			if (Constant.DISABLED.equals(user.getAvailable())) {
				Messagebox.show(
					"Your account is unavailable now, please contact with administrator!",
					"Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			if (user.getLoginFailCounter() >= Constant.MAX_LOG_IN_FAIL
				&& (System.currentTimeMillis() - user.getLoginFailTime().getTime()) <= 900000) {
				Messagebox.show(
					"You have failed to login 5 times! Please try again in 30 minutes, or contact administrator",
					"Error", Messagebox.OK, Messagebox.ERROR);
				/*
				 * captchaTextBox.setValue(""); userName.setValue("");
				 * password.setValue("");
				 */
				return;
			}

			// check capcha
			if (user.getLoginFailCounter() >= 4 && user.getLoginFailCounter() < Constant.MAX_LOG_IN_FAIL) {
				if (captchaId.isVisible() && captchaInput.isVisible()) {
					if (captchaTextBox.getValue().trim().equals("")) {
						loginMess.setValue("You have to fill in captcha box");
						return;
					} else if (!captchaTextBox.getValue().toUpperCase().equals(myCaptcha.getValue())) {
						loginMess.setValue("Captcha is not correct");
						// System.out.println(username + " login fail, incorect
						// captcha, IP: " + clientIP);
						AppLog.getOtherLog()
							.error("LOG INOUT: " + username + " login fail, incorect captcha, IP: " + clientIP);
						actionLoginFail(loginDao, user);
						/*
						 * userName.setValue(""); password.setValue("");
						 * captchaTextBox.setValue("");
						 */
						generateNewCapcha();
						return;
					}
				} else {
					captchaInput.setVisible(true);
					captchaId.setVisible(true);
					generateNewCapcha();
					loginMess.setValue(Constant.YOU_MUST_INPUT_CAPTCHA_CAUSE_BY_FAIL);
					/*
					 * captchaTextBox.setValue(""); userName.setValue("");
					 * password.setValue("");
					 */
					return;
				}
			}

			boolean validatePw = false;
			if (username.equals("admin") || username.equals("normal_user")) {
				String pwdHash = Utils.encryptSHA256(username + pwd + user.getSalt());
				if (pwdHash != null) {
					validatePw = pwdHash.equals(user.getPassword());
				} else {
					Messagebox.show("Cannot verify your account! Please try again later!", "Error", Messagebox.OK, Messagebox.ERROR);
					return;
				}
			} else {
				validatePw = ADAuthenticator.authenticate(username, pwd);
			}

			if (validatePw) {
				AppLog.getOtherLog().info("LOG INOUT: " + username + " login sucess from IP: " + clientIP);
				// save to session
				actionLoginSuscess(loginDao, user, session);
				// refresh page
				loginMess.setValue(null);
				sessionID = UUID.randomUUID().toString();
				session.setAttribute("userNameLog", username);
				session.setAttribute("sessionID", sessionID);

				EventQueues.lookup("loginQueue", EventQueues.APPLICATION, true)
					.publish(new Event("LoginEvent", null, new String[]{username, clientIP, sessionID}));
				Executions.sendRedirect("");
			} else {
				// System.out.println(username + " login fail, incorect
				// password, IP: " + clientIP);
				AppLog.getOtherLog().info("LOG INOUT: " + username + " login fail, incorect password, IP: " + clientIP);
				actionLoginFail(loginDao, user);
				if (user.getLoginFailCounter() >= 4) {
					captchaInput.setVisible(true);
					captchaId.setVisible(true);
					generateNewCapcha();
				}
				if (user.getLoginFailCounter() != 4) {
					if (user.getLoginFailCounter() == Constant.MAX_LOG_IN_FAIL) {
						loginMess.setValue(Constant.ERR_INVALID_PASSWORD_FIVE_TIME);
					} else {
						loginMess.setValue(Constant.ERR_INVALID_PASSWORD);
					}
				} else {
					loginMess.setValue(Constant.ERR_INVALID_PASSWORD_THREE_TIME);
				}
			}
		} else {
			loginMess.setValue(Constant.ERR_INVALID_USERNAME);
		}
		/*
		 * captchaTextBox.setValue(""); userName.setValue("");
		 * password.setValue("");
		 */
	}

	@Listen("onClick=#generateCaptcha")
	public void generateNewCapcha() {
		// TODO Auto-generated method stub
		myCaptcha.setValue(StringGeneration.randomAlphaNumeric(6));
		captchaTextBox.setValue("");
	}

	private void actionLoginFail(LoginoutDao loginDao, UserInformation user) {
		// TODO Auto-generated method stub
		if (user.getLoginFailCounter() < 6) {
			user.setLoginFailCounter(user.getLoginFailCounter() + 1);
		}
		user.setLoginFailTime(new Timestamp(System.currentTimeMillis()));
		loginDao.updateUserInformation(user, 1);
		loginDao.storeHistoryLogin(user, clientIP, Constant.ACTION_LOG_IN_FAIL, 1);
	}

	private void actionLoginSuscess(LoginoutDao loginDao, UserInformation user, Session session) {
		// TODO Auto-generated method stub
		session.setAttribute("login", user);
		session.setMaxInactiveInterval(Constant.SESSION_TIMEOUT_IN_SECONDS);
		// session_code?
		user.setLoginFailCounter(0);
		loginDao.updateUserInformation(user, 1);
		// loginDao.updateUserStatus(user, clientIP,
		// Constant.ACTION_LOG_IN_SUSCESS, 1);
		loginDao.storeHistoryLogin(user, clientIP, Constant.ACTION_LOG_IN_SUSCESS, 1);
	}

	@Listen("onClick=#forgot_pwd")
	public void clickForgotPwd() {
		loginGrid.setVisible(false);
		rsPwdGrid.setVisible(true);
	}

	@Listen("onClick=#btnCancel")
	public void clickCancel() {
		loginGrid.setVisible(true);
		rsPwdGrid.setVisible(false);
	}

	@Listen("onClick=#btnForgot")
	public void clickButtonForgot() {
		String loginName = resetLoginName.getValue().trim();
		String emailAdd = resetEmail.getValue().trim().toLowerCase();

		if (loginName.equals("") || emailAdd.equals("")) {
			Messagebox.show("Please enter username/email address", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		LoginoutDao resetDao = new LoginoutDao();
		UserInformation user = resetDao.findUserByUserAndEmail(loginName, emailAdd);
		if (user == null) {
			resetMess.setValue(Constant.ERR_INVALID_USERNAME_EMAIL);
		} else {
			// create new pass
			String newPass = StringGeneration.generateNewPassword();
			String hashPass = Utils.encryptSHA256(user.getLoginName() + newPass + user.getSalt());
			user.setPassword(hashPass);
			user.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
			user.setLoginFailCounter(0);
			EmailSender sender = new EmailSender();
			if (sender.checkEmailConfig()) {
				Messagebox.show("Error when reset password: Email config have problem!", "Error", Messagebox.OK,
					Messagebox.ERROR);
			}
			if (resetDao.updateUserInformation(user, 1)) {
				// send email
				String mess = String.format(
					"<b>Your new password information</b><br/>Login Name: <b>%s</b><br/>Password: <b>%s</b> <br/> <a>Please change password at the first time you login!</a>",
					user.getLoginName(), newPass);
				if (sender.sendEmail(new String[]{user.getEmail()}, Constant.EMAIL_SUBJECT + user.getLoginName(),
					mess)) {
					Messagebox.show("Your password has been reset! Please check email for the login information!",
						"Information", Messagebox.OK, Messagebox.INFORMATION);
				} else {
					Messagebox.show(
						"Your password has been reset!\r\nBut there's some problems while sending email. Please contact administrator for supported!",
						"Warning", Messagebox.OK, Messagebox.EXCLAMATION);
				}
			} else {
				Messagebox.show("Reset failed! Please contact administrator for supported!", "Error", Messagebox.OK,
					Messagebox.ERROR);
			}
		}
		resetLoginName.setValue("");
		resetEmail.setValue("");
	}

	@Listen("onClick=#buttonLogout,#labelLogout")
	public void clickLogout() {
		Session session = Sessions.getCurrent();
		UserInformation user = (UserInformation) session.getAttribute("login");
		LoginoutDao logoutDao = new LoginoutDao();
		// logoutDao.updateUserStatus(user, clientIP, Constant.ACTION_LOG_OUT,
		// 1);
		logoutDao.storeHistoryLogin(user, clientIP, Constant.LOG_OUT_STATUS, 1);
		session.removeAttribute("login");
		session.removeAttribute("client_ip");
		session.invalidate();
		Executions.sendRedirect("");
	}
}
