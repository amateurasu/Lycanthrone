package viettel.com.sf.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import viettel.com.sf.common.AppLog;
import viettel.com.sf.common.Constant;
import viettel.com.sf.common.HibernateUtils;
import viettel.com.sf.entity.ConfigTable;

import java.util.List;

public class ConfigDao {

	/*
	 * @SuppressWarnings("unchecked") public List<String> getEmailConfig() { //
	 * TODO Auto-generated method stub List<String> result = null; Session
	 * session = HibernateUtils.openNewSession(); try {
	 * session.beginTransaction(); String sql =
	 * "Select c.value from ConfigTable c where c.category = 'email' order by id"
	 * ; Query query = session.createQuery(sql); result = query.list(); } catch
	 * (Exception e) { e.printStackTrace(); } finally { session.close(); }
	 * return result; }
	 *
	 * @SuppressWarnings("unchecked") public List<String> getFileServerConfig()
	 * { // TODO Auto-generated method stub List<String> result = null; Session
	 * session = HibernateUtils.openNewSession(); try {
	 * session.beginTransaction(); String sql =
	 * "Select c.value from ConfigTable c where c.category = 'fileserver' order by id"
	 * ; Query query = session.createQuery(sql); result = query.list(); } catch
	 * (Exception e) { e.printStackTrace(); } finally { session.close(); }
	 * return result; }
	 */

	@SuppressWarnings("unchecked")
	public List<ConfigTable> getConfigByCategory(String category) {
		List<ConfigTable> result = null;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			String sql = "Select c from ConfigTable c where c.category = :category order by id";
			Query query = session.createQuery(sql);
			query.setParameter("category", category);
			result = query.list();
		} catch (Exception e) {
			AppLog.getOtherLog().error("Get config_table: " + category + ": " + e.getMessage());
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

	public boolean updateConfig(List<ConfigTable> listConfig, int retry) {
		// TODO Auto-generated method stub
		boolean result = false;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			for (ConfigTable config : listConfig) {
				session.update(config);
			}
			session.getTransaction().commit();
			result = true;
		} catch (Exception e) {
			//e.printStackTrace();
			session.getTransaction().rollback();
			AppLog.getOtherLog().error("update configtable email fail: " + ": " + e.getMessage());
			if (retry < Constant.MAX_RETRY_DB)
				return updateConfig(listConfig, retry + 1);
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
