package viettel.com.sf.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import viettel.com.sf.dao.LoginoutDao;
import viettel.com.sf.entity.UserInformation;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateUserInfoController extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long serialVersionUID = -5684087730946964531L;

	@Wire
	private Textbox email, phone, depart;

	@Listen("onClick=#updateUserButton")
	public void onClickUpdateButton() {
		boolean haveChange = false;
		UserInformation user = (UserInformation) Sessions.getCurrent().getAttribute("login");
		if (!email.getValue().trim().equals("") && !email.getValue().trim().equals(user.getEmail())) {
			String emailPattern = "^[A-Za-z0-9]{3,16}@vttek.vn$";
			Pattern regex = Pattern.compile(emailPattern);
			Matcher matcher = regex.matcher(email.getValue().trim());
			if (matcher.find()) {
				user.setEmail(email.getValue().trim());
				haveChange = true;
			} else {
				Messagebox.show("Invalid email format: only accept @vttek.vn email", "Error", Messagebox.OK,
					Messagebox.ERROR);
				return;
			}
		}

		if (!phone.getValue().trim().equals("") && !phone.getValue().trim().equals(user.getPhoneNumber())) {
			String phonePattern = "^[0|84][0-9]{9}$";
			Pattern regex = Pattern.compile(phonePattern);
			Matcher matcher = regex.matcher(phone.getValue().trim());
			if (matcher.find()) {
				user.setPhoneNumber(phone.getValue().trim());
				haveChange = true;
			} else {
				Messagebox.show("Phone number wrong: have 10 digits (0-digit first) or 11 digits (84 first)", "Error",
					Messagebox.OK, Messagebox.ERROR);
				return;
			}
		}

		if (!depart.getValue().trim().equals("") && !depart.getValue().trim().equals(user.getDepartement())) {
			user.setDepartement(depart.getValue().trim());
			haveChange = true;
		}

		if (haveChange) {
			// save user
			user.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
			LoginoutDao updateDao = new LoginoutDao();
			if (updateDao.updateUserInformation(user, 1)) {
				Sessions.getCurrent().setAttribute("login", user);
				Messagebox.show("User information successfully updated!");
			} else {
				Messagebox.show("Cannot update user information. Please try again later!", "Error", Messagebox.OK, Messagebox.ERROR);
			}
		} else {
			Messagebox.show("Nothing changed!");
		}
		email.setValue(user.getEmail());
		phone.setValue(user.getPhoneNumber());
		depart.setValue(user.getDepartement());
	}
}
