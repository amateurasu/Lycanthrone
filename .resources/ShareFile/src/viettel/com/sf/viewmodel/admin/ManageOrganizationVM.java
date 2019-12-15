package viettel.com.sf.viewmodel.admin;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.Messagebox;
import viettel.com.sf.dao.LoginoutDao;
import viettel.com.sf.entity.Organization;
import viettel.com.sf.entity.UserInformation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageOrganizationVM {
	private String empId, managerId;
	private List<Organization> listOrganizations;

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public List<Organization> getListOrganizations() {
		if (listOrganizations == null) {
			listOrganizations = new LoginoutDao().getAllOrganization();
		}
		return listOrganizations;
	}

	public void setListOrganizations(List<Organization> listOrganizations) {
		this.listOrganizations = listOrganizations;
	}

	@Command
	@NotifyChange({"empId", "managerId", "listOrganizations"})
	public void onClickAddNewOrg() {
		if (empId == null || managerId == null) {
			Messagebox.show("Please fill all textbox", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		empId = empId.trim();
		managerId = managerId.trim();

		String loginNamePattern = "^[a-z0-9]*$";
		Pattern regex = Pattern.compile(loginNamePattern);
		Matcher matcherEmp = regex.matcher(empId);
		Matcher mngId = regex.matcher(managerId);
		if (!matcherEmp.find() || !mngId.find()) {
			Messagebox.show("Only lowercase letters and digits are accepted", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		LoginoutDao loginoutDao = new LoginoutDao();
		UserInformation employee = loginoutDao.findUserByUserName(empId);
		UserInformation manager = loginoutDao.findUserByUserName(managerId);
		if (employee == null || manager == null) {
			Messagebox.show("Employee or manager not exist in database, please check!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		/* check org exist or not*/

		Organization org = new Organization();
		org.setEmployee(employee);
		org.setManager(manager);
		if (loginoutDao.insertNewOrganization(org, 1)) {
			Messagebox.show("Add successfully");
			listOrganizations = null;
		} else
			Messagebox.show("Something error when perform adding new record!", "Error", Messagebox.OK, Messagebox.ERROR);
	}

	@Command
	@NotifyChange("listOrganizations")
	public void onClickRemoveOrg(@BindingParam("org") Organization org) {
		new LoginoutDao().removeOrganization(org);
		listOrganizations = null;
	}
}
