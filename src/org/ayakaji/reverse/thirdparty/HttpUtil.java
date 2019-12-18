/**********************************
 * File Downloading               *
 * TLS is not supported in JDK1.6 *
 *********************************/
package org.ayakaji.reverse.thirdparty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

public class HttpUtil {
	private static final Log log = LogFactory.getLog(HttpUtil.class);
	private static final int connReqTimeoutMillis = 3000;
	private static final int connTimeoutMillis = 3000;
	private static final int sockTimeoutMillis = 3000;
	private static final String proxyHost = "10.19.240.116";
	private static final int proxyPort = 8801;
	private static final String proxyScheme = "http";
	private static final String destURI = "https://www.baidu.com/favicon.ico";

	private static TrustStrategy getTrustStrategy() {
		return new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				return true;
			}
		};
	}

	private static SSLContext getSslContext()
			throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		SSLContextBuilder builder = SSLContexts.custom();
		builder.loadTrustMaterial(null, getTrustStrategy());
		builder.setProtocol("TLSv1");
		SSLContext sslContext = builder.build();
		return sslContext;
	}

	private static SSLConnectionSocketFactory getSslConnSockFactory()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(getSslContext(),
				NoopHostnameVerifier.INSTANCE);
		return factory;
	}

	private static Registry<ConnectionSocketFactory> getSockFactoryRegistry()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		Registry<ConnectionSocketFactory> sockFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", getSslConnSockFactory()).build();
		return sockFactoryRegistry;
	}

	@SuppressWarnings("unused")
	private static PoolingHttpClientConnectionManager getConnectionManager()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(getSockFactoryRegistry());
		return cm;
	}

	private static CloseableHttpClient getHttpClient()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
//		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(getConnectionManager()).build();
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(getSslConnSockFactory()).build();
		return httpClient;
	}

	private static RequestConfig getRequestConfig() {
		Builder builder = RequestConfig.custom();
		builder.setConnectionRequestTimeout(connReqTimeoutMillis);
		builder.setConnectTimeout(connTimeoutMillis);
		builder.setSocketTimeout(sockTimeoutMillis);
		builder.setProxy(new HttpHost(proxyHost, proxyPort, proxyScheme));
		RequestConfig reqCfg = builder.build();
		return reqCfg;
	}

	private static HttpGet getHttpGet() {
		HttpGet httpGet = new HttpGet(destURI);
		httpGet.addHeader("Host", URI.create(destURI).getHost());
		httpGet.setConfig(getRequestConfig());
		return httpGet;
	}

	private static HttpResponse execute(HttpGet httpGet) throws KeyManagementException, ClientProtocolException,
			NoSuchAlgorithmException, KeyStoreException, IOException {
		HttpResponse response = getHttpClient().execute(httpGet);
		return response;
	}

	private static void download(HttpGet httpGet, HttpResponse response)
			throws UnsupportedOperationException, IOException {
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			String[] arr = httpGet.getURI().getPath().split("/");
			File dest = new File("dist/" + arr[arr.length - 1]);
			OutputStream output = new FileOutputStream(dest);
			int len = 0;
			byte[] ch = new byte[1024];
			while ((len = is.read(ch)) != -1) {
				output.write(ch, 0, len);
			}
			output.close();
			is.close();
		} else {
			log.error(response);
		}
	}

	public static void main(String[] args) throws KeyManagementException, ClientProtocolException,
			NoSuchAlgorithmException, KeyStoreException, IOException {
		HttpGet httpGet = getHttpGet();
		HttpResponse response = execute(httpGet);
		download(httpGet, response);
//		HttpGet httpGet = null;
//		try {
//			RequestConfig timeoutConfig = RequestConfig.custom().setConnectTimeout(3000)
//					.setConnectionRequestTimeout(3000).setSocketTimeout(3000).setProxy(proxy).build();
//			httpGet = new HttpGet();
//			httpGet.addHeader("Host", "ip.cn");
//			httpGet.setConfig(timeoutConfig);
//			HttpResponse downLoadResponse = httpClient.execute(httpGet);
//			StatusLine statusLine = downLoadResponse.getStatusLine();
//			int statusCode = statusLine.getStatusCode();
//			if (statusCode == 200) {
//				HttpEntity entity = downLoadResponse.getEntity();
//				InputStream input = entity.getContent();
//				File dest = new File("logo.gif");
//				OutputStream output = new FileOutputStream(dest);
//				int len = 0;
//				byte[] ch = new byte[1024];
//				while ((len = input.read(ch)) != -1) {
//					output.write(ch, 0, len);
//				}
//			} else {
//				System.out.println(downLoadResponse.toString());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (httpGet != null) {
//				httpGet.releaseConnection();
//			}
//		}
	}
}
