package com.hlibrary.net.common;

import android.content.Context;
import android.text.TextUtils;

import com.hlibrary.net.model.Respond;
import com.hlibrary.net.util.CookieManage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;


public abstract class BaseHttpAccessor {

	Context mCtx;

	public BaseHttpAccessor(Context mCtx) {
		this.mCtx = mCtx;
	}

	public void closeSilently(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ignored) {

			}
		}
	}

	protected Respond handleResponse(HttpURLConnection httpURLConnection,
									 boolean isSaveCookie) {
		int status = 0;
		try {
			status = httpURLConnection.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
			httpURLConnection.disconnect();
			return new Respond(Respond.False, Respond.TimeOut);
		}

		if (status != HttpURLConnection.HTTP_OK) {
			return handleError(httpURLConnection);
		}

		return readResult(httpURLConnection, isSaveCookie);
	}

	protected Respond handleError(HttpURLConnection urlConnection) {

		String result = readError(urlConnection);
		String err = null;
		int errCode = 0;
		try {
			JSONObject json = new JSONObject(result);
			err = json.optString("error_description", "");
			if (TextUtils.isEmpty(err)) {
				err = json.getString("error");
			}
			errCode = json.getInt("error_code");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new Respond(Respond.False, errCode + " " + err);
	}

	protected Respond readResult(HttpURLConnection urlConnection,
			boolean isSaveCookie) {
		InputStream is = null;
		BufferedReader buffer = null;
		Respond respond = null;
		try {
			is = urlConnection.getInputStream();

			String content_encode = urlConnection.getContentEncoding();

			if (null != content_encode && !"".equals(content_encode)
					&& content_encode.equals("gzip")) {
				is = new GZIPInputStream(is);
			}

			buffer = new BufferedReader(new InputStreamReader(is));
			StringBuilder strBuilder = new StringBuilder();
			String line;
			while ((line = buffer.readLine()) != null) {
				strBuilder.append(line);
			}
			respond = new Respond(Respond.Succee, strBuilder.toString());
			if (isSaveCookie) {
				CookieManage cookieMange = CookieManage.getInstance(mCtx);
				cookieMange.saveCookie(urlConnection);
			}
		} catch (IOException e) {
			e.printStackTrace();
			respond = new Respond(Respond.False, Respond.TimeOut);
		} finally {
			closeSilently(is);
			closeSilently(buffer);
			urlConnection.disconnect();
		}
		return respond;
	}

	protected String readError(HttpURLConnection urlConnection) {
		InputStream is = null;
		BufferedReader buffer = null;
		String errorStr = Respond.TimeOut;

		try {
			is = urlConnection.getErrorStream();

			if (is == null) {
				errorStr = Respond.NetError;
			}

			String content_encode = urlConnection.getContentEncoding();

			if (null != content_encode && !"".equals(content_encode)
					&& content_encode.equals("gzip")) {
				is = new GZIPInputStream(is);
			}

			buffer = new BufferedReader(new InputStreamReader(is));
			StringBuilder strBuilder = new StringBuilder();
			String line;
			while ((line = buffer.readLine()) != null) {
				strBuilder.append(line);
			}
			return strBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeSilently(is);
			closeSilently(buffer);
			urlConnection.disconnect();
		}
		return errorStr;
	}
	
	public abstract void abort();

}
