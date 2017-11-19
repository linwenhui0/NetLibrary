package com.hlibrary.net.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.HttpURLConnection;

public class CookieManage {

	private final String SESSION_NAME = "session_name",
			SESSION_KEY = "session_key";
	private SharedPreferences preferences;
	private String Session;
	private volatile static CookieManage instance;

	private CookieManage(Context mContext) {
		preferences = mContext.getSharedPreferences(SESSION_NAME,
				Context.MODE_PRIVATE);
		Session = preferences.getString(SESSION_KEY, null);
	}

	public static CookieManage getInstance(Context mContext) {
		if (instance == null)
			synchronized (CookieManage.class) {
				if (instance == null)
					instance = new CookieManage(mContext);
			}
		return instance;
	}

	public String getSession() {
		return Session;
	}

	private void saveSessionTo() {
		if (Session != null) {
			preferences.edit().putString(SESSION_KEY, Session).commit();
		}
	}

	/**
	 * @函数名称：saveCookie
	 * @功能描述：保存Cookie
	 * @返回类型：boolean
	 * @返回数据
	 */
	public boolean saveCookie(HttpURLConnection conn) {
		if (conn.getHeaderField("set-cookie") != null) {
			Session = conn.getHeaderField("set-cookie");
			saveSessionTo();
			return true;
		}
		return false;
	}
}
