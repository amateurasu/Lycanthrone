package viettel.com.sf.communicate;

import com.sun.mail.util.MailSSLSocketFactory;
import viettel.com.sf.common.AES;
import viettel.com.sf.common.AppLog;
import viettel.com.sf.dao.ConfigDao;
import viettel.com.sf.entity.ConfigTable;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class EmailSender {

	private static String emailAddr;
	private static String emailPwd;
	private static String emailHost;
	private static String emailPort;
	private static String emailTLsEnable;

	static {
		updateEmailConfig();
	}

	public static void updateEmailConfig() {
		ConfigDao configDao = new ConfigDao();
		AES aes = new AES();
		List<ConfigTable> listConfig = configDao.getConfigByCategory("email");
		if (listConfig != null && listConfig.size() == 5) {
			emailHost = aes.decrypt(listConfig.get(0).getValue());
			emailPort = listConfig.get(1).getValue();
			emailAddr = listConfig.get(2).getValue();
			emailPwd = aes.decrypt(listConfig.get(3).getValue());
			emailTLsEnable = listConfig.get(4).getValue();
		}
	}

	public boolean checkEmailConfig() {
		return emailAddr.isEmpty() || emailPwd.isEmpty() || emailHost.isEmpty() || emailPort.isEmpty();
	}

	public boolean sendEmail(String[] recvAddr, String subject, String mess) {
		try {
			Properties props = System.getProperties();
			//props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtp.host", emailHost);
			props.put("mail.smtp.port", emailPort);
			props.put("mail.smtp.timeout", 200000);
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.connectiontimeout", 200000);
			props.put("mail.smtp.starttls.enable", emailTLsEnable);
			MailSSLSocketFactory socketFactory = new MailSSLSocketFactory();
			socketFactory.setTrustAllHosts(true);
			props.put("mail.smtp.ssl.socketFactory", socketFactory);

			Authenticator pa = null;
			if (emailAddr != null && emailPwd != null) {
				props.put("mail.smtp.auth", "true");
				pa = new Authenticator() {
					@Override
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(emailAddr, emailPwd);
					}
				};
			}

			Session session = Session.getInstance(props, pa);
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailAddr));
			InternetAddress[] addressTo = new InternetAddress[recvAddr.length];
			for (int i = 0; i < recvAddr.length; i++) {
				addressTo[i] = new InternetAddress(recvAddr[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);
			msg.setSubject(subject);
			msg.setContent(mess, "text/html; charset=UTF-8");
			msg.setHeader("X-Mailer", "LOTONtechEmail");
			msg.setSentDate(new Date());
			msg.saveChanges();
			Transport.send(msg);

			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			AppLog.getOtherLog().error("Send Email: " + e.getMessage());
			return false;
		}
	}
}
