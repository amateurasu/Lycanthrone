package viettel.com.sf.viewmodel;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Filedownload;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;
import viettel.com.sf.common.AppLog;
import viettel.com.sf.common.Constant;
import viettel.com.sf.common.FileServerConfig;
import viettel.com.sf.communicate.Scp;
import viettel.com.sf.dao.FileDao;
import viettel.com.sf.entity.FileInfomation;
import viettel.com.sf.entity.UserInformation;

import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadVM {
	int fileDownloadCounter = 0;
	private List<FileInfomation> listFileDownload;

	private String userTextbox, fileTextbox;
	private Date from = new Date(), to = new Date();

	public String getUserTextbox() {
		return userTextbox;
	}

	public void setUserTextbox(String userTextbox) {
		this.userTextbox = userTextbox;
	}

	public String getFileTextbox() {
		return fileTextbox;
	}

	public void setFileTextbox(String fileTextbox) {
		this.fileTextbox = fileTextbox;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public List<FileInfomation> getListFileDownload() {

		return listFileDownload;
	}

	public void setListFileDownload(List<FileInfomation> listFileDownload) {
		this.listFileDownload = listFileDownload;
	}

	public int getFileDownloadCounter() {
		return fileDownloadCounter;
	}

	public void setFileDownloadCounter(int fileDownloadCounter) {
		this.fileDownloadCounter = fileDownloadCounter;
	}

	@Command
	@NotifyChange({"fileDownloadCounter"})
	public void checkFoDownload(@BindingParam("file") FileInfomation file) {
		if (file.isSelected())
			fileDownloadCounter++;
		else
			fileDownloadCounter--;
	}

	@Command
	@NotifyChange({"listFileDownload", "fileDownloadCounter"})
	public void doDownload() {
		UserInformation userLogin = ((UserInformation) Sessions.getCurrent().getAttribute("login"));
		if (!Constant.USER_PERMISSIONS.get(1).equals(userLogin.getPermission()) && Scp.getServerType().equals("PC")) {
			Messagebox.show("You dont have permission to download file to your PC, please contact adminstrator for more information!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		if (fileDownloadCounter <= 0) {
			return;
		} else if (fileDownloadCounter > 10) {
			Messagebox.show("You cannot download more than 10 files at once", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		double fileSize = 0;
		for (FileInfomation file : listFileDownload) {
			if (file.isSelected()) {
				fileSize += file.getFileSize();
			}
		}

		if (fileSize > (Scp.getMaxDownload() * 1024)) {
			Messagebox.show("You cannot download more than " + Scp.getMaxDownload() + "MB at once", "Error",
				Messagebox.OK, Messagebox.ERROR);
			return;
		}

		boolean upadteAction = true;
		boolean downloadAction = true;
		HashMap<String, Integer> mapFileName = new HashMap<>();
		UserInformation user = ((UserInformation) Sessions.getCurrent().getAttribute("login"));
		String pathTo = FileServerConfig.getFileServerRootPath() + File.separator + user.getDepartement()
			+ File.separator + user.getLoginName() + File.separator;
		FileOutputStream fos = null;
		try {
			File downloadFile = new File(pathTo + "file_download.zip");
			downloadFile.getParentFile().mkdirs();
			downloadFile.createNewFile();
			fos = new FileOutputStream(downloadFile, false);
		} catch (FileNotFoundException e1) {
			AppLog.getOtherLog().error("DOWNLOAD ERROR: " + e1.getMessage());
			return;
		} catch (IOException e1) {
			AppLog.getOtherLog().error("DOWNLOAD ERROR: " + e1.getMessage());
			return;
		}
		FileInputStream fin = null;
		ZipOutputStream _zos = new ZipOutputStream(fos);
		FileDao downloadDao = new FileDao();
		byte[] buffer = new byte[1024];
		for (int i = 0; i < listFileDownload.size(); i++) {
			if (listFileDownload.get(i).isSelected()) {
				try {
					int index = 0;
					if (mapFileName.containsKey(listFileDownload.get(i).getFileName())) {
						index = mapFileName.get(listFileDownload.get(i).getFileName());
					} else {
						mapFileName.put(listFileDownload.get(i).getFileName(), 0);
					}

					mapFileName.replace(listFileDownload.get(i).getFileName(), index + 1);

					ZipEntry ze = new ZipEntry(listFileDownload.get(i).getFileName().substring(0,
						listFileDownload.get(i).getFileName().lastIndexOf("."))
						+ (index == 0 ? "" : "(" + index + ")") + listFileDownload.get(i).getFileName()
						.substring(listFileDownload.get(i).getFileName().lastIndexOf(".")));
					_zos.putNextEntry(ze);
					fin = new FileInputStream(
						FileServerConfig.getFileServerRootPath() + listFileDownload.get(i).getPath()
							+ listFileDownload.get(i).getUpTime().toString().replace(':', '.').concat("_")
							+ listFileDownload.get(i).getFileName());
					int len = 0;
					while ((len = fin.read(buffer)) > 0) {
						_zos.write(buffer, 0, len);
					}
				} catch (Exception e) {
					AppLog.getOtherLog().error("DOWNLOAD ERROR: " + e.getMessage());
				} finally {
					if (fin != null) {
						try {
							fin.close();
						} catch (IOException e) {
							AppLog.getOtherLog().error("DOWNLOAD ERROR: " + e.getMessage());
						}
					}
					fin = null;
				}
			}
		}
		try {
			_zos.closeEntry();
		} catch (IOException e1) {
			AppLog.getOtherLog().error("DOWNLOAD ERROR: " + e1.getMessage());
		}

		try {
			_zos.close();
		} catch (IOException e1) {
			AppLog.getOtherLog().error("DOWNLOAD ERROR: " + e1.getMessage());
		}
		try {
			fos.close();
		} catch (IOException e1) {
			AppLog.getOtherLog().error("DOWNLOAD ERROR: " + e1.getMessage());
		}

		// download
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(pathTo + "file_download.zip");
			Filedownload.save(inStream, "application/zip", "file_download.zip");
		} catch (Exception e) {
			Messagebox.show("There was an error downloading your file! Please try again later!", "Error", Messagebox.OK,
				Messagebox.ERROR);
			downloadAction = false;
			AppLog.getOtherLog().error("DOWNLOAD ERROR: " + e.getMessage(), e);
		} finally {
			/*
			 * if (inStream != null) { try { inStream.close(); } catch
			 * (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); AppLog.getOtherLog().error(
			 * "DOWNLOAD ERROR: " + e.getMessage()); } }
			 */
		}
		// save action
		if (downloadAction) {
			for (FileInfomation file : listFileDownload) {
				if (file.isSelected()) {
					if (!downloadDao.storeActionDownload(file, 1)) {
						upadteAction = false;
						break;
					}
				}
			}
		}
		fileDownloadCounter = 0;
		for (int i = 0; i < listFileDownload.size(); i++) {
			listFileDownload.get(i).setSelected(false);
		}

		if (!upadteAction) {
			AppLog.getOtherLog().error("Your file have downloaded but error when save action ");
		} else {
			// Messagebox.show("Download started!");
		}
	}

	@Command
	@NotifyChange({"listFileDownload"})
	public void doDownloadFile(@BindingParam("param") FileInfomation file) {
		FileDao downloadDao = new FileDao();
		UserInformation userLogin = ((UserInformation) Sessions.getCurrent().getAttribute("login"));
		if (!Constant.USER_PERMISSIONS.get(1).equals(userLogin.getPermission()) && Scp.getServerType().equals("PC")) {
			Messagebox.show("You dont have permission to download file to your PC, please contact adminstrator for more information!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		String pathTo = FileServerConfig.getFileServerRootPath() + file.getPath();
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(
				pathTo + file.getUpTime().toString().replace(':', '.').concat("_") + file.getFileName());
			// inStream = new FileInputStream("D:\\" + "Tài liệu tiếng
			// việt.docx");
			Filedownload.save(inStream, "application/zip;", URLEncoder.encode(file.getFileName(), "UTF-8"));
		} catch (Exception e) {
			Messagebox.show("Ooops! Got exception when downloading file ", "Error", Messagebox.OK, Messagebox.ERROR);
			AppLog.getOtherLog().error("DOWNLOAD ERROR: " + e.getMessage());
			return;
		}

		if (!downloadDao.storeActionDownload(file, 1)) {
			AppLog.getOtherLog().error("Your file have downloaded but error when save action ");
		} else {
			// Messagebox.show("Download started!");
		}
	}

	@Init
	@NotifyChange({"listFileDownload", "fileDownloadCounter"})
	public void refresh() {
		FileDao downloadDao = new FileDao();
		listFileDownload = downloadDao
			.getAllUploadFileByUser((UserInformation) Sessions.getCurrent().getAttribute("login"));
	}

	/* Search */
	@Command
	@NotifyChange({"listFileDownload", "fileDownloadCounter", "fileTextbox"})
	public void clickSearchButton() {
		FileDao searchDao = new FileDao();
		// validate input
		if (from == null) {
			Messagebox.show("Please enter FromDate!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		if (to == null) {
			Messagebox.show("Please enter ToDate!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		if (from != null && to != null && to.before(from)) {
			Messagebox.show("From Date should be less than (or equals) To Date", "Error", Messagebox.OK,
				Messagebox.ERROR);
			return;
		}
		if (fileTextbox != null) {
			// fileTextbox = fileTextbox.replace("%", "").replace("'",
			// "").replace("*", "").replace("\\", "")
			// .replace("/", "").replace(":", "").replace("?", "").replace("<",
			// "").replace(">", "")
			// .replace("|", "");
			fileTextbox = fileTextbox.replace("*", "").replace("\\", "").replace("/", "").replace(":", "")
				.replace("?", "").replace("<", "").replace(">", "").replace("|", "");
		}
		// exe
		listFileDownload.clear();
		listFileDownload = searchDao.searchUploadFile(fileTextbox, userTextbox, from, to);
		fileDownloadCounter = 0;

		// save action
		searchDao.storeActionSearch(fileTextbox, userTextbox, from, to);
	}
}
