/********************************
 * AgentOne Helper              *
 * UDP Client                   *
 * Compiled By: jdk1.6.0_45_x64 *
 * Author: Hugh                 *
 ********************************/
package org.ayakaji.reverse.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ayakaji.reverse.thirdparty.ReverseUtil;

public class UdpClient {
	private static final Log log = LogFactory.getLog(UdpClient.class);
	private static String receiverAddress = "10.19.249.28";
	private static int receiverPort = 9516;

	static {
		Properties props = new Properties();
		InputStream is = ReverseUtil.class.getResourceAsStream("/org/ayakaji/reverse/thirdparty/config.properties");
		try {
			props.load(is);
			String value = props.getProperty("receiverAddress");
			if (value != null)
				receiverAddress = value;
			value = props.getProperty("receiverPort");
			if (value != null)
				receiverPort = Integer.parseInt(value);
		} catch (Throwable t) {
			t.printStackTrace();
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void send(String json) {
		if (json == null || json.equals(""))
			return;
		DatagramSocket clntSock = null;
		try {
			clntSock = new DatagramSocket();
		} catch (SocketException e) {
			log.fatal(e.getMessage());
		}
		if (clntSock == null)
			return;
		InetAddress ip = null;
		try {
			ip = InetAddress.getByName(receiverAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (ip == null)
			return;
		DatagramPacket sendPacket = new DatagramPacket(json.getBytes(), json.getBytes().length, ip, receiverPort);
		try {
			clntSock.send(sendPacket);
			clntSock.close();
		} catch (IOException e) {
			log.fatal(e.getMessage());
			clntSock.close();
		} finally {
			if (!clntSock.isClosed())
				clntSock.close();
		}
	}

	public static void main(String[] args) throws IOException {
	}

}