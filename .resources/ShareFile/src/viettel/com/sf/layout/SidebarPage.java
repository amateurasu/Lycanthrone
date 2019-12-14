package viettel.com.sf.layout;

public class SidebarPage {
	private String uri;
	private String name;

	public SidebarPage(String uri, String name) {
		super();
		this.uri = uri;
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return "/image/menu/" + this.name + ".ico";
	}
}
