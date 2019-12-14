package viettel.com.sf.authenticate;

import lombok.Data;
import viettel.com.sf.common.AES;
import viettel.com.sf.common.AppLog;
import viettel.com.sf.dao.ConfigDao;
import viettel.com.sf.entity.ConfigTable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Data
public class ADAuthenticator {
	private static String domain;
	private static String ldapHost;
	private static String searchBase;

	static {
		readADParame();
	}

	public static void readADParame() {
		ConfigDao configDao = new ConfigDao();
		AES aes = new AES();
		List<ConfigTable> listConfig = configDao.getConfigByCategory("auth");
		if (listConfig != null && listConfig.size() == 3) {
			domain = aes.decrypt(listConfig.get(0).getValue());
			ldapHost = aes.decrypt(listConfig.get(1).getValue());
			searchBase = aes.decrypt(listConfig.get(2).getValue());
		}
	}

	public static boolean authenticate(String user, String pass) {
		String[] returnedAtts = {"sn", "givenName", "mail"};
		String searchFilter = "(&(objectClass=user)(sAMAccountName=" + user + "))";
		// Create the search controls
		SearchControls searchCtls = new SearchControls();
		searchCtls.setReturningAttributes(returnedAtts);
		// Specify the search scope
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		Hashtable<Object, Object> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapHost);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, user + "@" + domain);
		env.put(Context.SECURITY_CREDENTIALS, pass);
		LdapContext ctxGC = null;
		boolean ldapUser = false;

		try {
			ctxGC = new InitialLdapContext(env, null);
			// Search objects in GC using filters
			NamingEnumeration<?> answer = ctxGC.search(searchBase, searchFilter, searchCtls);
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next();
				Attributes attrs = sr.getAttributes();
				if (attrs != null) {
					Map<String, Object> amap = new HashMap<>();
					NamingEnumeration<?> ne = attrs.getAll();
					while (ne.hasMore()) {
						Attribute attr = (Attribute) ne.next();
						amap.put(attr.getID(), attr.get());
						ldapUser = true;
					}
					ne.close();
				}
				return true;
			}
		} catch (NamingException e) {
			// e.printStackTrace();
			AppLog.getOtherLog().error("Authenticate Fail:" + e.getMessage());
			return false;
		} finally {
			try {
				if (ctxGC != null)
					ctxGC.close();
			} catch (NamingException e1) {
				AppLog.getOtherLog().error("Authenticate Fail:" + e1.getMessage());
			}
		}
		return true;
	}
}
