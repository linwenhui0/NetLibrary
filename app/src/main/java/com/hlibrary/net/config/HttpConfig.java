package com.hlibrary.net.config;

import android.content.Context;

import com.hlibrary.net.model.HttpMethod;
import com.hlibrary.net.model.Request;
import com.hlibrary.net.model.Respond;


/**
 * 网络请求数据配置
 */
public class HttpConfig {


    private Context mCtx;
    // 请求方式
    private HttpMethod httpMethod;
    // 请求参数
    private Request params;
    // 请求失败错误信息
    private String defaultErrorNotice;

    private boolean needValidHttps;
    private boolean saveCookie;

    /**
     * 构造函数
     */
    public HttpConfig(Context context) {
        this(context, HttpMethod.GET);
    }

    /**
     * 构造函数
     *
     * @param httpMethod 请求方式
     */
    public HttpConfig(Context context, HttpMethod httpMethod) {
        this.mCtx = context;
        this.httpMethod = httpMethod;
        defaultErrorNotice = Respond.TIME_OUT;
    }

    public Context getContext() {
        return mCtx;
    }


    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }


    public HttpConfig putParam(String key, String value) {
        if (params == null)
            params = new Request();
        params.put(key, value);
        return this;
    }

    public Request getParams() {
        return params;
    }

    public String getErrorNotice() {
        return defaultErrorNotice;
    }

    public void setErrorNotice(String defaultErrorNotice) {
        this.defaultErrorNotice = defaultErrorNotice;
    }

    public boolean isNeedValidHttps() {
        return needValidHttps;
    }

    public HttpConfig setNeedValidHttps(boolean needValidHttps) {
        this.needValidHttps = needValidHttps;
        return this;
    }

    public boolean isSaveCookie() {
        return saveCookie;
    }

    public HttpConfig setSaveCookie(boolean saveCookie) {
        this.saveCookie = saveCookie;
        return this;
    }
}
