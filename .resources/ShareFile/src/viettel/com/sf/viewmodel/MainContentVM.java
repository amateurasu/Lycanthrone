package viettel.com.sf.viewmodel;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import viettel.com.sf.layout.SidebarPage;

public class MainContentVM {
	private String includeSrc = "/layout/download.zul";

	@GlobalCommand("onClickSideBar")
	@NotifyChange("includeSrc")
	public void onClickSideBar(@BindingParam("param") SidebarPage page) {
		includeSrc = page.getUri();
	}

	public String getIncludeSrc() {
		return includeSrc;
	}
}