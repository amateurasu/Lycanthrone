package viettel.com.sf.viewmodel.admin;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.Messagebox;
import viettel.com.sf.common.AES;
import viettel.com.sf.communicate.Scp;
import viettel.com.sf.dao.ConfigDao;
import viettel.com.sf.entity.ConfigTable;

import java.sql.Timestamp;
import java.util.List;

public class ServerConfigVM {
	private String ipAddr, user, pwd;
	private int port, maxDownload;

	private ConfigDao configDao = new ConfigDao();
	private AES aes = new AES();
	private List<ConfigTable> listConfig;

	@Init
	public void init() {
		listConfig = configDao.getConfigByCategory(Scp.getServerType());
		if (listConfig != null && listConfig.size() == 5) {
			ipAddr = aes.decrypt(listConfig.get(0).getValue());
			user = aes.decrypt(listConfig.get(1).getValue());
			pwd = aes.decrypt(listConfig.get(2).getValue());
			port = Integer.parseInt(listConfig.get(3).getValue());
			maxDownload = Integer.parseInt(listConfig.get(4).getValue());
		} else {
			ipAddr = user = pwd = "";
			port = maxDownload = 0;
		}
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMaxDownload() {
		return maxDownload;
	}

	public void setMaxDownload(int maxDownload) {
		this.maxDownload = maxDownload;
	}

	@Command
	@NotifyChange({"ipAddr", "user", "pwd", "port"})
	public void onClickUpdateServer() {
		// validate

		listConfig.get(0).setValue(aes.encrypt(ipAddr));
		listConfig.get(1).setValue(aes.encrypt(user));
		listConfig.get(2).setValue(aes.encrypt(pwd));
		listConfig.get(3).setValue(String.valueOf(port));
		listConfig.get(4).setValue(String.valueOf(maxDownload));
		listConfig.get(0).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
		listConfig.get(1).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
		listConfig.get(2).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
		listConfig.get(3).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
		listConfig.get(4).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
		if (configDao.updateConfig(listConfig, 1)) {
			Messagebox.show("Updated");
			Scp.getRemoteServerConfig();
		} else {
			Messagebox.show("Update failed");
		}
		init();
	}
}
