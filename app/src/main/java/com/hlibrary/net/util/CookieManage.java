package com.hlibrary.net.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.Cookie;

public class CookieManage {

    private final static String SESSION_NAME = "session_name";
    private SharedPreferences preferences;
    private volatile static CookieManage instance;

    private CookieManage(Context mContext) {
        preferences = mContext.getSharedPreferences(SESSION_NAME,
                Context.MODE_PRIVATE);
    }

    public static CookieManage getInstance(Context mContext) {
        if (instance == null)
            synchronized (CookieManage.class) {
                if (instance == null)
                    instance = new CookieManage(mContext);
            }
        return instance;
    }

    public String getSession(String url) {
        return preferences.getString(url, null);
    }

    private boolean saveSessionTo(String url, String session) {
        if (!TextUtils.isEmpty(session) && !TextUtils.isEmpty(url)) {
            return preferences.edit().putString(url, session).commit();
        }
        return false;
    }

    /**
     * @函数名称：saveCookie
     * @功能描述：保存Cookie
     * @返回类型：boolean
     * @返回数据
     */
    public synchronized boolean saveCookie(HttpURLConnection conn) {
        if (conn.getHeaderField("set-cookie") != null) {
            String session = conn.getHeaderField("set-cookie");
            saveSessionTo(conn.getURL().toString(), session);
            return true;
        }
        return false;
    }

    public synchronized boolean saveCookie(String url, List<Cookie> cookies) {
        saveSessionTo(url, JSON.toJSONString(cookies));
        return false;
    }


}
