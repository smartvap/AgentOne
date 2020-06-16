package org.ayakaji.reverse.thirdparty;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.ethz.ssh2.Connection;

@SuppressWarnings("unused")
public class IngressUemUpdate {

	private static final Log log = LogFactory.getLog(IngressUemUpdate.class);

	private static String K8s_Master_IP = "134.80.209.64";
	private static int K8s_Master_Port = 22;
	private static String K8s_Master_Acct = "root";
	private static String K8s_Master_Pass = "Jnydzycsyy@123";
	private static Connection conn = null; // 全局连接
	private static String K8s_Master_Trans_Folder = "/home/heqiming"; // Temporary Transmit Folder
	private static String Uem_Err_Src_Full_Path = "D:\\workspaces\\uem\\uem-err-1.5.js"; // 业务异常截获脚本
	private static String Uem_JS_Err_Full_Path = "D:\\workspaces\\uem\\uem-jserr-1.5.js"; // 截获js错误的脚本
	private static String Uem_Err_Filename = "uem-err-1.5.js"; // 源文件实际文件名
	private static String Uem_JS_Err_Filename = "uem-jserr-1.5.js"; // 截获js错误的脚本
	private static String Uem_Err_Ctnr_Path = "/usr/local/nginx/html/uem/uem-err-1.5.js"; // 容器中的路径
	private static String Uem_JS_Err_Ctnr_Path = "/usr/local/nginx/html/uem/uem-jserr-1.5.js"; // 容器中的路径
	private static String Json2_Src_Full_Path = "D:\\workspaces\\uem\\json2.js"; // 原始路径
	private static String Json2_Filename = "json2.js"; // 源文件实际文件名
	private static String Json2_Ctnr_Path = "/usr/local/nginx/html/uem/json2.js"; // 容器中的路径
	private static String Fourxx_Zip_Full_path = "D:\\workspaces\\uem\\4xx\\4xx.zip"; // 4xx压缩包
	private static String Fourxx_Zip_Filename = "4xx.zip";
	private static String Fourxx_Ctnr_Path = "/usr/local/nginx/html/uem/4xx.zip"; // 容器中的路径
	private static String Ngx_Ing_Conf_Full_Path = "D:\\workspaces\\uem\\nginx-ingress-devel.conf"; // Nginx配置文件本地路径
	private static String Ngx_Ing_Conf_Filename = "nginx-ingress-devel.conf"; // 配置文件实际文件名
	private static String Ngx_Ing_Conf_Ctnr_Path = "/etc/nginx/nginx.conf"; // 容器中Nginx配置文件路径
	private static String Ctnr_Resource_Home = "/usr/local/nginx/html/uem"; // 静态资源目录
	private static String K8s_Ingress_Tenant = "ns8650d12a171403d6c66001"; // 租户ID，namespace
	private static String kubectl = "/var/paas/kubernetes/kubectl "
			+ "--client-certificate=/var/paas/srv/kubernetes/server.cer "
			+ "--client-key=/var/paas/srv/kubernetes/server_key.pem "
			+ "--certificate-authority=/var/paas/srv/kubernetes/ca.crt ";
	private static String Get_Ngx_Pod_Name = kubectl + "get pod -n " + K8s_Ingress_Tenant
			+ " | grep nginx-ingress | head -1 | awk '{print $1}'";
	private static String Ngx_Pod_Name = null;
	private static String Mrk_Ngx_Uem = "UEM 1.5 Configurations"; // UEM是否已注入Nginx的标志物
	private static String Ngx_Uem_Cnf =
			"\n\t\t##########################\n"
			+ "\t\t# UEM 1.5 Configurations #\n"
			+ "\t\t# Begin                  #\n"
			+ "\t\t##########################\n"
			+ "\t\t\n"
			+ "\t\t# Receive Uploaded UEM Data\n"
			+ "\t\tlocation /uem/uem-1.5.do {\n"
			+ "\t\t\tdefault_type text/html;\n"
			+ "\t\t\treturn 200 'UEM uploaded successfully!';\n"
			+ "\t\t}\n"
			+ "\t\t\n"
			+ "\t\t# Main Injected Agents\n"
			+ "\t\t# 4xx, 5xx Pages\n"
			+ "\t\tlocation /uem/ {\n"
			+ "\t\t\troot html;\n"
			+ "\t\t}\n"
			+ "\t\t\n"
			+ "\t\t# Inject Agents in each page\n"
			+ "\t\tsub_filter_types *;\n"
			+ "\t\tsub_filter \"</body>\" \"<script type='text/javascript' src='/uem/uem-err-1.5.js'></script></body>\";\n"
			+ "\t\tsub_filter \"<head>\" \"<head><script type='text/javascript' src='/uem/uem-jserr-1.5.js'></script>\";\n"
			+ "\t\tsub_filter_once off;\n"
			+ "\t\t\n"
			+ "\t\t##########################\n"
			+ "\t\t# UEM 1.5 Configurations #\n"
			+ "\t\t# End                    #\n"
			+ "\t\t##########################\n";
	private static String Mrk_Err_Page = "error_page 404 /uem/4xx.html;";

	static {
		try {
			conn = SshUtil.getConnection(K8s_Master_IP, K8s_Master_Port, K8s_Master_Acct, K8s_Master_Pass);
			Ngx_Pod_Name = SshUtil.execCommand(conn, Get_Ngx_Pod_Name, true).replace("\n", "");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	// 复制容器内Nginx Ingress配置文件到临时目录的命令
	private static String Pull_Ngx_Ing_Conf = kubectl + "cp " + K8s_Ingress_Tenant + "/" + Ngx_Pod_Name + ":nginx.conf "
			+ K8s_Master_Trans_Folder;
	// 在容器内创建UEM静态资源目录
	private static String Create_Uem_Folder = kubectl + "exec -i " + Ngx_Pod_Name + " -n " + K8s_Ingress_Tenant
			+ " -- mkdir -p " + Ctnr_Resource_Home;
	// 推送Nginx Ingress配置文件到容器内
	private static String Push_Ngx_Ing_Conf = kubectl + "cp " + K8s_Master_Trans_Folder + "/" + Ngx_Ing_Conf_Filename
			+ " " + K8s_Ingress_Tenant + "/" + Ngx_Pod_Name + ":" + Ngx_Ing_Conf_Ctnr_Path;
	private static String Push_Uem_Err = kubectl + "cp " + K8s_Master_Trans_Folder + "/" + Uem_Err_Filename + " "
			+ K8s_Ingress_Tenant + "/" + Ngx_Pod_Name + ":" + Uem_Err_Ctnr_Path;
	private static String Push_Js_Err = kubectl + "cp " + K8s_Master_Trans_Folder + "/" + Uem_JS_Err_Filename + " "
			+ K8s_Ingress_Tenant + "/" + Ngx_Pod_Name + ":" + Uem_JS_Err_Ctnr_Path;
	private static String Push_Json2 = kubectl + "cp " + K8s_Master_Trans_Folder + "/" + Json2_Filename + " "
			+ K8s_Ingress_Tenant + "/" + Ngx_Pod_Name + ":" + Json2_Ctnr_Path;
	private static String Push_4xx = kubectl + "cp " + K8s_Master_Trans_Folder + "/" + Fourxx_Zip_Filename + " "
			+ K8s_Ingress_Tenant + "/" + Ngx_Pod_Name + ":" + Fourxx_Ctnr_Path;
	// 解压缩
	private static String Unzip_4xx = kubectl + "exec -i " + Ngx_Pod_Name + " -n " + K8s_Ingress_Tenant + " -- unzip "
			+ Fourxx_Ctnr_Path + " -d " + Ctnr_Resource_Home + " -o";
	// 设置文件属性644
	private static String Chmod_Uem = kubectl + "exec -i " + Ngx_Pod_Name + " -n " + K8s_Ingress_Tenant
			+ " -- chmod -R 755 " + Ctnr_Resource_Home;
	// 验证Nginx配置
	private static String Ngx_Ing_Cnf_Verify = kubectl + "exec -i " + Ngx_Pod_Name + " -n " + K8s_Ingress_Tenant
			+ " -- /sbin/nginx -t";
	private static String Ngx_Ing_Cnf_Reload = kubectl + "exec -i " + Ngx_Pod_Name + " -n " + K8s_Ingress_Tenant
			+ " -- /sbin/nginx -s reload";

	public static void main(String[] args) throws IOException, InterruptedException {
//		deploy();
//		deployNgxConf();
		deployUEM();
//		deploy4xxPages();
//		deployJSErr();
	}

	private static void deploy() throws IOException, InterruptedException {
		// 1、从ingress容器中拷贝nginx.conf到Master节点的中转目录
		SshUtil.execCommand(conn, Pull_Ngx_Ing_Conf);
		// 2、从Master节点下载配置文件到工程开发目录
		SshUtil.download(conn, K8s_Master_Trans_Folder + "/nginx.conf", Ngx_Ing_Conf_Full_Path);
		// 3、读取最新的配置文件到字符串
		String sNgxCnf = read(Ngx_Ing_Conf_Full_Path);
		// 4、检查UEM标志，若不存在，需要做配置合并
		if (sNgxCnf.indexOf(Mrk_Ngx_Uem) == -1) {
			// 找到第一个业务Location上端最近的换行符处为最佳写入位置
			int pos = sNgxCnf.substring(0, sNgxCnf.indexOf("location /systemAdminMgr {")).lastIndexOf("\n");
			// 合并配置文件
			merge(Ngx_Ing_Conf_Full_Path, pos, Ngx_Uem_Cnf);
		}
		// 5、检查404配置
		sNgxCnf = read(Ngx_Ing_Conf_Full_Path);
		if (sNgxCnf.indexOf(Mrk_Err_Page) == -1) {
			int pos = sNgxCnf.indexOf("error_page 404 = @custom_upstream-default-backend_404;");
			merge(Ngx_Ing_Conf_Full_Path, pos, "#");
			pos = sNgxCnf.substring(0, sNgxCnf.indexOf("error_page 404 = @custom_upstream-default-backend_404;"))
					.lastIndexOf("\n");
			merge(Ngx_Ing_Conf_Full_Path, pos, Mrk_Err_Page);
		}
		// 6、上传合并后的配置文件到Master节点中转目录
		SshUtil.upload(conn, Ngx_Ing_Conf_Full_Path, K8s_Master_Trans_Folder);
		// 7、上传UEM代理到Master节点中转目录
		SshUtil.upload(conn, Uem_Err_Src_Full_Path, K8s_Master_Trans_Folder);
		SshUtil.upload(conn, Uem_JS_Err_Full_Path, K8s_Master_Trans_Folder);
		// 8、上传json2依赖到Master节点中转目录
		SshUtil.upload(conn, Json2_Src_Full_Path, K8s_Master_Trans_Folder);
		// 9、上传默认4xx.html到Master节点中转目录
		SshUtil.upload(conn, Fourxx_Zip_Full_path, K8s_Master_Trans_Folder);
		// 10、拷贝Nginx Ingress配置文件到容器内
		SshUtil.execCommand(conn, Push_Ngx_Ing_Conf);
		// 11、无条件创建UEM静态资源目录
		SshUtil.execCommand(conn, Create_Uem_Folder);
		// 12、推送UEM脚本到容器
		SshUtil.execCommand(conn, Push_Uem_Err);
		// 13、推送JS_ERR脚本到容器
		SshUtil.execCommand(conn, Push_Js_Err);
		// 14、推送Json2脚本到容器
		SshUtil.execCommand(conn, Push_Json2);
		// 15、推送4xx.zip到容器
		SshUtil.execCommand(conn, Push_4xx);
		// 16、解压缩4xx.zip
		SshUtil.execCommand(conn, Unzip_4xx);
		// 17、设置属性755
		SshUtil.execCommand(conn, Chmod_Uem);
		// 18、验证Nginx配置
		SshUtil.execCommand(conn, Ngx_Ing_Cnf_Verify);
		// 19、重新加载Nginx配置
		SshUtil.execCommand(conn, Ngx_Ing_Cnf_Reload);
		// 20、关闭Connection
		conn.close();
	}

	/**
	 * 部署Nginx Ingress配置文件
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void deployNgxConf() throws IOException, InterruptedException {
		SshUtil.upload(conn, Ngx_Ing_Conf_Full_Path, K8s_Master_Trans_Folder);
		SshUtil.execCommand(conn, Push_Ngx_Ing_Conf);
		SshUtil.execCommand(conn, Ngx_Ing_Cnf_Verify);
		SshUtil.execCommand(conn, Ngx_Ing_Cnf_Reload);
		conn.close();
	}

	/**
	 * Deploy uem-err-1.5.js
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void deployUEM() throws IOException, InterruptedException {
		SshUtil.upload(conn, Uem_Err_Src_Full_Path, K8s_Master_Trans_Folder);
		SshUtil.execCommand(conn, Create_Uem_Folder);
		SshUtil.execCommand(conn, Push_Uem_Err);
		SshUtil.execCommand(conn, Chmod_Uem);
		conn.close();
	}
	
	/**
	 * Deploy uem-jserr-1.5.js
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void deployJSErr() throws IOException, InterruptedException {
		SshUtil.upload(conn, Uem_JS_Err_Full_Path, K8s_Master_Trans_Folder);
		SshUtil.execCommand(conn, Create_Uem_Folder);
		SshUtil.execCommand(conn, Push_Js_Err);
		SshUtil.execCommand(conn, Chmod_Uem);
		conn.close();
	}

	private static void deploy4xxPages() throws IOException, InterruptedException {
		SshUtil.upload(conn, Fourxx_Zip_Full_path, K8s_Master_Trans_Folder);
		SshUtil.execCommand(conn, Create_Uem_Folder);
		SshUtil.execCommand(conn, Push_4xx);
		SshUtil.execCommand(conn, Unzip_4xx);
		SshUtil.execCommand(conn, Chmod_Uem);
		conn.close();
	}

	/**
	 * 合并配置文件
	 * 
	 * @param src
	 * @param pos
	 * @param cont
	 * @throws IOException
	 */
	public static void merge(String src, int pos, String cont) throws IOException {
		File f = new File(src);
		if (!(f.exists() && f.isFile())) {
			log.error("文件不存在或不合法!");
			return;
		}
		if ((pos < 0) || (pos > f.length())) {
			log.error("插入位置不合法!");
			return;
		}

		// 创建临时文件
		File tmp = File.createTempFile("ngx_ingr_cnf", ".temp", new File("d:/"));
		FileOutputStream fos = new FileOutputStream(tmp);
		FileInputStream fis = new FileInputStream(tmp);
		tmp.deleteOnExit();

		// 随机访问，定位到相应位置
		RandomAccessFile rw = new RandomAccessFile(f, "rw");
		rw.seek(pos);

		int i = -1;
		// 将position位置后的内容写入临时文件
		while ((i = rw.read()) != -1) {
			fos.write(i);
		}
		// 将追加内容cont写入pos位置
		rw.seek(pos);
		rw.write(cont.getBytes());

		// 将临时文件写回文件，并将创建的流关闭
		while ((i = fis.read()) != -1) {
			rw.write(i);
		}
		rw.close();
		fos.close();
		fis.close();
		log.info("配置文件合并完成！");
	}

	/**
	 * 将文本文件读取到字符串
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private static String read(String filePath) throws IOException {
		File f = new File(filePath);
		if (!f.exists() && !f.isFile()) {
			log.error("源文件不存在或不合法");
			return null;
		}
		FileInputStream fis = new FileInputStream(f);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int len = -1;
		while ((len = fis.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		baos.close();
		fis.close();
		return baos.toString();
	}

	/**
	 * Copy Files
	 * 
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void copy(File src, File dst) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		input = new FileInputStream(src);
		output = new FileOutputStream(dst);
		byte[] buf = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buf)) > 0) {
			output.write(buf, 0, bytesRead);
		}
		input.close();
		output.close();
	}

}
