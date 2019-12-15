package viettel.com.sf.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.zkoss.zk.ui.Sessions;
import viettel.com.sf.common.AppLog;
import viettel.com.sf.common.Constant;
import viettel.com.sf.common.HibernateUtils;
import viettel.com.sf.entity.ActionHistory;
import viettel.com.sf.entity.FileInfomation;
import viettel.com.sf.entity.UserInformation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FileDao {

	public boolean insertNewFile(FileInfomation fileInfomation, int retry) {
		boolean result = false;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			session.save(fileInfomation);
			session.getTransaction().commit();
			result = true;
		} catch (Exception e) {
			AppLog.getOtherLog().error("insert new file: " + fileInfomation.getFileName() + " - retry: " + retry);
			AppLog.getOtherLog().error(e.getMessage());
			if (session.getTransaction() != null)
				session.getTransaction().rollback();
			if (retry < Constant.MAX_RETRY_DB)
				result = insertNewFile(fileInfomation, retry + 1);
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}
		return result;
	}

	public boolean storeActionUpload(FileInfomation fileInfomation, boolean insertResult, int retry) {
		// TODO Auto-generated method stub
		ActionHistory action = new ActionHistory();
		action.setUser(fileInfomation.getUploader());
		action.setAction(Constant.ACTION_UPLOAD);
		action.setIpAddr((String) Sessions.getCurrent().getAttribute("clientIP"));
		action.setActionTime(fileInfomation.getUpTime());
		action.setFile(fileInfomation);
		action.setDescription(insertResult ? "" : "Insert DB fail");
		return storeActionUpload(action, fileInfomation, insertResult, retry);
	}

	private boolean storeActionUpload(ActionHistory action, FileInfomation fileInfomation, boolean insertResult,
	                                  int retry) {
		boolean result = false;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			session.save(action);
			session.getTransaction().commit();
			result = true;
		} catch (Exception e) {
			AppLog.getOtherLog().error("storeActionUpload: " + fileInfomation.getFileName() + " - retry: " + retry);
			AppLog.getOtherLog().error(e.getMessage());
			if (session.getTransaction() != null)
				session.getTransaction().rollback();
			if (retry < Constant.MAX_RETRY_DB)
				result = storeActionUpload(action, fileInfomation, insertResult, retry + 1);
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}
		return result;
	}

	public boolean storeActionDownload(FileInfomation fileInfomation, int retry) {
		// TODO Auto-generated method stub
		ActionHistory action = new ActionHistory();
		action.setUser((UserInformation) Sessions.getCurrent().getAttribute("login"));
		action.setAction(Constant.ACTION_DOWNLOAD);
		action.setIpAddr((String) Sessions.getCurrent().getAttribute("clientIP"));
		action.setActionTime(new Timestamp(System.currentTimeMillis()));
		action.setFile(fileInfomation);
		// action.setDescription(insertResult? "":"Insert DB fail");
		fileInfomation.setDownloadCounter(fileInfomation.getDownloadCounter() + 1);
		fileInfomation.setLastDownTime(new Timestamp(System.currentTimeMillis()));
		return storeActionDownload(action, fileInfomation, retry);
	}

	private boolean storeActionDownload(ActionHistory action, FileInfomation fileInfomation, int retry) {
		boolean result = false;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();

			/*
			 * StringBuilder sql = new StringBuilder(); sql.append(
			 * "Select f.downloadCounter FileInfomation f where f.fileId = :fileID "
			 * ); Query query = session.createQuery(sql.toString());
			 * query.setParameter("fileID", fileInfomation.getFileId()); int
			 * currentDownloadCount = (int) query.list().get(0);
			 * fileInfomation.setDownloadCounter(currentDownloadCount + 1);
			 */
			session.save(action);
			session.update(fileInfomation);
			session.getTransaction().commit();
			result = true;
		} catch (Exception e) {
			AppLog.getOtherLog().error("storeActionDownload: " + fileInfomation.getFileName() + " - retry: " + retry);
			AppLog.getOtherLog().error(e.getMessage());
			if (session.getTransaction() != null)
				session.getTransaction().rollback();
			if (retry < Constant.MAX_RETRY_DB)
				result = storeActionDownload(action, fileInfomation, retry + 1);
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}
		return result;
	}

	@SuppressWarnings({"unchecked"})
	public List<FileInfomation> getAllUploadFileByUser(UserInformation user) {
		// TODO Auto-generated method stub
		List<FileInfomation> result = new ArrayList<>();

		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			String sql = "Select f from FileInfomation f where f.uploader = :loginName "
				+ "and f.upTime >= :from and (f.available='AVAILABLE' OR f.available is NULL) "
				+ "order by f.upTime desc";
			Query query = session.createQuery(sql);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date datefrom = cal.getTime();
			query.setParameter("from", datefrom);
			query.setParameter("loginName", user);
			result = query.list();
		} catch (Exception e) {
			AppLog.getOtherLog().error(e.getMessage());
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<FileInfomation> searchUploadFile(String fileTextbox, String userTextbox, Date from, Date to) {
		// TODO Auto-generated method stub

		List<FileInfomation> result = new ArrayList<>();
		Session session = HibernateUtils.openNewSession();
		if (((UserInformation) Sessions.getCurrent().getAttribute("login")).getRole().equals("NRM")) {
			userTextbox = ((UserInformation) Sessions.getCurrent().getAttribute("login")).getLoginName();
		}
		try {
			session.beginTransaction();
			StringBuilder sql = new StringBuilder();
			sql.append("Select f from FileInfomation f where 1=1 ");

			if (fileTextbox != null && fileTextbox.trim().length() > 0) {
				fileTextbox = "%" + fileTextbox.trim() + "%";
				sql.append("and LOWER(f.fileName) like :filename ");
			} else {
				fileTextbox = null;
			}

			if (userTextbox != null) {
				sql.append("and LOWER(f.uploader.loginName) like :user ");
			}

			if (from != null) {
				sql.append("and f.upTime >= :from ");
			}
			if (to != null) {
				sql.append("and f.upTime < :to ");
			}
			sql.append(" and (f.available='AVAILABLE' OR f.available is NULL) order by f.upTime desc");

			Query query = session.createQuery(sql.toString());
			if (fileTextbox != null) {
				query.setParameter("filename", fileTextbox.trim().toLowerCase());
			}
			if (userTextbox != null) {
				query.setParameter("user", "%" + userTextbox.trim().toLowerCase() + "%");
			}
			if (from != null) {
				Date datefrom = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(from);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				datefrom = cal.getTime();
				query.setParameter("from", datefrom);
			}
			if (to != null) {
				Date dt = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(to);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cal.add(Calendar.DATE, 1);
				dt = cal.getTime();
				query.setParameter("to", dt);
			}
			result = query.list();
		} catch (Exception e) {
			AppLog.getOtherLog().error(e.getMessage());
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}
		return result;
	}

	public boolean storeActionSearch(String fileTextbox, String userTextbox, Date from, Date to) {
		ActionHistory action = new ActionHistory();
		action.setUser((UserInformation) Sessions.getCurrent().getAttribute("login"));
		action.setAction(Constant.ACTION_SEARCH);
		action.setIpAddr((String) Sessions.getCurrent().getAttribute("clientIP"));
		action.setActionTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		// action.setFile(fileInfomation);
		StringBuilder description = new StringBuilder("Search Infor: ");
		description.append("file name = " + fileTextbox);
		description.append("; user = " + userTextbox);
		description.append("; from = " + from);
		description.append("; to = " + to);
		action.setDescription(description.toString());
		return storeActionSearch(action, 1);
	}

	private boolean storeActionSearch(ActionHistory action, int retry) {
		boolean result = false;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			session.save(action);
			session.getTransaction().commit();
			result = true;
		} catch (Exception e) {
			AppLog.getOtherLog().error("storeActionSearch: " + " - retry: " + retry);
			AppLog.getOtherLog().error(e.getMessage());
			if (session.getTransaction() != null)
				session.getTransaction().rollback();
			if (retry < Constant.MAX_RETRY_DB)
				result = storeActionSearch(action, retry + 1);
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<FileInfomation> getAllPendingFileByUser(UserInformation userLogin) {
		List<FileInfomation> result = new ArrayList<>();

		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			String listUploader = "";
			for (int i = 0; i < userLogin.getListEmployee().size(); i++) {
				listUploader += "'" + userLogin.getListEmployee().get(i).getEmployee().getLoginName() + "'";
				if (i < userLogin.getListEmployee().size() - 1)
					listUploader += ",";
			}
			String sql = "Select f from FileInfomation f where f.uploader in ( " + listUploader
				+ " ) and f.available='PENDING' order by f.upTime desc";
			Query query = session.createQuery(sql);
			result = query.list();
		} catch (Exception e) {
			AppLog.getOtherLog().error(e.getMessage());
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}

		return result;
	}

	public boolean updateFileInfor(FileInfomation file, int retry) {
		boolean result = false;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			session.saveOrUpdate(file);
			session.getTransaction().commit();
			result = true;
		} catch (Exception e) {
			AppLog.getOtherLog().error("Update file exception : " + e.getMessage());
			if (session.getTransaction() != null)
				session.getTransaction().rollback();
			if (retry < Constant.MAX_RETRY_DB)
				return updateFileInfor(file, retry + 1);
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}
		return result;
	}
}
