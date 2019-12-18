/******************************
 * AgentOne Reverse Utility   *
 * Used For Agent Package     *
 * And thirdparty jar replace *
 *****************************/
package org.ayakaji.reverse.thirdparty;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ReverseUtil {

	private static final Log log = LogFactory.getLog(ReverseUtil.class);
	private static String distAgentPath = "dist/agentone-1.6.jar"; // The local distribute path of agent
	private static String distHttpClientPath = "dist/httpclient-4.5.5-agentone.jar"; // The local distribution path of httpclient jar
	private static String distCatalinaPath = "dist/catalina-agentone.jar";
	private static String agentClassPath = "bin/"; // The path of the agent class files
	private static String serverIP = "10.19.244.184"; // The remote server IP
	private static int serverPort = 8062; // The remote server shell port
	private static String account = "root"; // The shell login account
	private static String password = "Xg6=M-n1"; // The shell login password
	private static String serverTempPath = "/home/dist"; // The jar will be saved in this path on the remote server
	private static String catalinaLibPath = "/usr/local/apache-tomcat-8.5.47/lib"; // Lib path in container
	private static String appLibPath = "/usr/local/apache-tomcat-8.5.47/webapps/esopportal/WEB-INF/lib";
	private static String imageLocation = "10.19.244.184/esopportal:centos_dt_v191028";

	static {
		Properties props = new Properties();
		InputStream is = ReverseUtil.class.getResourceAsStream("/org/ayakaji/reverse/thirdparty/config.properties");
		try {
			props.load(is);
			String value = props.getProperty("distAgentPath");
			if (value != null)
				distAgentPath = value;
			value = props.getProperty("agentClassPath");
			if (value != null)
				agentClassPath = value;
			value = props.getProperty("serverIP");
			if (value != null)
				serverIP = value;
			value = props.getProperty("serverPort");
			if (value != null)
				serverPort = Integer.parseInt(value);
			value = props.getProperty("account");
			if (value != null)
				account = value;
			value = props.getProperty("password");
			if (value != null)
				password = Sm4Util.decrypt(value);
			value = props.getProperty("serverTempPath");
			if (value != null)
				serverTempPath = value;
			value = props.getProperty("catalinaLibPath");
			if (value != null)
				catalinaLibPath = value;
			value = props.getProperty("appLibPath");
			if (value != null)
				appLibPath = value;
			value = props.getProperty("imageLocation");
			if (value != null)
				imageLocation = value;
		} catch (Throwable t) {
			t.printStackTrace();
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
		reverseCloseableHttpClient();
		reverseCoyoteAdapter();
		reverseCatalina();
		pack(distAgentPath, agentClassPath);
		Connection conn = getConnection();
		upload(conn, distAgentPath);
		upload(conn, distCatalinaPath);
		upload(conn, distHttpClientPath);
		deployContainer(conn, distAgentPath, "esopportal", catalinaLibPath);
		deployContainer(conn, distCatalinaPath, "esopportal", catalinaLibPath);
		deployContainer(conn, distHttpClientPath, "esopportal", appLibPath);
		commitImage(conn, "esopportal", imageLocation);
		restartContainer(conn, "esopportal");
		conn.close();
	}

	/**
	 * Create Shell Connection
	 * 
	 * @param ip
	 * @param port
	 * @return
	 * @throws IOException
	 */
	private static Connection getConnection() throws IOException {
		Connection conn = new Connection(serverIP, serverPort);
		conn.connect();
		log.info("Successfully Connected to K8S Node " + serverIP + " ...");
		conn.authenticateWithPassword(account, password);
		log.info("Successfully Login ...");
		return conn;
	}

	/**
	 * Upload File to Remote Node
	 * 
	 * @param conn
	 * @param jarPath
	 * @throws IOException
	 */
	private static void upload(Connection conn, String localAgentPath) throws IOException {
		File file = new File(localAgentPath);
		if (!file.exists()) {
			log.error(localAgentPath + "not exists!");
			System.exit(0);
		}
		SCPClient scp = new SCPClient(conn);
		scp.put(localAgentPath, serverTempPath);
		log.info("Successfully uploaded " + localAgentPath + " ...");
		execCommand(conn, "ls -ltr " + serverTempPath + "/" + file.getName());
		log.info("Local File Size: " + file.length());
	}

	/**
	 * Deploy to docker container
	 * 
	 * @param conn             SHELL Connection
	 * @param localAgentPath
	 * @param containerLocator
	 * @param deployPath       The agent destination path in container
	 * @throws IOException
	 */
	private static void deployContainer(Connection conn, String localAgentPath, String containerLocator,
			String deployPath) throws IOException {
		String agentFileName = new File(localAgentPath).getName();
		execCommand(conn, "/usr/bin/docker cp " + serverTempPath + "/" + agentFileName
				+ " `/usr/bin/docker ps | grep " + containerLocator + " | grep -v pause | awk " + "'{print $1}'`:" + deployPath);
		log.info(localAgentPath + " has been deployed to " + containerLocator + " container's " + deployPath);
	}

	/**
	 * Commit the modified container to image
	 * 
	 * @param conn
	 * @param containerLocator
	 * @param image
	 * @throws IOException
	 */
	private static void commitImage(Connection conn, String containerLocator, String image) throws IOException {
		execCommand(conn, "/usr/bin/docker commit `/usr/bin/docker ps | grep esopportal | grep -v pause | awk "
				+ "'{print $1}'` " + image);
		log.info(containerLocator + " container has been commited to image " + image);
	}

	/**
	 * Kill the container process
	 * 
	 * @param conn
	 * @param containerLocator
	 * @throws IOException
	 */
	private static void restartContainer(Connection conn, String containerLocator) throws IOException {
		execCommand(conn, "/usr/bin/docker kill `/usr/bin/docker ps | grep " + containerLocator
				+ " | grep -v pause | awk '{print $1}'`");
		log.info(containerLocator + " container has been killed");
		log.info("Use this command to see logs : " + "/usr/bin/docker logs -f `/usr/bin/docker ps | grep "
				+ containerLocator + " | grep -v pause | awk '{print $1}'`");
	}

	/**
	 * Execute Shell
	 * 
	 * @param conn
	 * @param cmd
	 * @throws IOException
	 */
	private static void execCommand(Connection conn, String cmd) throws IOException {
		Session sess = conn.openSession();
		log.info("[CMD] " + cmd);
		sess.execCommand(cmd);
		printInputStream(sess.getStderr());
		printInputStream(sess.getStdout());
		sess.close();
	}

	/**
	 * Print InputStream
	 * 
	 * @param is
	 * @throws IOException
	 */
	private static void printInputStream(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		log.info(sb.toString());
	}
	
	/**
	 * @Middleware Tomcat 8.5.47
	 * @jarPath %CATALINA_HOME%/lib/catalina.jar
	 * @description HTTP Inbound Requests Capture
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 * @throws IOException
	 */
	private static void reverseCoyoteAdapter() throws NotFoundException, CannotCompileException, IOException {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get("org.apache.catalina.connector.CoyoteAdapter");
		CtMethod m = cc.getDeclaredMethod("service");
		m.insertAt(343,
				"{ org.ayakaji.reverse.helper.UdpClient.send(org.ayakaji.reverse.thirdparty.CommonInterceptor.getRequestInfo(request)); }");
		m.insertAt(344,
				"{ org.ayakaji.reverse.helper.UdpClient.send(org.ayakaji.reverse.thirdparty.CommonInterceptor.getResponseInfo(response)); }");
		cc.writeFile();
		overwriteClass2Jar(distCatalinaPath, "org.apache.catalina.connector.CoyoteAdapter".replace(".", "\\"));
	}

	/**
	 * @jarPath %CATALINA_HOME%/webapps/esopportal/WEB-INF/lib/httpclient-4.5.5.jar
	 * @description HTTP Outbound Requests Capture
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 * @throws IOException
	 */
	private static void reverseCloseableHttpClient() throws NotFoundException, CannotCompileException, IOException {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get("org.apache.http.impl.client.CloseableHttpClient");
		String s = "public static final java.lang.String getHttpClientInfo(org.apache.http.HttpHost httpHost,\r\n"
				+ "			org.apache.http.HttpRequest httpRequest, org.apache.http.protocol.HttpContext httpContext,\r\n"
				+ "			org.apache.http.client.methods.CloseableHttpResponse httpResponse, long startTimeMillis) {\r\n"
				+ "		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject(new java.util.LinkedHashMap());\r\n"
				+ "		org.joda.time.DateTime dt = new org.joda.time.DateTime($5);\r\n"
				+ "		json.put(\"httpClientStartTime\", dt.toString(\"yyyy-MM-dd hh:mm:ss\"));\r\n"
				+ "		json.put(\"timeLossSeconds\", java.lang.Double.toString((java.lang.System.currentTimeMillis() - $5) / 1000.0));\r\n"
				+ "		if ($1 != null) {\r\n"
				+ "			json.put(\"dstPort\", java.lang.Integer.toString($1.getPort()));\r\n"
				+ "			json.put(\"schemeName\", $1.getSchemeName());\r\n"
				+ "			json.put(\"URI\", $1.toURI());\r\n" + "		}\r\n"
				+ "		if ($2 != null) {\r\n"
				+ "			json.put(\"headers\", $2.getAllHeaders());\r\n"
				+ "			json.put(\"reqLine\", $2.getRequestLine());\r\n"
				+ "			json.put(\"protocolVersion\", $2.getProtocolVersion());\r\n"
				+ "		}\r\n"
				+ "		if ($3 != null) {\r\n"
				+ "			json.put(\"httpContext\", $3.toString());\r\n"
				+ "		}\r\n"
				+ "		if ($4 != null) {\r\n"
				+ "			json.put(\"statusLine\", $4.getStatusLine());\r\n"
				+ "		}\r\n"
				+ "		return com.alibaba.fastjson.JSON.toJSONString(json, true);\r\n"
				+ "	}";
		CtMethod m1 = CtMethod.make(s, cc);
		cc.addMethod(m1);
		CtMethod m = cc.getDeclaredMethod("execute", new CtClass[] { pool.getCtClass("org.apache.http.HttpHost"),
				pool.getCtClass("org.apache.http.HttpRequest") });
		m.setBody("{ \r\n"
				+ "		if ($2 != null) {\r\n"
				+ "			java.lang.String xTag = org.ayakaji.reverse.thirdparty.CommonInterceptor.getXTag();\r\n"
				+ "			java.lang.Integer layer = org.ayakaji.reverse.thirdparty.CommonInterceptor.getLayerNum();\r\n"
				+ "			if (xTag != null)\r\n"
				+ "				$2.setHeader(\"XTag\", xTag);\r\n"
				+ "			if (layer != null)\r\n"
				+ "				$2.setHeader(\"XLayer\", layer.toString());\r\n"
				+ "		}\r\n"
				+ "		long startTimeMillis = java.lang.System.currentTimeMillis();\r\n"
				+ "		org.apache.http.client.methods.CloseableHttpResponse response = doExecute($1, $2, null);\r\n"
				+ "		org.ayakaji.reverse.helper.UdpClient.send(getHttpClientInfo($1, $2, null, response, startTimeMillis));\r\n"
				+ "		return response;\r\n"
				+ " }");
		cc.writeFile();
		overwriteClass2Jar(distHttpClientPath,
				"org.apache.http.impl.client.CloseableHttpClient".replace(".", "\\"));
	}
	
	private static void reverseCatalina() throws NotFoundException, CannotCompileException, IOException {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get("org.apache.catalina.startup.Catalina");
		CtMethod m = cc.getDeclaredMethod("start");
		m.insertAt(705, "{ \r\n"
				+ "		org.ayakaji.reverse.thirdparty.CommonInterceptor.start();\r\n"
				+ " }");
		cc.writeFile();
		overwriteClass2Jar(distCatalinaPath, "org.apache.catalina.startup.Catalina".replace(".", "\\"));
	}

	/**
	 * Overwrite class file to jar
	 * 
	 * @param jarFilePath
	 * @param classFilePath
	 * @throws IOException
	 */
	private static void overwriteClass2Jar(String jarFilePath, String classFilePath) throws IOException {
		String cmd = System.getProperty("java.home") + "\\..\\bin\\jar.exe uvf " + jarFilePath + " " + classFilePath
				+ ".class";
		Process proc = Runtime.getRuntime().exec(new String[] { "cmd", "/c", cmd });
		InputStream in = proc.getInputStream();
		Reader r = new InputStreamReader(in, "gbk");
		BufferedReader br = new BufferedReader(r);
		for (String res = ""; (res = br.readLine()) != null;) {
			log.info(res);
		}
		br.close();
		r.close();
		proc.getOutputStream().close();
	}

	/**
	 * 打包
	 * 
	 * @param jarFilePath
	 * @param basePath
	 * @throws IOException
	 */
	private static void pack(String jarFilePath, String basePath) throws IOException {
		String cmd = System.getProperty("java.home") + "\\..\\bin\\jar.exe cvf " + jarFilePath + " -C " + basePath
				+ " .";
		Process proc = Runtime.getRuntime().exec(new String[] { "cmd", "/c", cmd });
		InputStream in = proc.getInputStream();
		Reader r = new InputStreamReader(in, "gbk");
		BufferedReader br = new BufferedReader(r);
		for (String res = ""; (res = br.readLine()) != null;) {
			log.info(res);
		}
		br.close();
		r.close();
		proc.getOutputStream().close();
	}
}
