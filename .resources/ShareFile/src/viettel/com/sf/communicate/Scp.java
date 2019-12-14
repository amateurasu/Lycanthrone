package viettel.com.sf.communicate;

import com.jcraft.jsch.*;
import org.zkoss.zk.ui.Executions;
import viettel.com.sf.common.AES;
import viettel.com.sf.common.AppLog;
import viettel.com.sf.dao.ConfigDao;
import viettel.com.sf.entity.ConfigTable;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class Scp {
	private static final String FILE_CONFIG_PATH = "config/system.properties";
	private static String host;
	private static String user;
	private static String pass;
	private static int port;
	private static String serverType; // VDI or Pc-internet
	private static int maxDownload;

	static {
		Properties pro = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(
				Executions.getCurrent().getDesktop().getWebApp().getRealPath("/") + FILE_CONFIG_PATH);
			pro.load(fis);
			serverType = pro.getProperty("SERVER_LOCATION");
			// System.out.println("Server type: " + serverType);
			AppLog.getOtherLog().info("Server type: " + serverType);
		} catch (Exception e) {
			// e.printStackTrace();
			AppLog.getOtherLog().error("SCP: Read config server: " + e.getMessage());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception ex) {
					// ex.printStackTrace();
					AppLog.getOtherLog().error("SCP: Read config server: " + ex.getMessage());
				}
			}
		}
		getRemoteServerConfig();
	}

	public static void getRemoteServerConfig() {
		ConfigDao configDao = new ConfigDao();
		AES aes = new AES();
		List<ConfigTable> listConfig = configDao.getConfigByCategory(serverType);
		if (listConfig != null && listConfig.size() == 5) {
			host = aes.decrypt(listConfig.get(0).getValue());
			user = aes.decrypt(listConfig.get(1).getValue());
			pass = aes.decrypt(listConfig.get(2).getValue());
			port = Integer.parseInt(listConfig.get(3).getValue());
			maxDownload = Integer.parseInt(listConfig.get(4).getValue());
			// System.out.println("SCP: Read server config success " + host +
			// ":" + port);
			AppLog.getOtherLog().info("SCP: Read server config success " + host + ":" + port);
		} else {
			// System.out.println("SCP: read server config fail");
			AppLog.getOtherLog().error("SCP: Read server config fail");
			host = user = pass = "";
			port = maxDownload = 0;
		}
	}

	public static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == -1 || b == 0)
			return b;

		if (b == 1 || b == 2) {
			StringBuilder sb = new StringBuilder();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				AppLog.getOtherLog().error("Check ACK 1: " + sb.toString());
			}
			if (b == 2) { // fatal error
				AppLog.getOtherLog().error("Check ACK 2: " + sb.toString());
			}
			sb.delete(0, sb.length());
		}
		return b;
	}

	public static String getServerType() {
		return serverType;
	}

	public static int getMaxDownload() {
		return maxDownload;
	}

	public boolean copyToRemote(String from, String to, String fileName) {
		boolean result = true;
		Session session = null;
		try {
			// createDir(to);
			session = createSession(user, host, port);
			result = copyLocalToRemote(session, from, to, fileName);
		} catch (JSchException e) {
			// e.printStackTrace();
			AppLog.getOtherLog().error("Copy to remote Server: " + e.getMessage());
			result = false;
		} finally {
			if (session != null && session.isConnected())
				session.disconnect();
		}
		return result;
	}

	private boolean copyLocalToRemote(Session session, String from, String to, String fileName) {
		boolean result = true;
		from = from + File.separator + fileName;
		String command = "mkdir -p " + to + "; scp -p -t " + to;
		Channel channel;
		try {
			channel = session.openChannel("exec");
		} catch (JSchException e) {
			// e.printStackTrace();
			AppLog.getOtherLog().error("Copy to remote Server: " + e.getMessage());
			session.disconnect();
			return false;
		}
		((ChannelExec) channel).setCommand(command);

		OutputStream out;
		InputStream in;
		try {
			out = channel.getOutputStream();
			in = channel.getInputStream();
		} catch (IOException e) {
			AppLog.getOtherLog().error("Copy to remote Server: " + e.getMessage());
			// e.printStackTrace();
			session.disconnect();
			return false;
		}

		try {
			channel.connect();
		} catch (JSchException e) {
			AppLog.getOtherLog().error("Copy to remote Server: " + e.getMessage());
			// e.printStackTrace();
			return false;
		}

		if (!getCheckStatus(in)) {
			// System.out.println("ping to remote server failed");
			AppLog.getOtherLog().error("ping to remote server failed");
			channel.disconnect();
			return false;
		}

		if (!createDir(session, to)) {
			// System.out.println("create folder " + to + " failed");
			AppLog.getOtherLog().error("create folder " + to + " failed");
			channel.disconnect();
			return false;
		}

		File _lfile = new File(from);

		command = "T" + (_lfile.lastModified() / 1000) + " 0";
		// The access time should be sent here,
		// but it is not accessible with JavaAPI ;-<
		command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
		try {
			out.write(command.getBytes());
			out.flush();
		} catch (IOException e) {
			AppLog.getOtherLog().error(e.getMessage());
			channel.disconnect();
			return false;
		}
		if (!getCheckStatus(in)) {
			AppLog.getOtherLog().error("send access time failed");
			channel.disconnect();
			return false;
		}

		// send "C0644 filesize filename", where filename should not include '/'
		long filesize = _lfile.length();
		command = "C0644 " + filesize + " " + fileName;
		command += "\n";
		try {
			out.write(command.getBytes());
			out.flush();
		} catch (IOException e1) {
			AppLog.getOtherLog().error("send C0644 failed: " + e1.getMessage());
			channel.disconnect();
			return false;
		}

		if (!getCheckStatus(in)) {
			AppLog.getOtherLog().error("send C0644 return failed");
			channel.disconnect();
			return false;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(from);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len);
			}
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			if (!getCheckStatus(in)) {
				AppLog.getOtherLog().error("Copy file to remote failed");
				channel.disconnect();
				session.disconnect();
				return false;
			}
		} catch (IOException e) {
			AppLog.getOtherLog().error("Copy to remote Server: " + e.getMessage());
			result = false;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					AppLog.getOtherLog().error("Copy to remote Server: close fis: " + e.getMessage());
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					AppLog.getOtherLog().error("Copy to remote Server: close out: " + e.getMessage());
				}
			}
			channel.disconnect();
		}

		return result;
	}

	private Session createSession(String user2, String host2, int port2) throws JSchException {
		JSch jsch = new JSch();
		Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		Session session = jsch.getSession(user, host, port);
		// pw
		session.setPassword(pass);
		session.setConfig(config);
		session.connect();
		return session;
	}

	private boolean getCheckStatus(InputStream in) {
		try {
			int checkinResult = checkAck(in);
			return checkinResult == 0;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public boolean removeFile(String filePath, String fileName) {
		boolean result = true;
		Session session = null;
		Channel channel = null;
		try {
			String command = "rm -rf " + filePath + fileName;
			session = createSession(user, host, port);
			channel = session.openChannel("exec");
			channel.connect();
			((ChannelExec) channel).setCommand(command);
			// channel.run();
		} catch (JSchException e) {
			AppLog.getOtherLog().error("Remove file from remote Server: " + e.getMessage());
			result = false;
		} finally {
			if (channel != null && channel.isConnected())
				channel.disconnect();
			if (session != null && session.isConnected())
				session.disconnect();
		}
		return result;
	}

	private boolean createDir(Session session, String path) {
		Channel channel;
		try {
			channel = session.openChannel("exec");
		} catch (JSchException e) {
			AppLog.getOtherLog().error("Copy to remote Server: " + e.getMessage());
			return false;
		}
		String command = "mkdir -p " + path;
		((ChannelExec) channel).setCommand(command);

		try {
			channel.connect();
		} catch (JSchException e) {
			AppLog.getOtherLog().error("Copy to remote Server: " + e.getMessage());
			return false;
		}
		channel.disconnect();
		return true;
	}
}
