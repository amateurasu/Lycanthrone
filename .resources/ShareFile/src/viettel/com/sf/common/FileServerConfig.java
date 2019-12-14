package viettel.com.sf.common;

import lombok.*;
import viettel.com.sf.dao.ConfigDao;
import viettel.com.sf.entity.ConfigTable;

import java.util.List;

@Data
public class FileServerConfig {
	@Setter
	@Getter
	private static String fileServerAddress;
	@Setter
	@Getter
	private static String fileServerRootPath;

	static {
		updateFileServerConfig();
	}

	public static void updateFileServerConfig() {
		ConfigDao fileConfigDao = new ConfigDao();
		AES aes = new AES();
		List<ConfigTable> listConfig = fileConfigDao.getConfigByCategory("fileserver");
		if (listConfig != null && listConfig.size() == 2) {
			fileServerAddress = aes.decrypt(listConfig.get(0).getValue());
			fileServerRootPath = aes.decrypt(listConfig.get(1).getValue());
		} else {
			fileServerAddress = null;
			fileServerRootPath = null;
		}
		//System.out.println(fileServerAddress + File.separator + fileServerRootPath);
	}
}
