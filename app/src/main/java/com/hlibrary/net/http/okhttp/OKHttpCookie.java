package com.hlibrary.net.http.okhttp;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.hlibrary.net.util.CookieManage;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * @author linwenhui
 */
public class OKHttpCookie implements CookieJar {

    private CookieManage cookieManage;

    public OKHttpCookie(Context context) {
        cookieManage = CookieManage.getInstance(context);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieManage.saveCookie(url.toString(), cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        String cookieJson = cookieManage.getSession(url.toString());
        if (TextUtils.isEmpty(cookieJson)) {
            return null;
        }
        return JSON.parseArray(cookieJson, Cookie.class);
    }
}
