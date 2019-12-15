package viettel.com.sf.viewmodel.admin;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.Messagebox;
import viettel.com.sf.common.AES;
import viettel.com.sf.communicate.EmailSender;
import viettel.com.sf.dao.ConfigDao;
import viettel.com.sf.entity.ConfigTable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailVM {
	private String emailAddr, emailPwd, emailHost;
	private int emailPort;
	private boolean emailTLS;
	private List<Boolean> listTLS = new ArrayList<>();
	private ConfigDao configDao = new ConfigDao();
	private AES aes = new AES();
	private List<ConfigTable> listConfig;

	@Init
	public void init() {
		listConfig = configDao.getConfigByCategory("email");
		if (listConfig != null && listConfig.size() == 5) {
			emailHost = aes.decrypt(listConfig.get(0).getValue());
			emailPort = Integer.parseInt(listConfig.get(1).getValue());
			emailAddr = listConfig.get(2).getValue();
			emailPwd = aes.decrypt(listConfig.get(3).getValue());
			emailTLS = listConfig.get(4).getValue().equals("true");
			listTLS.add(true);
			listTLS.add(false);
		} else {
			emailAddr = emailPwd = emailHost = "";
			emailPort = 0;
			emailTLS = false;
		}
	}

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	public String getEmailPwd() {
		return emailPwd;
	}

	public void setEmailPwd(String emailPwd) {
		this.emailPwd = emailPwd;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}

	public int getEmailPort() {
		return emailPort;
	}

	public void setEmailPort(int emailPort) {
		this.emailPort = emailPort;
	}

	public boolean getEmailTLS() {
		return emailTLS;
	}

	public void setEmailTLS(boolean emailTLS) {
		this.emailTLS = emailTLS;
	}

	public List<Boolean> getListTLS() {
		return listTLS;
	}

	public void setListTLS(List<Boolean> listTLS) {
		this.listTLS = listTLS;
	}

	@Command
	@NotifyChange({"emailAddr", "emailPwd", "emailHost", "emailPort", "emailTLS"})
	public void onClickUpdateEmailDetail() {
		if (listConfig != null && listConfig.size() == 5) {
			String emailPattern = "^[A-Za-z0-9]{3,}@vttek.vn$";
			Pattern regex = Pattern.compile(emailPattern);
			Matcher matcher = regex.matcher(emailAddr.trim());
			if (!matcher.find()) {
				Messagebox.show("Wrong email format: only accept @vttek.vn email", "Error", Messagebox.OK,
					Messagebox.ERROR);
				return;
			}

			String pwPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*_=+-/])[A-Za-z\\d!@#$%^&*_=+-/]{8,16}$";
			regex = Pattern.compile(pwPattern);
			matcher = regex.matcher(emailPwd.trim());
			if (matcher.find()) {
			} else {
				Messagebox.show(
					"Password length from 8-16, must have at least one uppercase letter, one lowercase letter, one number and one special character (!@#$%^&*_=+-/)",
					"Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}

			listConfig.get(0).setValue(aes.encrypt(emailHost));
			listConfig.get(1).setValue(String.valueOf(emailPort));
			listConfig.get(2).setValue(emailAddr);
			listConfig.get(3).setValue(aes.encrypt(emailPwd));
			listConfig.get(4).setValue(emailTLS ? "true" : "false");
			listConfig.get(0).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
			listConfig.get(1).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
			listConfig.get(2).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
			listConfig.get(3).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
			listConfig.get(4).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
			if (configDao.updateConfig(listConfig, 1)) {
				Messagebox.show("Updated");
				EmailSender.updateEmailConfig();
			} else {
				Messagebox.show("Update failed");
			}
		}
		init();
	}
}
