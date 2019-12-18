package org.ayakaji.reverse.thirdparty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class SshUtil {

	private static final Log log = LogFactory.getLog(SshUtil.class);

	/**
	 * The purpose is to establish Ganymed SSH connection with the server. Ensure
	 * that the following parameters of the ssh service are properly configured:
	 * PermitRootLogin yes, PasswordAuthentication yes
	 * 
	 * @param serverIP
	 * @param serverPort
	 * @param account
	 * @param password
	 * @return
	 * @throws IOException
	 */
	public static Connection getConnection(String serverIP, int serverPort, String account, String password)
			throws IOException {
		Connection conn = new Connection(serverIP, serverPort);
		conn.connect();
		log.info("Successfully connected to node " + serverIP + " ...");
		conn.authenticateWithPassword(account, password);
		log.info("Successfully login ...");
		return conn;
	}

	/**
	 * Execute Shell
	 * 
	 * @param conn
	 * @param cmd
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void execCommand(Connection conn, String cmd) throws IOException, InterruptedException {
		Session sess = conn.openSession();
		log.info("[CMD] " + cmd);
		sess.execCommand(cmd);
		printInputStream(sess.getStderr());
		printInputStream(sess.getStdout());
		Thread.sleep(1000);
		sess.close();
	}

	/**
	 * Execute Shell and return echo contents
	 * @param conn
	 * @param cmd
	 * @param stdOut
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String execCommand(Connection conn, String cmd, boolean stdOut)
			throws IOException, InterruptedException {
		Session sess = conn.openSession();
		log.info("[CMD] " + cmd);
		sess.execCommand(cmd);
		String echo = getStringFromStream(stdOut ? sess.getStdout() : sess.getStderr());
		sess.close();
		return echo;
	}

	/**
	 * Print InputStream
	 * 
	 * @param is
	 * @throws IOException
	 */
	public static void printInputStream(InputStream is) throws IOException {
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine()) != null) {
			log.info(line);
		}
	}

	/**
	 * Convert Input Stream to String
	 * @param is
	 * @return
	 */
	private static String getStringFromStream(InputStream is) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line + "/n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * Usage
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		Connection conn = getConnection("0.0.0.0", 0, "root", "root");
		execCommand(conn, "ls");
		conn.close();
	}

}
