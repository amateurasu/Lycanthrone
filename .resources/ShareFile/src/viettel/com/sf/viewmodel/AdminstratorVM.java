package viettel.com.sf.viewmodel;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;
import viettel.com.sf.common.Constant;
import viettel.com.sf.common.Utils;
import viettel.com.sf.communicate.EmailSender;
import viettel.com.sf.communicate.Scp;
import viettel.com.sf.dao.LoginoutDao;
import viettel.com.sf.entity.Organization;
import viettel.com.sf.entity.UserInformation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminstratorVM {
	public static final char ACTION_LOG_IN_SUSCESS = '0';
	public static final char ACTION_LOG_IN_FAIL = '1';
	public static final char ACTION_LOG_OUT = '2';
	public static final char ACTION_UPLOAD = '3';
	public static final char ACTION_DOWNLOAD = '4';
	private String newUserLoginName, newUserFullName, newUserEmail, newUserDepart, newUserPhone, newUserManagerList,
		optionConfig = "MAIL", newUserRole = Constant.USER_ROLE.get(1),
		newUserPermission = Constant.USER_PERMISSIONS.get(0);
	private String gridSubject = "Email Server Config";
	private String sourceConfig = "/layout/admin_layout/email.zul";

	public AdminstratorVM() {
		oncheckRadioButton();
	}

	public String getGridSubject() {
		return gridSubject;
	}

	public void setGridSubject(String gridSubject) {
		this.gridSubject = gridSubject;
	}

	public String getOptionConfig() {
		return optionConfig;
	}

	public void setOptionConfig(String optionConfig) {
		this.optionConfig = optionConfig;
	}

	public String getNewUserRole() {
		return newUserRole;
	}

	public void setNewUserRole(String newUserRole) {
		this.newUserRole = newUserRole;
	}

	public String getNewUserLoginName() {
		return newUserLoginName;
	}

	public void setNewUserLoginName(String newUserLoginName) {
		this.newUserLoginName = newUserLoginName;
	}

	public String getNewUserFullName() {
		return newUserFullName;
	}

	public void setNewUserFullName(String newUserFullName) {
		this.newUserFullName = newUserFullName;
	}

	public String getNewUserEmail() {
		return newUserEmail;
	}

	public void setNewUserEmail(String newUserEmail) {
		this.newUserEmail = newUserEmail;
	}

	public String getNewUserDepart() {
		return newUserDepart;
	}

	public void setNewUserDepart(String newUserDepart) {
		this.newUserDepart = newUserDepart;
	}

	public String getNewUserPhone() {
		return newUserPhone;
	}

	public void setNewUserPhone(String newUserPhone) {
		this.newUserPhone = newUserPhone;
	}

	public String getSourceConfig() {
		return sourceConfig;
	}

	public void setSourceConfig(String sourceConfig) {
		this.sourceConfig = sourceConfig;
	}

	public String getNewUserPermission() {
		return newUserPermission;
	}

	public void setNewUserPermission(String newUserPermission) {
		this.newUserPermission = newUserPermission;
	}

	public List<String> getRoles() {
		return Constant.USER_ROLE;
	}

	public List<String> getPermissions() {
		return Constant.USER_PERMISSIONS;
	}

	public String getNewUserManagerList() {
		return newUserManagerList;
	}

	public void setNewUserManagerList(String newUserManagerList) {
		this.newUserManagerList = newUserManagerList;
	}

	@Command
	@NotifyChange({"newUserLoginName", "newUserFullName", "newUserEmail", "newUserDepart", "newUserPhone",
		"newUserManagerList", "newUserPermission"})
	public void onClickCreateUser() {
		// validate
		if (newUserLoginName == null || newUserLoginName.trim().equals("") || newUserFullName == null
			|| newUserFullName.trim().equals("") || newUserEmail == null || newUserEmail.trim().equals("")
			|| newUserDepart == null || newUserDepart.trim().equals("") || newUserPhone == null
			|| newUserPhone.trim().equals("")) {
			Messagebox.show("Please fill all required fields", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		String emailPattern = "^[A-Za-z0-9]{3,}@vttek.vn$";
		Pattern regex = Pattern.compile(emailPattern);
		Matcher matcher = regex.matcher(newUserEmail);
		if (!matcher.find()) {
			Messagebox.show("Wrong email format: only accept @vttek.vn email", "Error", Messagebox.OK,
				Messagebox.ERROR);
			return;
		}

		String phonePattern = "^[0|84]+[0-9]{9}$";
		regex = Pattern.compile(phonePattern);
		matcher = regex.matcher(newUserPhone);
		if (!matcher.find()) {
			Messagebox.show("Wrong phone number format: have 10 digits (0-digit first) or 11 digits (84 first)",
				"Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		String loginNamePattern = "^[a-z0-9]*$";
		regex = Pattern.compile(loginNamePattern);
		matcher = regex.matcher(newUserLoginName);
		if (!matcher.find()) {
			Messagebox.show("The login name must contain only lowercase letters and digits!", "Error", Messagebox.OK,
				Messagebox.ERROR);
			return;
		}

		newUserManagerList = newUserManagerList.trim();

		if (newUserPermission.equals(Constant.USER_PERMISSIONS.get(0)) && newUserManagerList == null) {
			Messagebox.show("Manager field is required for user with one_way permission", "Error", Messagebox.OK,
				Messagebox.ERROR);
			return;
		}

		if (newUserManagerList != null) {
			String listManagerPattern = "^[a-z0-9|,]*$";
			regex = Pattern.compile(listManagerPattern);
			matcher = regex.matcher(newUserManagerList);
			if (!matcher.find()) {
				Messagebox.show(
					"The manager list must contain only lowercase letters and digits, each name seprated by comma (Example: manager1,manager2)!",
					"Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}
		}

		LoginoutDao createDao = new LoginoutDao();

		if (createDao.findUserByUserName(newUserLoginName) != null) {
			Messagebox.show("The login name already exists!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		// execute
		UserInformation newUser = new UserInformation();
		newUser.setLoginName(newUserLoginName);
		newUser.setEmail(newUserEmail);
		newUser.setDepartement(newUserDepart);
		newUser.setFullName(newUserFullName);
		newUser.setPhoneNumber(newUserPhone);
		newUser.setLoginFailCounter(0);
		newUser.setRole(newUserRole.equals("") ? Constant.USER_ROLE.get(1) : newUserRole);
		newUser.setPermission(newUserPermission);
		newUser.setCreatedTime(new Timestamp(System.currentTimeMillis()));
		newUser.setSalt(Utils.encryptSHA256(newUser.getCreatedTime().toString()));

		List<Organization> listManager = new ArrayList<>();
		for (String managerId : newUserManagerList.split(",")) {
			managerId = managerId.trim();
			UserInformation manager = createDao.findUserByUserName(managerId);
			if (manager != null) {
				Organization org = new Organization();
				org.setEmployee(newUser);
				org.setManager(manager);
				listManager.add(org);
			} else {
				Messagebox.show("The manager name " + managerId + " not exists, please re-check!", "Error",
					Messagebox.OK, Messagebox.ERROR);
				return;
			}
		}

		newUser.setListManager(listManager);

		/*
		 * String pass = StringGeneration.generateNewPassword();
		 * newUser.setPassword(Utils.encryptSHA256(newUser.getLoginName() + pass
		 * + newUser.getSalt()));
		 */
		newUser.setCreateBy((UserInformation) Sessions.getCurrent().getAttribute("login"));

		boolean updateRessult = createDao.updateUserInformation(newUser, 1);
		if (updateRessult) {
			String mess = String.format(
				"<b>Your account information</b><br/>Login Name: <b>%s</b><br/>Password: <b>Please use your VDI password</b>",
				newUserLoginName);
			if (new EmailSender().sendEmail(new String[]{newUserEmail},
				Constant.NEW_USER_EMAIL_SUBJECT + newUserLoginName, mess)) {
				Messagebox.show("User successfully created!");
			} else {
				Messagebox.show("User successfully created, but there was a problem while sending email!", "Error",
					Messagebox.OK, Messagebox.ERROR);
			}
		} else {
			Messagebox.show("Cannot create user! Please contact administrator for supported!", "Error", Messagebox.OK,
				Messagebox.ERROR);
		}
		newUserLoginName = newUserFullName = newUserEmail = newUserDepart = newUserPhone = newUserManagerList = "";
	}

	@Command
	@NotifyChange({"gridSubject", "sourceConfig"})
	public void oncheckRadioButton() {
		switch (optionConfig) {
			case "MAIL":
				gridSubject = "Email Server Config";
				sourceConfig = "/layout/admin_layout/email.zul";
				break;
			case "FS":
				gridSubject = "File Server Config";
				sourceConfig = "/layout/admin_layout/file_server.zul";
				break;
			case "VDI":
				gridSubject = "VDI Web-server Config";
				sourceConfig = "/layout/admin_layout/serverconfig.zul";
				break;
			case "PC":
				gridSubject = "Internet Web-server Config";
				sourceConfig = "/layout/admin_layout/serverconfig.zul";
				break;
			case "AUTH":
				gridSubject = "Authenticate";
				sourceConfig = "/layout/admin_layout/authenticate.zul";
				break;
			default:
				break;
		}
	}

	public String getAction(char action) {
		switch (action) {
			case Constant.ACTION_LOG_IN_SUSCESS:
				return "Login Sucess";
			case Constant.ACTION_LOG_IN_FAIL:
				return "Login Fail";
			case Constant.ACTION_LOG_OUT:
				return "Logout";
			case Constant.ACTION_UPLOAD:
				return "Upload";
			case Constant.ACTION_DOWNLOAD:
				return "Download";
			case Constant.ACTION_SEARCH:
				return "Search";
			default:
				return "No action";
		}
	}

	public String getServerType() {
		return Scp.getServerType();
	}
}
