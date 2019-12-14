package viettel.com.sf.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import viettel.com.sf.common.AppLog;
import viettel.com.sf.common.Constant;
import viettel.com.sf.common.HibernateUtils;
import viettel.com.sf.entity.ActionHistory;
import viettel.com.sf.entity.Organization;
import viettel.com.sf.entity.UserInformation;

import java.sql.Timestamp;
import java.util.List;

public class LoginoutDao {

	@SuppressWarnings("rawtypes")
	public UserInformation findUserByUserName(String username) {
		UserInformation user = null;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			String sql = "Select u from UserInformation u where u.loginName = :loginName";
			Query query = session.createQuery(sql);
			query.setParameter("loginName", username);
			List results = query.list();
			if (results != null && results.size() > 0) {
				user = (UserInformation) results.get(0);
			}
		} catch (Exception e) {
			AppLog.getOtherLog().error("Find User: " + username + " " + e.getMessage());
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}
		return user;
	}

	@SuppressWarnings("rawtypes")
	public boolean havePermitIP(String clientIP) {
		// TODO Auto-generated method stub
		boolean result = false;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			String sql = "Select c.value from ConfigTable c where c.category = 'permit'";
			Query query = session.createQuery(sql);
			List results = query.list();
			if (results != null && results.size() > 0) {
				if (results.get(0).equals("ALL"))
					result = true;
				else {
					String sql2 = "Select ip.ipAddr from IpTable ip where ip.ipAddr = :ipAddr";
					Query query2 = session.createQuery(sql2);
					query.setParameter("ipAddr", clientIP);
					List results2 = query2.list();
					result = results2 != null && results2.size() > 0;
				}
			} else {
				result = true;
			}
		} catch (Exception e) {
			AppLog.getOtherLog().error("Find IP: " + clientIP + " " + e.getMessage());
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

	public boolean updateUserInformation(UserInformation user, int retry) {
		return updateUser(user, retry, "update");
	}

	public boolean deleteUserInformation(UserInformation user, int retry) {
		return updateUser(user, retry, "delete");
	}

	private boolean updateUser(UserInformation user, int retry, String action) {
		boolean result = false;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			switch (action) {
				case "update":
					session.saveOrUpdate(user);
					break;
				case "delete":
					session.delete(user);
					break;
			}
			session.getTransaction().commit();
			result = true;
		} catch (Exception e) {
			AppLog.getOtherLog().error("Update User : " + user.getLoginName() + " " + e.getMessage());
			if (session.getTransaction() != null)
				session.getTransaction().rollback();
			if (retry < Constant.MAX_RETRY_DB)
				return updateUserInformation(user, retry + 1);
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

	public void storeHistoryLogin(UserInformation user, String clientIP, char logInAction, int retry) {
		// TODO Auto-generated method stub
		ActionHistory action = new ActionHistory();
		action.setUser(user);
		action.setAction(logInAction);
		action.setIpAddr(clientIP);
		action.setActionTime(new Timestamp(System.currentTimeMillis()));
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			session.save(action);
			session.getTransaction().commit();
		} catch (Exception e) {
			AppLog.getOtherLog().error("store action : " + user.getLoginName() + " " + e.getMessage());
			if (session.getTransaction() != null)
				session.getTransaction().rollback();
			if (retry < Constant.MAX_RETRY_DB)
				storeHistoryLogin(user, clientIP, logInAction, retry + 1);
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public UserInformation findUserByUserAndEmail(String loginName, String emailAdd) {
		UserInformation user = null;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			String sql = "Select u from UserInformation u where u.loginName = :loginName and u.email = :email";
			Query query = session.createQuery(sql);
			query.setParameter("loginName", loginName);
			query.setParameter("email", emailAdd);
			List results = query.list();
			if (results != null && results.size() > 0) {
				user = (UserInformation) results.get(0);
			}
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
		return user;
	}

	@SuppressWarnings("unchecked")
	public List<UserInformation> findListUserByName(String input) {
		// TODO Auto-generated method stub
		List<UserInformation> results = null;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			String sql = "Select u from UserInformation u where u.loginName like '%" + input + "%' order by u.loginName";
			Query query = session.createQuery(sql);
			results = query.list();
		} catch (Exception e) {
			AppLog.getOtherLog().error(e.getMessage());
			return null;
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}
		return results;
	}

	/* Function for Organization table */
	public boolean insertNewOrganization(Organization org, int retry) {
		boolean result = false;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			session.saveOrUpdate(org);
			session.getTransaction().commit();
			result = true;
		} catch (Exception e) {
			AppLog.getOtherLog().error("Insert new organization " + org.getEmployee().getLoginName() + " - "
				+ org.getManager().getLoginName() + " : " + e.getMessage());
			if (session.getTransaction() != null)
				session.getTransaction().rollback();
			if (retry < Constant.MAX_RETRY_DB)
				return insertNewOrganization(org, retry + 1);
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

	public boolean removeOrganization(Organization org) {
		boolean result = false;
		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			session.delete(org);
			session.getTransaction().commit();
			result = true;
		} catch (Exception e) {
			AppLog.getOtherLog().error("Remove organization " + org.getEmployee().getLoginName() + " - "
				+ org.getManager().getLoginName() + " : " + e.getMessage());
			if (session.getTransaction() != null)
				session.getTransaction().rollback();
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

	public List<Organization> getAllOrganization() {
		// TODO Auto-generated method stub

		Session session = HibernateUtils.openNewSession();
		try {
			session.beginTransaction();
			String sql = "Select u from Organization u order by u.employee.loginName";
			Query query = session.createQuery(sql);
			return query.list();
		} catch (Exception e) {
			AppLog.getOtherLog().error(e.getMessage());
			return null;
		} finally {
			try {
				if (session != null)
					session.close();
			} catch (Exception ex) {
				AppLog.getOtherLog().error(ex.getMessage());
			}
		}
	}

	/*
	 * public LoginoutDao() { UserInformation admin = new UserInformation();
	 * admin.setFullName("I am Admin"); admin.setLoginName("admin");
	 * admin.setLoginFailCounter(0); admin.setDepartement("OSS");
	 * admin.setEmail("admin@viettel"); admin.setRole("ADM");
	 * admin.setPhoneNumber("123456789"); admin.setCreatedTime(new
	 * Timestamp(System.currentTimeMillis()));
	 * admin.setSalt(Utils.encryptSHA256(admin.getCreatedTime().toString()));
	 * admin.setPassword(Utils.encryptSHA256(admin.getLoginName()+ "abc13579"
	 * +admin.getSalt()));
	 *
	 * Session session = HibernateUtils.openNewSession();
	 * session.beginTransaction(); session.save(admin);
	 * session.getTransaction().commit(); session.close();
	 *
	 * }
	 */
}
