package org.ayakaji.reverse.thirdparty;

import java.io.IOException;

import ch.ethz.ssh2.Connection;

public class VBoxUtil {
	
	private static void test() throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection("10.19.195.18", 22, "root", "Xg6=M-n1");
		SshUtil.execCommand(conn, "for i in 1 2 3; do echo \"abcd\"; sleep 1; done");
		conn.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		test();

	}

}
