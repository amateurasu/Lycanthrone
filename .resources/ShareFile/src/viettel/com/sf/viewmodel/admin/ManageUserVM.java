package viettel.com.sf.viewmodel.admin;

import lombok.*;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;
import viettel.com.sf.common.Constant;
import viettel.com.sf.dao.LoginoutDao;
import viettel.com.sf.entity.UserInformation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class ManageUserVM {
	private String searchId;
	private List<UserInformation> listResult = new ArrayList<>();
	private List<String> listStates;

	@Init
	public void init() {
		listStates = new ArrayList<>();
		listStates.add(Constant.AVAILABLE);
		listStates.add(Constant.DISABLED);
	}

	public List<String> getPermissions() {
		return Constant.USER_PERMISSIONS;
	}

	public List<String> getRoles() {
		return Constant.USER_ROLE;
	}

	public List<String> getStates() {
		return listStates;
	}

	@Command
	@NotifyChange("listResult")
	public void onClickSearchUser() {
		/* validate input */
		String input = searchId == null ? "" : searchId.trim();
		if ("".equals(input)) {
			Messagebox.show("Textfield can not null", "Error", Messagebox.OK, Messagebox.ERROR);
			listResult.clear();
			return;
		}
		String loginNamePattern = "^[a-z0-9]*$";
		Pattern regex = Pattern.compile(loginNamePattern);
		Matcher matcher = regex.matcher(input);
		if (!matcher.find()) {
			Messagebox.show("Only lowercase letters and digits are accepted", "Error", Messagebox.OK, Messagebox.ERROR);
			listResult.clear();
			return;
		}

		listResult = new LoginoutDao().findListUserByName(input);
	}

	@Command
	@NotifyChange("listResult")
	public void updateUserByAdmin(@BindingParam("param") UserInformation user) {
		// System.out.println("Hello" + user.toString());
		user.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
		LoginoutDao updateDao = new LoginoutDao();
		if (updateDao.updateUserInformation(user, 1)) {
			Sessions.getCurrent().setAttribute("login", user);
			Messagebox.show("User information successfully updated!");
		} else {
			Messagebox.show("Cannot update user information. Please try again later!", "Error", Messagebox.OK,
				Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("listResult")
	public void deleteUserByAdmin(@BindingParam("param") UserInformation user) {
		LoginoutDao updateDao = new LoginoutDao();
		if (updateDao.deleteUserInformation(user, 1)) {
			String input = searchId == null ? "" : searchId.trim();
			listResult = updateDao.findListUserByName(input);
			Messagebox.show("User's been deleted successfully!");
		} else {
			Messagebox.show("Cannot delete user information. Please try again later!", "Error",
				Messagebox.OK, Messagebox.ERROR);
		}
	}
}
