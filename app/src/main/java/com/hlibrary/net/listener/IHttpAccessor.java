package com.hlibrary.net.listener;


import com.hlibrary.net.model.Requests;
import com.hlibrary.net.model.Respond;

public interface IHttpAccessor {

    Respond executeNormalTask(HttpMethod httpMethod, String url,
                              Requests param, boolean isSaveCookie, boolean isNeedValidHttps);

    Respond executeNormalTask(HttpMethod httpMethod, String url,
                              Requests param, int connectTimeOut, int readTimeOut,
                              boolean isSaveCookie, boolean isNeedValidHttps);

    void abort();

    public static enum HttpMethod {
        Post, Get
    }

}
