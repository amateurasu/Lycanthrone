package viettel.com.sf.common;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.zkoss.zk.ui.Executions;

public class AppLog {
	private static Logger alarmLog;
	private static Logger perfLog;
	private static Logger cliLog;
	private static Logger secureLog;
	private static Logger remoteLog;
	private static Logger otherLog;
	private static Logger dbLog;

	public static void initialize() {
		if (alarmLog != null) return;

		if (Executions.getCurrent() == null) return;

		String l4j_path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("")
			+ "/WEB-INF/classes/log4j.properties";
		// System.out.println("== log4j_path = " + l4j_path);
		PropertyConfigurator.configure(l4j_path);

		alarmLog = Logger.getLogger("alarmLogger");
		perfLog = Logger.getLogger("perfLogger");
		cliLog = Logger.getLogger("cliLogger");
		secureLog = Logger.getLogger("secureLogger");
		remoteLog = Logger.getLogger("remoteLogger");
		otherLog = Logger.getLogger("otherLogger");
		dbLog = Logger.getLogger("dbLogger");
	}

	public static Logger getAlarmLog() {
		return alarmLog;
	}

	public static Logger getPerfLog() {
		return perfLog;
	}

	public static Logger getCliLog() {
		return cliLog;
	}

	public static Logger getSecureLog() {
		return secureLog;
	}

	public static Logger getRemoteLog() {
		if (remoteLog == null)
			initialize();
		return remoteLog;
	}

	public static Logger getOtherLog() {
		if (otherLog == null)
			initialize();
		return otherLog;
	}

	public static Logger getDbLog() {
		if (dbLog == null)
			initialize();
		return dbLog;
	}
}