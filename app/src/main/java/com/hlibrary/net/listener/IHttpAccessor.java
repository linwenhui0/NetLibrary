package com.hlibrary.net.listener;


import com.hlibrary.net.model.HttpMethod;
import com.hlibrary.net.model.Request;
import com.hlibrary.net.model.Respond;

public interface IHttpAccessor {

    /**
     *
     * @param httpMethod 请求方式
     * @param url 请求地址
     * @param param 请求参数
     * @param connectTimeOut 连接超时时间
     * @param readTimeOut 读时超时时间
     * @param isSaveCookie 是否保存cookie
     * @return
     */
    Respond executeNormalTask(HttpMethod httpMethod, String url,
                              Request param, int connectTimeOut, int readTimeOut,
                              boolean isSaveCookie);

    /**
     * 请求中断
     */
    void abort();

}
