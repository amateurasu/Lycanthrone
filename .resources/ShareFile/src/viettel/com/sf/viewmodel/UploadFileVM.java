package viettel.com.sf.viewmodel;

import lombok.*;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Messagebox;
import viettel.com.sf.common.AppLog;
import viettel.com.sf.common.Constant;
import viettel.com.sf.common.FileServerConfig;
import viettel.com.sf.common.TikaFileTypeDetector;
import viettel.com.sf.communicate.EmailSender;
import viettel.com.sf.communicate.Scp;
import viettel.com.sf.dao.FileDao;
import viettel.com.sf.entity.FileInfomation;
import viettel.com.sf.entity.Organization;
import viettel.com.sf.entity.UserInformation;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class UploadFileVM {
	Media[] media;
	private List<FileInfomation> listFile = new ArrayList<>();

	@Init
	public void init() {

	}

	@Command
	@NotifyChange("listFile")
	public void preUpload(@ContextParam(ContextType.BIND_CONTEXT) BindContext ctx) {
		boolean duplicateFile = false;
		Event upEvent = ctx.getTriggerEvent();
		if (upEvent == null) return;

		if ((upEvent instanceof UploadEvent)) {
			media = ((UploadEvent) upEvent).getMedias();
		} else {
			return;
		}

		if (media == null) {
			Messagebox.show("Please select file to upload", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		for (Media item : media) {
			String filetype = new TikaFileTypeDetector().probeContentType(item);
			if (filetype == null || (!filetype.equals("application/x-tika-ooxml") // docx, xlsx, pptx
				&& !filetype.equals("application/x-tika-msoffice") // ppt, doc
				&& !filetype.equals("application/pdf") // pdf
				&& !filetype.equals("application/vnd.ms-excel"))) { // xls

				Messagebox.show(
					"Your file type is not supported. Support files: doc, docx, ppt, pptx, pdf, xlsx, xls!",
					"Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}
		}

		for (Media value : media) {
			if (value.getByteData().length <= 0) {
				Messagebox.show("Empty files are not allowed!", "Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}
		}

		for (Media value : media) {
			boolean isExist = false;
			for (FileInfomation file : listFile) {
				if (value.getName().equals(file.getFileName()) && value.getFormat().equals(file.getFileType())
					&& (Math.round(value.getByteData().length / (1024.0) * 100) / 100.0) == file
					.getFileSize()) {
					isExist = true;
					break;
				}
			}
			if (isExist) {
				duplicateFile = true;
				continue;
			}

			FileInfomation fileInfomation = new FileInfomation();
			fileInfomation.setMedia(value);
			byte[] uploadedfile = value.getByteData();
			fileInfomation.setFileSize(uploadedfile.length / (1024.0));
			fileInfomation.setFileName(value.getName());
			fileInfomation.setFileType(value.getFormat());
			UserInformation user = ((UserInformation) Sessions.getCurrent().getAttribute("login"));
			fileInfomation.setPath(
				File.separator + user.getDepartement() + File.separator + user.getLoginName() + File.separator);
			listFile.add(fileInfomation);
		}

		if (duplicateFile) {
			Messagebox.show("This file already exists! Please choose another one!",
				"Warning", Messagebox.OK, Messagebox.EXCLAMATION);
		}
	}

	@Command
	@NotifyChange("listFile")
	public void doUpload() {
		/* check user permission first */
		UserInformation userLogin = ((UserInformation) Sessions.getCurrent().getAttribute("login"));

		if (listFile.size() == 0) {
			Messagebox.show("Please choose your file(s) first!", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		if (listFile.size() > 20) {
			Messagebox.show("You can not upload more than 20 files", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		double totalsize = 0;
		for (FileInfomation fileInfomation : listFile) {
			totalsize += fileInfomation.getFileSize();
			if (totalsize >= (15 * 1024)) {
				Messagebox.show("You can not upload more than 15MB", "Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}
		}
		int i;
		String uploadStatus = null;
		FileDao dao = new FileDao();
		for (i = 0; i < listFile.size(); i++) {
			uploadStatus = "Success";
			InputStream inputStream = null;
			OutputStream outputStream = null;
			File fileUpload;
			FileInfomation fileExe = listFile.get(i);
			if (!Constant.USER_PERMISSIONS.get(1).equals(userLogin.getPermission()) && Scp.getServerType().equals("VDI")) {
				/*
				 * User have only permission submit from PC->VDI, so when submit
				 * VDI->PC need boss's appprove
				 */
				fileExe.setAvailable(Constant.PENDING);
			}
			try {
				long startTime = System.currentTimeMillis();
				boolean insertResult;
				fileExe.setUpTime(new Timestamp(startTime));

				/* copy file from pc to server */
				inputStream = new ByteArrayInputStream(fileExe.getMedia().getByteData());

				String fileName = fileExe.getUpTime().toString().replace(':', '.').concat("_") + fileExe.getFileName();
				String filePath = FileServerConfig.getFileServerRootPath() + fileExe.getPath();
				// String filePathTest = "Desktop/test_upload";
				fileUpload = new File(filePath + fileName);
				fileUpload.getParentFile().mkdirs();

				outputStream = new FileOutputStream(fileUpload);
				byte[] buffer = new byte[1024];
				for (int count; (count = inputStream.read(buffer)) != -1; ) {
					outputStream.write(buffer, 0, count);
				}

				/* copy file to remote server */

				Scp scp = new Scp();
				if (scp.copyToRemote(filePath, filePath, fileName)) {
					long stopTime = System.currentTimeMillis();
					long elapsedTime = stopTime - startTime;

					fileExe.setUploader(userLogin);
					fileExe.setUpDuration(elapsedTime);

					if (dao.insertNewFile(fileExe, 1)) {
						insertResult = true;
						if (!dao.storeActionUpload(fileExe, insertResult, 1)) {
							if (fileUpload != null && fileUpload.exists())
								fileUpload.delete();
							uploadStatus = " Fail to save action";
						}
					} else {
						insertResult = false;
						uploadStatus = " Fail to insert file information";
						if (fileUpload != null && fileUpload.exists())
							fileUpload.deleteOnExit();
						if (!scp.removeFile(filePath, fileName)) {
							uploadStatus = " Fail to insert file information and remove file in remote server";
						}
					}
				} else {
					if (fileUpload != null && fileUpload.exists())
						fileUpload.delete();
					uploadStatus = " Fail to transfer to remote";
				}
			} catch (FileNotFoundException e) {
				AppLog.getOtherLog().error("UPLOAD ERROR: " + e.getMessage());
				uploadStatus = "FileNotFoundException";
			} catch (IOException e) {
				uploadStatus = "IOException";
				AppLog.getOtherLog().error("UPLOAD ERROR: " + e.getMessage());
			} finally {
				if (outputStream != null) {
					try {
						outputStream.flush();
					} catch (IOException e1) {
						AppLog.getOtherLog().error("UPLOAD ERROR: " + e1.getMessage());
					}

					try {
						outputStream.close();
					} catch (IOException e) {
						AppLog.getOtherLog().error("UPLOAD ERROR: " + e.getMessage());
					}
				}
				if (inputStream != null)
					try {
						inputStream.close();
					} catch (IOException e) {
						AppLog.getOtherLog().error("UPLOAD ERROR: " + e.getMessage());
					}
			}
			if (!uploadStatus.equals("Success")) {
				break;
			}
		}
		if (i == listFile.size()) {
			if (!Constant.USER_PERMISSIONS.get(1).equals(userLogin.getPermission())
				&& Scp.getServerType().equals("VDI")) {
				/* Send email for User boss */

				EmailSender sender = new EmailSender();
				if (sender.checkEmailConfig()) {
					Messagebox.show("Error: Email config have problem!", "Error", Messagebox.OK, Messagebox.ERROR);
				}

				StringBuilder tableFile = new StringBuilder("<table><tr><th>File name</th><th>File size</th></tr>");//header

				for (FileInfomation file : listFile) {
					tableFile.append("<tr><td>")
						.append(file.getFileName()).append(".").append(file.getFileType()).append("</td>")
						.append("<td>").append(file.getFileSize()).append("</td></tr>");
				}
				tableFile.append("</table>");
				String mess = String.format(
					"<b>Your employee: <b>%s</b> uploaded some files into system. <br/><a>Please login and give your decision!</a><br/><br/>" + tableFile,
					userLogin.getLoginName());
				List<String> listEmail = new ArrayList<>();
				for (Organization mng : userLogin.getListManager()) {
					listEmail.add(mng.getManager().getEmail());
				}
				// if (sender.sendEmail(listEmail.toArray(new String[0]), Constant.EMAIL_APPROVE_SUBJECT, mess))
				Messagebox.show("Your file(s) uploaded, please ask your manager to approve!");
				// else{Messagebox.show("Your file(s) uploaded, but something error when send email to your manager");}
			} else {
				Messagebox.show("File(s) uploaded successfully");
			}
			listFile.clear();
		} else {
			Messagebox.show(
				"There was a problem uploading file: " + uploadStatus + "\n" + listFile.get(i).getFileName(),
				"Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("listFile")
	public void removeFile(@BindingParam("file") FileInfomation file) {
		listFile.remove(file);
	}

	@Command
	@NotifyChange("listFile")
	public void doReset() {
		listFile.clear();
	}
}
