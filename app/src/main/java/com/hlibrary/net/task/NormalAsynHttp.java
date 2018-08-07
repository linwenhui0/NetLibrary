package com.hlibrary.net.task;

import android.os.AsyncTask;

import com.hlibrary.net.callback.IResultErrorCallback;
import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.http.common.SimpleHttpAccessor;
import com.hlibrary.net.listener.IHttpAccessor;


public class NormalAsynHttp<T, D extends IResultErrorCallback> extends BaseAsynHttp<D> {


    protected Task<T, D> netTask = null;
    private Class<T> clz;

    /**
     * 构造函数
     *
     * @param httpConfig 网络请求参数
     */
    public NormalAsynHttp(HttpConfig httpConfig, Class<T> clz) {
        this(httpConfig, new SimpleHttpAccessor(httpConfig.getContext()), clz);
    }

    /**
     * 构造函数
     *
     * @param httpConfig 网络请求参数
     * @param accessor   网络请求的实现类
     */
    public NormalAsynHttp(HttpConfig httpConfig, IHttpAccessor accessor, Class<T> clz) {
        super(httpConfig, accessor);
        this.clz = clz;
    }

    @Override
    public void setCallback(D callback) {
        super.setCallback(callback);
        if (netTask != null)
            netTask.setCallback(callback);
    }

    /**
     * 是否执行完成
     *
     * @return
     */
    public boolean isFinish() {
        if (netTask != null && netTask.getStatus() == AsyncTask.Status.FINISHED) {
            return true;
        }
        return false;
    }

    /**
     * 取消网线操作
     */
    public void cancel() {
        if (netTask != null)
            netTask.cancel(true);
        netTask = null;
    }


    /**
     * 发起请求
     *
     * @param url
     */
    public AsyncTask doPost(String url) {
        if (netTask == null) {
            netTask = new Task<>(accessor, clz, httpConfig, callback, parseCallback);
            netTask.executeOnExecutor(threadPool, url);
        } else {
            if (netTask.getStatus() == AsyncTask.Status.FINISHED) {
                netTask = new Task<>(accessor, clz, httpConfig, callback, parseCallback);
                netTask.executeOnExecutor(threadPool, url);
            }
        }
        return netTask;
    }


}
