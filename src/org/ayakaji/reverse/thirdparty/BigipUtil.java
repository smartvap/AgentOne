package org.ayakaji.reverse.thirdparty;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ch.ethz.ssh2.Connection;

@SuppressWarnings("unused")
public class BigipUtil {
	private static final Log log = LogFactory.getLog(BigipUtil.class);
	private static String serverIP = "10.19.194.134"; // The remote server IP
	private static int serverPort = 8032; // The remote server shell port
	private static String account = "root"; // The shell login account
	private static String password = "Xg6=M-n1"; // The shell login password

	public static void main(String[] args) throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection(serverIP, serverPort, account, password);
		addRecordA(conn, "dcos-conductor.sd.chinamobile.com", "134.80.184.23");
		conn.close();
	}

	/**
	 * Add A type records
	 * 
	 * @param conn
	 * @param wideIP
	 * @param destIP
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void addRecordA(Connection conn, String wideIP, String destIP)
			throws IOException, InterruptedException {
		SshUtil.execCommand(conn, "tmsh modify gtm server dns_server virtual-servers add { vs_" + wideIP
				+ " { destination " + destIP + ":0 monitor gateway_icmp } }");
		SshUtil.execCommand(conn,
				"tmsh create gtm pool a pool_" + wideIP + " members replace-all-with { dns_server:vs_" + wideIP + " }");
		SshUtil.execCommand(conn, "tmsh create gtm wideip a " + wideIP
				+ " pool-lb-mode round-robin pools replace-all-with { pool_" + wideIP + " }");
	}
}
