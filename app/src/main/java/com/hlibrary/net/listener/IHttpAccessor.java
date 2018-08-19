package com.hlibrary.net.listener;


import com.hlibrary.net.model.Respond;

import java.util.Map;

/**
 * @author linwenhui
 */
public interface IHttpAccessor {

    /**
     * 网线请求接口
     *
     * @param httpMethod     请求方式
     * @param url            请求地址
     * @param params         请求参数
     * @param connectTimeOut 连接超时时间
     * @param readTimeOut    读时超时时间
     * @param isSaveCookie   是否保存cookie
     * @return Result
     */
    Respond executeRequest(int httpMethod, String url, Map<String, String> params,
                           int connectTimeOut, int readTimeOut,
                           boolean isSaveCookie);

    /**
     * 请求中断
     */
    void abort();

}
