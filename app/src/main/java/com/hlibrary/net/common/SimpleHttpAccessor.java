package com.hlibrary.net.common;

import android.content.Context;

import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.model.Requests;
import com.hlibrary.net.model.Respond;
import com.hlibrary.net.util.CookieManage;
import com.hlibrary.util.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleHttpAccessor extends BaseHttpAccessor implements
		IHttpAccessor {

	private final static String TAG = "SimpleHttpAccessor";

	private static final int CONNECT_TIMEOUT = 10 * 1000;

	private static final int READ_TIMEOUT = 10 * 1000;
	private HttpURLConnection urlConnection;

	public SimpleHttpAccessor(Context mCtx) {
		super(mCtx);
	}

	@Override
	public Respond executeNormalTask(HttpMethod httpMethod, String url,
									 Requests param, int connectTimeOut, int readTimeOut,
									 boolean isSaveCookie, boolean isNeedValidHttps) {
		if (httpMethod == HttpMethod.Get)
			return doGet(url, param, connectTimeOut, readTimeOut, isSaveCookie);
		return doPost(url, param, connectTimeOut, readTimeOut, isSaveCookie);
	}

	@Override
	public Respond executeNormalTask(HttpMethod httpMethod, String url,
									 Requests param, boolean isSaveCookie, boolean isNeedValidHttps) {
		return executeNormalTask(httpMethod, url, param, CONNECT_TIMEOUT,
				READ_TIMEOUT, isSaveCookie,isNeedValidHttps);
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
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(connectTimeOut);
			urlConnection.setReadTimeout(readTimeOut);
			urlConnection.setInstanceFollowRedirects(false);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection
					.setRequestProperty("Accept-Encoding", "gzip, deflate");
			CookieManage cookieMange = CookieManage.getInstance(mCtx);
			String SessionId = cookieMange.getSession();
			if (SessionId != null)
				urlConnection.setRequestProperty("cookie", SessionId);
			urlConnection.connect();

			DataOutputStream out = new DataOutputStream(
					urlConnection.getOutputStream());
			out.write(param.encodeUrl().getBytes());
			out.flush();
			out.close();
			respond = handleResponse(urlConnection, isSaveCookie);
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
			Logger.getInstance().i(TAG,urlBuilder.toString());
			URL url = new URL(urlBuilder.toString());
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(false);
			urlConnection.setConnectTimeout(connectTimeOut);
			urlConnection.setReadTimeout(readTimeOut);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection
					.setRequestProperty("Accept-Encoding", "gzip, deflate");
			CookieManage cookieMange = CookieManage.getInstance(mCtx);
			String SessionId = cookieMange.getSession();
			if (SessionId != null)
				urlConnection.setRequestProperty("cookie", SessionId);
			urlConnection.connect();
			respond = handleResponse(urlConnection, isSaveCookie);
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
		if (urlConnection != null) {
			urlConnection.disconnect();
			urlConnection = null;
		}
	}

}
