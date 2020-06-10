/****************************************
 * This source file will not be used!!! *
 ****************************************/
package org.ayakaji.reverse.thirdparty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

@SuppressWarnings("unused")
public class UpgradeUtil {
	private static final Log log = LogFactory.getLog(UpgradeUtil.class);

	private static final LinkedHashMap<String, Object> iCrmSrv = new LinkedHashMap<String, Object>() {
		private static final long serialVersionUID = 4764131368467349434L;
		{
			put("srvAddr", "10.17.249.118");
			put("srvPort", 22);
			put("account", "root");
			put("password", "Bo<i>Ni0Oq>l");
			put("appHome", "/was7/WebSphere/AppServer/profiles/Server1/installedApps/c4w01Cell01");
		}
	};

	private static final LinkedHashMap<String, Object> crm3Srv = new LinkedHashMap<String, Object>() {
		private static final long serialVersionUID = 7179556005853834437L;
		{
			put("srvAddr", "10.19.203.177");
			put("srvPort", 8022);
			put("account", "root");
			put("password", "Nj$u(Do4Pr)b");
			put("appHome", "/was7/WebSphere/AppServer/profiles/Server1/installedApps/c5whal301Cell01");
		}
	};

	private static final LinkedHashMap<String, Object> marketSrv = new LinkedHashMap<String, Object>() {
		private static final long serialVersionUID = 4774826460129337024L;
		{
			put("srvAddr", "10.19.203.50");
			put("srvPort", 22);
			put("account", "root");
			put("password", "Bt$y<Ln5Ng_v");
			put("appHome", "/was7/WebSphere/AppServer/profiles/Server1/installedApps/sw2bCell02");
		}
	};

	private static final LinkedHashMap<String, Object> esopSrv = new LinkedHashMap<String, Object>() {
		private static final long serialVersionUID = -7631217586310361711L;
		{
			put("srvAddr", "10.19.244.144");
			put("srvPort", 22);
			put("account", "root");
			put("password", "Kw!i(Dz0Oe)b");
			put("appHome", "/was7/WebSphere/AppServer/profiles/Server1/installedApps/tesop01Cell01");
		}
	};

	private static final LinkedHashMap<String, Object> shmSrv = new LinkedHashMap<String, Object>() {
		private static final long serialVersionUID = -5254774176048788993L;
		{
			put("srvAddr", "10.19.203.177");
			put("srvPort", 8042);
			put("account", "root");
			put("password", "Iv(l$Pr9Ag)u");
			put("appHome", "/crmhome/shmprm");
			put("compress",
					"cd /crmhome/shmprm; tar -czf shmprm.tar.gz brfshm.ini load.sh load-brfshm.sh dbc_loader dbc_adminx dbc_loaderx BrfKernel64.a dbc_server brfshm dbc_admin dbcache dbc_conf.ini");
		}
	};

	/**
	 * tpmaster deployment, 192.195.0.40
	 */
	private static final LinkedHashMap<String, Object> tpMasSrv = new LinkedHashMap<String, Object>() {
		private static final long serialVersionUID = -5254774176048788993L;
		{
			put("srvAddr", "10.19.203.177");
			put("srvPort", 8032);
			put("account", "root");
			put("password", "Fx(q<Rk0Ie>q");
			put("appHome",
					new String[] { "/crmhome/tpmaster/app/bin", "/crmhome/tpmaster/app/lib",
							"/crmhome/tpmaster/tpcloud/bin", "/crmhome/tpmaster/tpcloud/lib",
							"/crmhome/tpmaster/tpcloud/include" });
		}
	};

	/**
	 * tpworker deployment, 192.195.0.58
	 */
	private static final LinkedHashMap<String, Object> tpWrkSrv = new LinkedHashMap<String, Object>() {
		private static final long serialVersionUID = 7157619320514819085L;
		{
			put("srvAddr", "10.19.203.177");
			put("srvPort", 8042);
			put("account", "root");
			put("password", "Iv(l$Pr9Ag)u");
			put("appHome",
					new String[] { "/crmhome/tpworker/app/lib", "/crmhome/tpworker/app/bin",
							"/crmhome/tpworker/tpcloud/bin", "/crmhome/tpworker/tpcloud/lib",
							"/crmhome/tpworker/tpcloud/include" });
			put("shmHome",
					new String[] { "/crmhome/shmprm/brfshm.ini", "/crmhome/shmprm/dbc_server",
							"/crmhome/shmprm/dbc_loaderx", "/crmhome/shmprm/BrfKernel64.a", "/crmhome/shmprm/brfshm",
							"/crmhome/shmprm/dbc_admin", "/crmhome/shmprm/dbcache", "/crmhome/shmprm/dbc_loader",
							"/crmhome/shmprm/dbc_adminx", "/crmhome/shmprm/dbc_conf.ini" });
		}
	};

	private static final LinkedHashMap<String, Object> spSrv = new LinkedHashMap<String, Object>() {
		private static final long serialVersionUID = 7157619320514819085L;
		{
			put("srvAddr", "10.19.202.19");
			put("srvPort", 22);
			put("account", "root");
			put("password", "Bs(s!Tw8Wj>m");
			put("appHome", "/was7/WebSphere/AppServer/profiles/Server1/installedApps/sisp1bCell02");
		}
	};

	private static final LinkedHashMap<String, Object> isoSrv = new LinkedHashMap<String, Object>() {
		private static final long serialVersionUID = 7105803141988101482L;
		{
			put("srvAddr", "10.19.244.184");
			put("srvPort", 8022);
			put("account", "root");
			put("password", "iG59$jsrA");
			put("appHome", "/was7/WebSphere/AppServer/profiles/Server1/installedApps/sles11Cell01");
		}
	};

	private static final List<LinkedHashMap<String, Object>> ears = new ArrayList<LinkedHashMap<String, Object>>() {
		private static final long serialVersionUID = 4764131368467349434L;
		{
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 8487518707157734534L;
				{
					put("earName", "workflowmgr.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -1368917123423375536L;
				{
					put("earName", "uniteview2.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 1264377336232813805L;
				{
					put("earName", "unisettle.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -7454052001073612971L;
				{
					put("earName", "unimkt.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -979907219177063474L;
				{
					put("earName", "systemAdminMgr.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 8731761231612862509L;
				{
					put("earName", "report2.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 2102683017915843895L;
				{
					put("earName", "report.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 7298132447174537326L;
				{
					put("earName", "piecesettle.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 4856144563577501314L;
				{
					put("earName", "orderMgr.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 7646377784079601658L;
				{
					put("earName", "openchannel.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -5075997097201775293L;
				{
					put("earName", "ngsysadmin.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 5419325892069614443L;
				{
					put("earName", "ngportal.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -5247804029212887930L;
				{
					put("earName", "ngpdf.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 7496454719169779977L;
				{
					put("earName", "ngorder.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -609279548923535854L;
				{
					put("earName", "nginventory.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -4709436188566306516L;
				{
					put("earName", "ngcustcare.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -7439341673596156583L;
				{
					put("earName", "inif.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 5729611584939951231L;
				{
					put("earName", "custsvc.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 5604982213302334817L;
				{
					put("earName", "custcare.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -3028608860385918440L;
				{
					put("earName", "csp.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -6068655604599287843L;
				{
					put("earName", "cmop.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 8279465402955538567L;
				{
					put("earName", "charge.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 7646532562975062133L;
				{
					put("earName", "channel.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 5837810239272002046L;
				{
					put("earName", "Inventory.ear");
					put("srvInfo", iCrmSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 9210606723815281366L;
				{
					put("earName", "ui-custsvc.ear");
					put("srvInfo", crm3Srv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -9021818020279987L;
				{
					put("earName", "ngmarket.ear");
					put("srvInfo", marketSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -6294857445268227737L;
				{
					put("earName", "interface.ear");
					put("srvInfo", spSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = -3363515892022337204L;
				{
					put("earName", "ngesop.ear");
					put("srvInfo", esopSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 7802547239168638674L;
				{
					put("earName", "esop.ear");
					put("srvInfo", esopSrv);
				}
			});
			add(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 8923023016768149594L;
				{
					put("earName", "shmprm");
					put("srvInfo", shmSrv);
				}
			});
		}
	};

	/**
	 * Find the EAR installation location
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static LinkedHashMap<String, Object> getEarInstLoc(String earName) {
		for (LinkedHashMap<String, Object> ear : ears) {
			if (ear.get("earName").equals(earName)) {
				return (LinkedHashMap<String, Object>) ear.get("srvInfo");
			}
		}
		return null;
	}

	/**
	 * Clear original backup
	 * 
	 * @param earName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void clearBackup(String earName) throws IOException, InterruptedException {
		LinkedHashMap<String, Object> srvInfo = getEarInstLoc(earName);
		Connection conn = SshUtil.getConnection((String) srvInfo.get("srvAddr"), (Integer) srvInfo.get("srvPort"),
				(String) srvInfo.get("account"), (String) srvInfo.get("password"));
		SshUtil.execCommand(conn, "cd " + (String) srvInfo.get("appHome") + "; if [ -f " + earName
				+ ".tar.gz ]; then rm " + earName + ".tar.gz; fi");
		conn.close();
		log.info(earName + ".tar.gz has been cleaned up");
	}

	/**
	 * Compress The ear folder, support Linux & AIX
	 * 
	 * @param earName
	 * @throws IOException
	 * @throws InterruptedException
	 * @author heqiming
	 */
	private static void compress(String earName) throws IOException, InterruptedException {
		LinkedHashMap<String, Object> srvInfo = getEarInstLoc(earName);
		Connection conn = SshUtil.getConnection((String) srvInfo.get("srvAddr"), (Integer) srvInfo.get("srvPort"),
				(String) srvInfo.get("account"), (String) srvInfo.get("password"));
		if (srvInfo.get("compress") != null) {
			SshUtil.execCommand(conn, (String) srvInfo.get("compress"));
		} else {
			SshUtil.execCommand(conn,
					"cd " + (String) srvInfo.get("appHome") + "; if [ `uname -s` = 'Linux' ]; then tar -czf " + earName
							+ ".tar.gz " + earName + "; elif [ `uname -s` = 'AIX' ]; then tar -cf " + earName + ".tar "
							+ earName + "; gzip " + earName + ".tar; fi");
		}
		conn.close();
		log.info(earName + " has been compressed to " + earName + ".tar.gz");
	}

	/**
	 * Download
	 * 
	 * @param earName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void download(String earName) throws IOException, InterruptedException {
		LinkedHashMap<String, Object> srvInfo = getEarInstLoc(earName);
		Connection conn = SshUtil.getConnection((String) srvInfo.get("srvAddr"), (Integer) srvInfo.get("srvPort"),
				(String) srvInfo.get("account"), (String) srvInfo.get("password"));
		log.info("Remote file size: ");
		SshUtil.execCommand(conn,
				"du -s -b " + (String) srvInfo.get("appHome") + "/" + earName + ".tar.gz | awk '{print $1}'");
		SCPClient client = new SCPClient(conn);
		client.get(srvInfo.get("appHome") + "/" + earName + ".tar.gz");
		log.info("Local file size: " + new File("dist/" + earName + ".tar.gz").length());
		conn.close();
		log.info(srvInfo.get("srvAddr") + ":" + srvInfo.get("appHome") + "/" + earName
				+ ".tar.gz has been downloaded to dist/");
	}

	/**
	 * Upload the package to isolated environment
	 * 
	 * @param earName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void upload(String earName) throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection((String) isoSrv.get("srvAddr"), (Integer) isoSrv.get("srvPort"),
				(String) isoSrv.get("account"), (String) isoSrv.get("password"));
		SCPClient client = new SCPClient(conn);
//		client.put("dist/" + earName + ".tar.gz", (String) isoSrv.get("appHome"));
		log.info("Local file size: " + new File("dist/" + earName + ".tar.gz").length());
		log.info("Remote file size: ");
		SshUtil.execCommand(conn,
				"du -s -b " + (String) isoSrv.get("appHome") + "/" + earName + ".tar.gz | awk '{print $1}'");
		conn.close();
		log.info("dist/" + earName + ".tar.gz has been uploaded to " + isoSrv.get("srvAddr") + ":"
				+ isoSrv.get("appHome"));
	}

	/**
	 * Upload the package to destination path
	 * 
	 * @param earName
	 * @param dstPath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void upload(String earName, String dstPath) throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection((String) isoSrv.get("srvAddr"), (Integer) isoSrv.get("srvPort"),
				(String) isoSrv.get("account"), (String) isoSrv.get("password"));
		SCPClient client = new SCPClient(conn);
//		client.put("dist/" + earName + ".tar.gz", dstPath);
		log.info("Local file size: " + new File("dist/" + earName + ".tar.gz").length());
		log.info("Remote file size: ");
		SshUtil.execCommand(conn, "du -s -b " + dstPath + "/" + earName + ".tar.gz | awk '{print $1}'");
		conn.close();
		log.info("dist/" + earName + ".tar.gz has been uploaded to " + isoSrv.get("srvAddr") + ":" + dstPath);
	}

	/**
	 * Cleanup the local transit folder's ear package
	 * 
	 * @param earName
	 */
	private static void cleanupLocal(String earName) {
		File file = new File("dist/" + earName + ".tar.gz");
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * Remove the history ear folder
	 * 
	 * @param earName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void removeOld(String earName) throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection((String) isoSrv.get("srvAddr"), (Integer) isoSrv.get("srvPort"),
				(String) isoSrv.get("account"), (String) isoSrv.get("password"));
		SshUtil.execCommand(conn, "cd " + (String) isoSrv.get("appHome") + "; if [ -d " + earName + " ]; then rm -rf "
				+ earName + "; fi");
		conn.close();
		log.info((String) isoSrv.get("appHome") + "/" + earName + " has been removed.");
	}

	/**
	 * Decompress the ear package
	 * 
	 * @param earName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void decompress(String earName) throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection((String) isoSrv.get("srvAddr"), (Integer) isoSrv.get("srvPort"),
				(String) isoSrv.get("account"), (String) isoSrv.get("password"));
		SshUtil.execCommand(conn, "cd " + (String) isoSrv.get("appHome") + "; tar -xzf " + earName + ".tar.gz");
		conn.close();
		log.info((String) isoSrv.get("appHome") + "/" + earName + ".tar.gz has been decompressed.");
	}

	/**
	 * Decompress
	 * 
	 * @param earName
	 * @param dstPath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void decompress(String earName, String dstPath) throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection((String) isoSrv.get("srvAddr"), (Integer) isoSrv.get("srvPort"),
				(String) isoSrv.get("account"), (String) isoSrv.get("password"));
		SshUtil.execCommand(conn, "cd " + dstPath + "; tar -xzf " + earName + ".tar.gz");
		conn.close();
		log.info(dstPath + "/" + earName + ".tar.gz has been decompressed.");
	}

	/**
	 * Free up the isolated environment's disk space
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void cleanupRemote(String earName) throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection((String) isoSrv.get("srvAddr"), (Integer) isoSrv.get("srvPort"),
				(String) isoSrv.get("account"), (String) isoSrv.get("password"));
		SshUtil.execCommand(conn, "cd " + (String) isoSrv.get("appHome") + "; rm -f " + earName + ".tar.gz");
		conn.close();
		log.info((String) isoSrv.get("appHome") + "/" + earName + ".tar.gz has been cleared.");
	}

	private static void cleanupRemote(String earName, String dstPath) throws IOException, InterruptedException {
		Connection conn = SshUtil.getConnection((String) isoSrv.get("srvAddr"), (Integer) isoSrv.get("srvPort"),
				(String) isoSrv.get("account"), (String) isoSrv.get("password"));
		SshUtil.execCommand(conn, "cd " + dstPath + "; rm -f " + earName + ".tar.gz");
		conn.close();
		log.info(dstPath + "/" + earName + ".tar.gz has been cleared.");
	}

	/**
	 * Reload shmprm
	 */
	private static void reloadShm() {

	}

	/**
	 * Auto deploy the ear to isolated environment
	 * 
	 * @param earName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void deploy(String earName) throws IOException, InterruptedException {
		clearBackup(earName);
		compress(earName);
		download(earName);
		upload(earName);
		cleanupLocal(earName);
		removeOld(earName);
		decompress(earName);
		cleanupRemote(earName);
	}

	/**
	 * Auto deploy the ear to isolated environment (variant), overlay installation
	 * 
	 * @param earName
	 * @param dstPath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void deploy(String earName, String dstPath) throws IOException, InterruptedException {
		clearBackup(earName);
		compress(earName);
		download(earName);
		upload(earName, dstPath);
		cleanupLocal(earName);
		decompress(earName, dstPath);
		cleanupRemote(earName, dstPath);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
//		deploy("interface.ear");
//		deploy("ngesop.ear");
//		deploy("esop.ear");
//		deploy("shmprm", (String) shmSrv.get("appHome"));
		Connection conn = SshUtil.getConnection((String) isoSrv.get("srvAddr"), (Integer) isoSrv.get("srvPort"),
				(String) isoSrv.get("account"), (String) isoSrv.get("password"));
		SshUtil.execCommand(conn,
				"su - shmprm <<!\n" + "ps -ef | grep dbc_loader | grep -v grep | awk '{print $2}' | xargs kill -9\n"
						+ "ipcrm -M 0x12345680\n" + "ipcrm -M 0x4100ccf2\n" + "./load-brfshm.sh\n" + "./load.sh\n"
						+ "!");
		conn.close();
	}
}
