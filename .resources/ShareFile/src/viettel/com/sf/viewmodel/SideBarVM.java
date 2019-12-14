package viettel.com.sf.viewmodel;

import org.zkoss.zk.ui.Sessions;
import viettel.com.sf.common.Constant;
import viettel.com.sf.entity.UserInformation;
import viettel.com.sf.layout.SidebarPage;

import java.util.ArrayList;
import java.util.List;

public class SideBarVM {

	private List<SidebarPage> allMVVMPages;

	public List<SidebarPage> getAllMVVMPages() {
		UserInformation loginUser = (UserInformation) Sessions.getCurrent().getAttribute("login");
		if (allMVVMPages == null) {
			allMVVMPages = new ArrayList<>();
			allMVVMPages.add(new SidebarPage("/layout/download.zul", "Download"));
			if (loginUser != null && loginUser.getRole().equals("NRM")) {
				allMVVMPages.add(new SidebarPage("/layout/upload.zul", "Upload"));
			}

			allMVVMPages.add(new SidebarPage("/layout/user_info.zul", "User information"));

			if (loginUser != null && loginUser.getRole().equals("ADM")) {
				allMVVMPages.add(new SidebarPage("/layout/admin.zul", "Administration"));
				allMVVMPages.add(new SidebarPage("/layout/history.zul", "History"));
			}

			if (Constant.USER_PERMISSIONS.get(1).equals(loginUser.getPermission())) {
				allMVVMPages.add(new SidebarPage("/layout/approve.zul", "Approve"));
			}
		}
		return allMVVMPages;
	}

	public void setAllMVVMPages(List<SidebarPage> allMVVMPages) {
		this.allMVVMPages = allMVVMPages;
	}
}
