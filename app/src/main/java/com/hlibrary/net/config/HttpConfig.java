package com.hlibrary.net.config;

import android.content.Context;

import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.model.Request;
import com.hlibrary.net.model.Requests;
import com.hlibrary.net.model.Respond;


/**
 * 网络请求数据配置
 */
public class HttpConfig {


    private Context mCtx;
    // 请求方式
    private IHttpAccessor.HttpMethod httpMethod;
    // 请求参数
    private Requests params;
    // 请求失败错误信息
    private String defaultErrorNotice;

    /**
     * 构造函数
     */
    public HttpConfig(Context context) {
        this(context, IHttpAccessor.HttpMethod.Get);
    }

    /**
     * 构造函数
     *
     * @param httpMethod 请求方式
     */
    public HttpConfig(Context context, IHttpAccessor.HttpMethod httpMethod) {
        this.mCtx = context;
        this.httpMethod = httpMethod;
        this.params = new Requests();
        defaultErrorNotice = Respond.TimeOut;
    }

    public Context getContext() {
        return mCtx;
    }


    public IHttpAccessor.HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(IHttpAccessor.HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Requests getParams() {
        return params;
    }

    public HttpConfig putParam(Request request) {
        params.put(request);
        return this;
    }


    public HttpConfig setRequests(Requests requests) {
        this.params = requests;
        return this;
    }

    public String getErrorNotice() {
        return defaultErrorNotice;
    }

    public void setErrorNotice(String defaultErrorNotice) {
        this.defaultErrorNotice = defaultErrorNotice;
    }

}
