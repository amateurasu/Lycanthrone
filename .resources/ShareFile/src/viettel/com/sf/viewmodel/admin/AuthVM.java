package viettel.com.sf.viewmodel.admin;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.Messagebox;
import viettel.com.sf.authenticate.ADAuthenticator;
import viettel.com.sf.common.AES;
import viettel.com.sf.dao.ConfigDao;
import viettel.com.sf.entity.ConfigTable;

import java.sql.Timestamp;
import java.util.List;

@Data
public class AuthVM {
	private String ldapDomain, ldapHost, searchBase;
	private ConfigDao configDao = new ConfigDao();
	private AES aes = new AES();
	private List<ConfigTable> listConfig;

	@Init
	public void init() {
		listConfig = configDao.getConfigByCategory("auth");
		if (listConfig != null && listConfig.size() == 3) {
			ldapDomain = aes.decrypt(listConfig.get(0).getValue());
			ldapHost = aes.decrypt(listConfig.get(1).getValue());
			searchBase = aes.decrypt(listConfig.get(2).getValue());
		} else {
			ldapDomain = ldapHost = searchBase = "";
		}
	}

	@Command
	@NotifyChange({"ldapDomain", "ldapHost", "searchBase"})
	public void onClickUpdateAuthDetail() {
		if (listConfig != null && listConfig.size() == 3) {
			listConfig.get(0).setValue(aes.encrypt(ldapDomain));
			listConfig.get(1).setValue(aes.encrypt(ldapHost));
			listConfig.get(2).setValue(aes.encrypt(searchBase));

			listConfig.get(0).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
			listConfig.get(1).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
			listConfig.get(2).setLastUpdateTime(new Timestamp(System.currentTimeMillis()));

			if (configDao.updateConfig(listConfig, 1)) {
				Messagebox.show("Successfully updated!");
				ADAuthenticator.readADParame();
			} else {
				Messagebox.show("Update failed");
			}
		}
		init();
	}
}
