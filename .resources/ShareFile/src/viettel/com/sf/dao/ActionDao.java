package viettel.com.sf.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import viettel.com.sf.common.AppLog;
import viettel.com.sf.common.Constant;
import viettel.com.sf.common.HibernateUtils;
import viettel.com.sf.entity.ActionHistory;

import java.util.*;

public class ActionDao {
	@SuppressWarnings("unchecked")
	public List<ActionHistory> getAllActionByKey(String action) {
		List<ActionHistory> result = null;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			String sql = "Select c from ActionHistory c where c.actionTime >= :from order by c.actionTime desc";
			Query query = session.createQuery(sql);
			// query.setParameter("category", action);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date datefrom = cal.getTime();
			query.setParameter("from", datefrom);
			result = query.list();
		} catch (Exception e) {
			AppLog.getOtherLog().error("Get ActionHistory: " + action + ": " + e.getMessage());
			// e.printStackTrace();
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

	public List<ActionHistory> searchActionByParam(
		String traceFilename, String traceUser, Date traceFrom, Date traceTo, Set<String> selection
	) {
		List<Character> charSelect = new ArrayList<>();
		if (selection != null) {
			for (String select : selection) {
				switch (select) {
					case "Login Sucess":
						charSelect.add(Constant.ACTION_LOG_IN_SUSCESS);
						break;
					case "Login Fail":
						charSelect.add(Constant.ACTION_LOG_IN_FAIL);
						break;
					case "Logout":
						charSelect.add(Constant.ACTION_LOG_OUT);
						break;
					case "Upload":
						charSelect.add(Constant.ACTION_UPLOAD);
						break;
					case "Download":
						charSelect.add(Constant.ACTION_DOWNLOAD);
						break;
					case "Search":
						charSelect.add(Constant.ACTION_SEARCH);
						break;
				}
			}
		}
		return searchAction(
			traceFilename == null ? null : traceFilename.trim(),
			traceUser == null ? null : traceUser.trim(), traceFrom, traceTo, charSelect);
	}

	@SuppressWarnings("unchecked")
	private List<ActionHistory> searchAction(
		String traceFilename, String traceUser, Date traceFrom, Date traceTo, List<Character> charSelect
	) {
		List<ActionHistory> result = null;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			StringBuilder sql = new StringBuilder();
			sql.append("Select c from ActionHistory c where 1=1 ");

			if (traceFilename != null) {
				traceFilename = "%" + traceFilename + "%";
				sql.append("and LOWER(c.file.fileName) like :filename ");
			}

			if (traceUser != null) {
				sql.append("and LOWER(c.user.loginName) like :user ");
			}

			if (traceFrom != null) {
				sql.append("and c.actionTime >= :from ");
			}
			if (traceTo != null) {
				sql.append("and c.actionTime < :to ");
			}

			if (charSelect.size() > 0) {
				sql.append(" and ( ");
				for (Character chr : charSelect) {
					sql.append("c.action = '").append(chr).append("' OR ");
				}
				sql.append(" 0=1) ");
			}
			sql.append("order by c.actionTime desc");
			Query query = session.createQuery(sql.toString());
			if (traceFilename != null) {
				query.setParameter("filename", traceFilename.toLowerCase());
			}
			if (traceUser != null) {
				query.setParameter("user", "%" + traceUser.toLowerCase() + "%");
			}
			if (traceFrom != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(traceFrom);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Date from = cal.getTime();
				query.setParameter("from", from);
			}
			if (traceTo != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(traceTo);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cal.add(Calendar.DATE, 1);
				Date to = cal.getTime();
				query.setParameter("to", to);
			}
			result = query.list();
		} catch (Exception e) {
			AppLog.getOtherLog().error("searchAction: " + e.getMessage());
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
