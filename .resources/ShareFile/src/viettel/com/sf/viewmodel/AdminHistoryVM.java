package viettel.com.sf.viewmodel;

import lombok.*;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import viettel.com.sf.common.Constant;
import viettel.com.sf.dao.ActionDao;
import viettel.com.sf.entity.ActionHistory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class AdminHistoryVM {

	public static final char ACTION_LOG_IN_SUSCESS = '0';
	public static final char ACTION_LOG_IN_FAIL = '1';
	public static final char ACTION_LOG_OUT = '2';
	public static final char ACTION_UPLOAD = '3';
	public static final char ACTION_DOWNLOAD = '4';

	private String traceFilename, traceUser;
	private ListModelList<String> listTraceAction = new ListModelList<>(Collections.unmodifiableCollection(
		Arrays.asList("Login Sucess", "Login Fail", "Logout", "Upload", "Download", "Search")));
	private Date traceFrom = new Date();
	private Date traceTo = new Date();
	private List<ActionHistory> listAction = getAllListAction();

	@Command
	@NotifyChange("listAction")
	public List<ActionHistory> getAllListAction() {
		// TODO Auto-generated method stub
		ActionDao actDao = new ActionDao();
		return actDao.getAllActionByKey("");
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

	@Command("clickSearchTraceButton")
	@NotifyChange("listAction")
	public void clickSearchTraceButton() {
		if (traceFrom == null) {
			Messagebox.show("Please enter FromDate!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		if (traceTo == null) {
			Messagebox.show("Please enter ToDate!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		if (traceTo.before(traceFrom)) {
			Messagebox.show("From Date should be less than (or equals) To Date", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		ActionDao actDao = new ActionDao();
		listAction = actDao.searchActionByParam(traceFilename, traceUser, traceFrom, traceTo, listTraceAction.getSelection());
	}
}
