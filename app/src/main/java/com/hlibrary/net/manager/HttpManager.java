package com.hlibrary.net.manager;

import android.content.Context;
import android.os.AsyncTask;

import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.listener.FileDownloadListener;
import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.listener.IResult;
import com.hlibrary.net.listener.IResults;
import com.hlibrary.net.model.Requests;
import com.hlibrary.net.task.FileDownloadAsynHttp;
import com.hlibrary.net.task.FileUploaderAsynHttp;
import com.hlibrary.net.task.NormalAsynHttp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by linwenhui on 2017/4/6.
 */

public final class HttpManager {

    private static HttpManager instance;
    private ExecutorService pool;
    private Context mCtx;

    private HttpManager(Context context) {
        this.mCtx = context;
        pool = Executors.newFixedThreadPool(5);
    }

    public static HttpManager getInstance(Context context) {
        if (instance == null)
            synchronized (HttpManager.class) {
                if (instance == null)
                    instance = new HttpManager(context);
            }
        return instance;
    }

    public synchronized AsyncTask fileDownReqGet(String url, FileDownloadListener listener) {
        HttpConfig httpConfig = new HttpConfig(mCtx, IHttpAccessor.HttpMethod.Get);
        return fileDownReq(httpConfig, url, listener);
    }

    public synchronized AsyncTask fileDownReqPost(String url, FileDownloadListener listener) {
        HttpConfig httpConfig = new HttpConfig(mCtx, IHttpAccessor.HttpMethod.Post);
        return fileDownReq(httpConfig, url, listener);
    }

    private synchronized AsyncTask fileDownReq(HttpConfig httpConfig, String url, FileDownloadListener listener) {
        FileDownloadAsynHttp fileDownloadAsynHttp = new FileDownloadAsynHttp(httpConfig, listener);
        fileDownloadAsynHttp.executeOnExecutor(pool, url);
        return fileDownloadAsynHttp;
    }

    public synchronized AsyncTask fileUpload(String url, Requests requests) {
        HttpConfig httpConfig = new HttpConfig(mCtx, IHttpAccessor.HttpMethod.Post);
        if (requests != null)
            httpConfig.setRequests(requests);
        FileUploaderAsynHttp fileUploaderAsynHttp = new FileUploaderAsynHttp(httpConfig);
        fileUploaderAsynHttp.setThreadPool(pool);
        return fileUploaderAsynHttp.doPost(url);
    }

    private synchronized <T> AsyncTask doReq(String url, HttpConfig httpConfig, Class<T> clazz, Requests requests, IResult<T> resultListener) {
        if (requests != null) {
            httpConfig.setRequests(requests);
        }
        NormalAsynHttp<T> normalAsynHttp = new NormalAsynHttp<>(httpConfig, clazz);
        normalAsynHttp.setThreadPool(pool);
        normalAsynHttp.setResult(resultListener);
        return normalAsynHttp.doPost(url);
    }

    public synchronized <T> AsyncTask doReqPost(String url, Class<T> clazz, Requests requests, IResult<T> resultListener) {
        HttpConfig httpConfig = new HttpConfig(mCtx, IHttpAccessor.HttpMethod.Post);
        return doReq(url, httpConfig, clazz, requests, resultListener);
    }

    public synchronized <T> AsyncTask doReqGet(String url, Class<T> clazz, Requests requests, IResult<T> resultListener) {
        HttpConfig httpConfig = new HttpConfig(mCtx, IHttpAccessor.HttpMethod.Get);
        return doReq(url, httpConfig, clazz, requests, resultListener);
    }

    private synchronized <T> AsyncTask doReq(String url, HttpConfig httpConfig, Class<T> clazz, Requests requests, IResults<T> resultsListener) {
        if (requests != null) {
            httpConfig.setRequests(requests);
        }
        NormalAsynHttp<T> normalAsynHttp = new NormalAsynHttp<>(httpConfig, clazz);
        normalAsynHttp.setThreadPool(pool);
        normalAsynHttp.setResults(resultsListener);
        return normalAsynHttp.doPost(url);
    }

    public synchronized <T> AsyncTask doReqPost(String url, Class<T> clazz, Requests requests, IResults<T> resultListener) {
        HttpConfig httpConfig = new HttpConfig(mCtx, IHttpAccessor.HttpMethod.Post);
        return doReq(url, httpConfig, clazz, requests, resultListener);
    }

    public synchronized <T> AsyncTask doReqGet(String url, Class<T> clazz, Requests requests, IResults<T> resultListener) {
        HttpConfig httpConfig = new HttpConfig(mCtx, IHttpAccessor.HttpMethod.Get);
        return doReq(url, httpConfig, clazz, requests, resultListener);
    }


}
