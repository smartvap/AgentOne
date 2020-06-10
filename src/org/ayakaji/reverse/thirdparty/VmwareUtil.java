/***********************************
 * VMware Workstation 15 Utilities *
 * Based on 15.5.1                 *
 **********************************/
package org.ayakaji.reverse.thirdparty;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

@SuppressWarnings("unused")
public class VmwareUtil {
	private static final Log log = LogFactory.getLog(VmwareUtil.class);
	private static final String serverIP = "10.19.240.33";
	private static final int serverPort = 22;
	private static final String account = "root";
	private static final String password = "onDH/47dg";
	private static final String baiduPCSPackage = "BaiduPCS-Go-v3.6.1-linux-amd64.zip";
	private static final String baiduPCSExePath = "BaiduPCS-Go-v3.6.1-linux-amd64/BaiduPCS-Go";
	private static final String baiduPCSSoftLink = "/usr/bin/BaiduPCS-Go";
	private static final String localBaiduPCSPath = "dist";
	private static final String remoteBaiduPCSPath = "/usr/local";

	/**
	 * Initialize Baidu Cloud Disk Client
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void initBaiduPCS() throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection(serverIP, serverPort, account, password);
		SCPClient client = new SCPClient(conn);
//		client.put(localBaiduPCSPath + "/" + baiduPCSPackage, remoteBaiduPCSPath);
		SshUtil.execCommand(conn, "cd " + remoteBaiduPCSPath + "; unzip " + baiduPCSPackage);
		SshUtil.execCommand(conn, "ln -s " + remoteBaiduPCSPath + "/" + baiduPCSExePath + " " + baiduPCSSoftLink);
		conn.close();
	}

	/**
	 * Upload file to Baidu Cloud Disk
	 * 
	 * @param remoteFilePath
	 * @param baiduPath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void uploadBaidu(String remoteFilePath, String baiduPath) throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection(serverIP, serverPort, account, password);
		String echo = SshUtil.execCommand(conn, "BaiduPCS-Go loglist | grep -v \"用户名\" | grep -v \"^$\"", true);
		if (echo != null && echo.equals("")) {
			log.error("Please login to a baidu cloud account first!");
			return;
		}
		SshUtil.execCommand(conn, "BaiduPCS-Go upload " + remoteFilePath + " " + baiduPath + " -p 50 --norapid");
		conn.close();
	}

	/**
	 * Changing File or Package Formats. Support ovf to ova, ova to ovf, vmx to ovf,
	 * vmx to ova, ova to vmx, ovf to vmx.
	 * 
	 * @param vmxPath
	 * @param ovfPath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void ovftool(String srcPath, String dstPath) throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection(serverIP, serverPort, account, password);
		SshUtil.execCommand(conn, "ovftool " + srcPath + " " + dstPath);
		conn.close();
	}

	private static void hotMigrate(String srcVm, String dstVm) {

	}

	public static void main(String[] args) throws IOException, InterruptedException {
		uploadBaidu("/home/virtual/win7pro_credit/win7pro_credit.ova", "/Projects");
//		ovftool("/home/virtual/win7pro_credit/win7pro_credit.vmx", "/home/virtual/win7pro_credit/win7pro_credit.ova");
	}

}
