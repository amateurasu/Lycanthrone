package viettel.com.sf.viewmodel;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;
import viettel.com.sf.common.Constant;
import viettel.com.sf.dao.FileDao;
import viettel.com.sf.entity.FileInfomation;
import viettel.com.sf.entity.UserInformation;

import java.util.List;

public class ApproveVM {

	private UserInformation userLogin = ((UserInformation) Sessions.getCurrent().getAttribute("login"));
	private List<FileInfomation> listFile;

	public List<FileInfomation> getListFile() {
		if (listFile == null) {
			listFile = new FileDao().getAllPendingFileByUser(userLogin);
		}
		return listFile;
	}

	@Command
	@NotifyChange("listFile")
	public void approveFile(@BindingParam("file") FileInfomation file) {
		file.setAvailable(Constant.AVAILABLE);
		if (!new FileDao().updateFileInfor(file, 1)) {
			Messagebox.show("Have some thing error when approve file!", "Error", Messagebox.OK, Messagebox.ERROR);
		} else {
			listFile.remove(file);
		}
		/* send email notify for uploader */
	}

	@Command
	@NotifyChange("listFile")
	public void rejectFile(@BindingParam("file") FileInfomation file) {
		file.setAvailable(Constant.REJECT);
		new FileDao().updateFileInfor(file, 1);
		if (!new FileDao().updateFileInfor(file, 1)) {
			Messagebox.show("Have some thing error when reject file!", "Error", Messagebox.OK, Messagebox.ERROR);
		} else {
			listFile.remove(file);
		}
		/* send email notify for uploader */
	}
}
