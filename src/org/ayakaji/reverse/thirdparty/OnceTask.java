/*******************************************************
 * A one-time task for collecting and sending software *
 * and hardware information that is not necessary for  *
 * repeated reporting.                                 *
 ******************************************************/
package org.ayakaji.reverse.thirdparty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.catalina.util.ServerInfo;
import org.ayakaji.reverse.helper.UdpClient;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class OnceTask extends Task {
	public static final int onceTaskDelaySec = 2000;
	private static final String[] osFilter = new String[] { "os", "arch", "description", "version" };
	private static final String[] cpuInfoFilter = new String[] { "cpuInfo", "mhz", "model", "totalCores" };
	private static final String[] sysPropsFilter = new String[] { "sysProps", "java.vm.name", "user.dir",
			"java.runtime.version", "user.name" };
	private static final String[] sysEnvFilter = new String[] { "sysEnv" };
	private static final String[] netCfgFilter = new String[] { "net", "address", "description", "name", "broadcast",
			"netmask" };

	@Override
	public void run() {
		UdpClient.send(merge(
				new String[] { getOSInfo(), getCpuInfo(), getSysProps(), getSysEnv(), getSrvInfo(), getNetCfg() }));
	}

	private static String getOSInfo() {
		JSONObject jsonObj = new JSONObject(new LinkedHashMap<String, Object>());
		OperatingSystem os = OperatingSystem.getInstance();
		jsonObj.put("os", os);
		return JSON.toJSONString(jsonObj, getIncludeFilter(osFilter), SerializerFeature.PrettyFormat);
	}

	private static String getCpuInfo() {
		JSONObject jsonObj = new JSONObject(new LinkedHashMap<String, Object>());
		Sigar sigar = new Sigar();
		try {
			CpuInfo[] cpuInfoArr = sigar.getCpuInfoList();
			if (cpuInfoArr != null && cpuInfoArr.length > 0)
				jsonObj.put("cpuInfo", cpuInfoArr[0]);
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return JSON.toJSONString(jsonObj, getIncludeFilter(cpuInfoFilter), SerializerFeature.PrettyFormat);
	}

	private static String getSysProps() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("sysProps", System.getProperties());
		return JSON.toJSONString(jsonObj, getIncludeFilter(sysPropsFilter), SerializerFeature.PrettyFormat);
	}

	private static String getSysEnv() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("sysEnv", System.getenv());
		return JSON.toJSONString(jsonObj, getIncludeFilter(sysEnvFilter), SerializerFeature.PrettyFormat);
	}

	private static String getSrvInfo() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("serverInfo", ServerInfo.getServerInfo());
		return JSON.toJSONString(jsonObj, SerializerFeature.PrettyFormat);
	}

	private static String getNetCfg() {
		JSONObject jsonObj = new JSONObject();
		List<NetInterfaceConfig> netCfgList = new ArrayList<NetInterfaceConfig>();
		Sigar sigar = new Sigar();
		String[] ifNames = null;
		try {
			ifNames = sigar.getNetInterfaceList();
		} catch (SigarException e) {
			e.printStackTrace();
		}
		for (int i = 0; ifNames != null && i < ifNames.length; i++) {
			NetInterfaceConfig ifcfg = null;
			try {
				ifcfg = sigar.getNetInterfaceConfig(ifNames[i]);
			} catch (SigarException e) {
				e.printStackTrace();
			}
			if (ifcfg != null && !ifcfg.getAddress().equals("0.0.0.0") && !ifcfg.getAddress().equals("127.0.0.1")) {
				netCfgList.add(ifcfg);
			}
		}
		jsonObj.put("net", netCfgList);
		return JSON.toJSONString(jsonObj, getIncludeFilter(netCfgFilter), SerializerFeature.PrettyFormat);
	}

}