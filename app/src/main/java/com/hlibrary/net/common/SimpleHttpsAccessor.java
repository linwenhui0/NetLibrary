package com.hlibrary.net.common;

import android.content.Context;
import android.text.TextUtils;

import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.model.Requests;
import com.hlibrary.net.model.Respond;
import com.hlibrary.net.util.CookieManage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SimpleHttpsAccessor extends BaseHttpAccessor implements
		IHttpAccessor {


	private static final int CONNECT_TIMEOUT = 10 * 1000;

	private static final int READ_TIMEOUT = 10 * 1000;
	private HttpsURLConnection uRLConnection;

	public class NullHostNameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
		}
	} };

	public SimpleHttpsAccessor(Context mCtx,boolean debug) {

		super(mCtx);
		// allow Android to use an untrusted certificate for SSL/HTTPS
		// connection
		// so that when you debug app, you can use Fiddler http://fiddler2.com
		// to logs all HTTPS traffic
		try {
			if (debug) {
				HttpsURLConnection
						.setDefaultHostnameVerifier(new NullHostNameVerifier());
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc
						.getSocketFactory());
			}
		} catch (Exception e) {
		}

	}

	@Override
	public Respond executeNormalTask(HttpMethod httpMethod, String url,
									 Requests param, boolean isSaveCookie, boolean isNeedValidHttps) {
		return executeNormalTask(httpMethod, url, param, CONNECT_TIMEOUT,
				READ_TIMEOUT, isSaveCookie, isNeedValidHttps);
	}

	@Override
	public Respond executeNormalTask(HttpMethod httpMethod, String url,
									 Requests param, int connectTimeOut, int readTimeOut,
									 boolean isSaveCookie, boolean isNeedValidHttps) {
		if (httpMethod == HttpMethod.Get)
			return doGet(url, param, connectTimeOut, readTimeOut, isSaveCookie);
		return doPost(url, param, connectTimeOut, readTimeOut, isSaveCookie);
	}

	private static Proxy getProxy() {
		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		if (!TextUtils.isEmpty(proxyHost) && !TextUtils.isEmpty(proxyPort)) {
			return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
					proxyHost, Integer.valueOf(proxyPort)));
		} else {
			return null;
		}
	}

	public Respond doPost(String urlAddress, Requests param,
			boolean isSaveCookie) {
		return doPost(urlAddress, param, CONNECT_TIMEOUT, READ_TIMEOUT,
				isSaveCookie);
	}

	public Respond doPost(String urlAddress, Requests param,
			int connectTimeOut, int readTimeOut, boolean isSaveCookie) {
		Respond respond = null;
		try {
			URL url = new URL(urlAddress);
			Proxy proxy = getProxy();
			if (proxy != null) {
				uRLConnection = (HttpsURLConnection) url.openConnection(proxy);
			} else {
				uRLConnection = (HttpsURLConnection) url.openConnection();
			}

			uRLConnection.setDoInput(true);
			uRLConnection.setDoOutput(true);
			uRLConnection.setRequestMethod("POST");
			uRLConnection.setUseCaches(false);
			uRLConnection.setConnectTimeout(connectTimeOut);
			uRLConnection.setReadTimeout(readTimeOut);
			uRLConnection.setInstanceFollowRedirects(false);
			uRLConnection.setRequestProperty("Connection", "Keep-Alive");
			uRLConnection.setRequestProperty("Charset", "UTF-8");
			uRLConnection
					.setRequestProperty("Accept-Encoding", "gzip, deflate");
			CookieManage cookieMange = CookieManage.getInstance(mCtx);
			String SessionId = cookieMange.getSession();
			if (SessionId != null)
				uRLConnection.setRequestProperty("cookie", SessionId);
			uRLConnection.connect();

			DataOutputStream out = new DataOutputStream(
					uRLConnection.getOutputStream());
			out.write(param.encodeUrl().getBytes());
			out.flush();
			out.close();
			respond = handleResponse(uRLConnection, isSaveCookie);
		} catch (IOException e) {
			e.printStackTrace();
			respond = new Respond(Respond.False, Respond.TimeOut);
		} finally {
			abort();
		}
		return respond;
	}

	public Respond doGet(String urlStr, Requests param, boolean isSaveCookie) {
		return doGet(urlStr, param, CONNECT_TIMEOUT, READ_TIMEOUT, isSaveCookie);
	}

	public Respond doGet(String urlStr, Requests param, int connectTimeOut,
			int readTimeOut, boolean isSaveCookie) {
		Respond respond = null;
		try {
			StringBuilder urlBuilder = new StringBuilder(urlStr);
			urlBuilder.append("?").append(param.encodeUrl());
			URL url = new URL(urlBuilder.toString());
			Proxy proxy = getProxy();
			if (proxy != null) {
				uRLConnection = (HttpsURLConnection) url.openConnection(proxy);
			} else {
				uRLConnection = (HttpsURLConnection) url.openConnection();
			}

			uRLConnection.setRequestMethod("GET");
			uRLConnection.setDoOutput(false);
			uRLConnection.setConnectTimeout(connectTimeOut);
			uRLConnection.setReadTimeout(readTimeOut);
			uRLConnection.setRequestProperty("Connection", "Keep-Alive");
			uRLConnection.setRequestProperty("Charset", "UTF-8");
			uRLConnection
					.setRequestProperty("Accept-Encoding", "gzip, deflate");
			CookieManage cookieMange = CookieManage.getInstance(mCtx);
			String SessionId = cookieMange.getSession();
			if (SessionId != null)
				uRLConnection.setRequestProperty("cookie", SessionId);
			uRLConnection.connect();
			respond = handleResponse(uRLConnection, isSaveCookie);
		} catch (IOException e) {
			e.printStackTrace();
			respond = new Respond(Respond.False, Respond.TimeOut);
		} finally {
			abort();
		}
		return respond;

	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub
		if (uRLConnection != null) {
			uRLConnection.disconnect();
			uRLConnection = null;
		}

	}

}
