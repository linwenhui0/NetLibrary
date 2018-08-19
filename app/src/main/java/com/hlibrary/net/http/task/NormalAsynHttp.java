package com.hlibrary.net.http.task;

import android.content.Context;
import android.os.AsyncTask;

import com.hlibrary.net.callback.IResultErrorCallback;

import java.util.Map;

/**
 * @param <T>
 * @param <D>
 * @author linwenhui
 */
public class NormalAsynHttp<T, D extends IResultErrorCallback> extends BaseAsynHttp<D> {


    private Task<T, D> netTask = null;
    private Class<T> clz;


    /**
     * 构造函数
     *
     * @param method     网络请求方式
     * @param params     请求参数
     * @param saveCookie 是否保存cookie
     */
    NormalAsynHttp(Context context, int method, Map<String, String> params, boolean saveCookie, Class<T> clz) {
        super(context, method, params, saveCookie);
        this.clz = clz;
    }

    @Override
    public void setCallback(D callback) {
        super.setCallback(callback);
        if (netTask != null) {
            netTask.setCallback(callback);
        }
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
        if (netTask != null) {
            netTask.cancel(true);
            netTask = null;
        }

    }


    /**
     * 发起请求
     *
     * @param url
     */
    public AsyncTask doPost(String url) {
        if (netTask == null) {
            netTask = new Task<>(accessor, method, params, saveCookie, clz, callback, parseCallback);
            netTask.executeOnExecutor(threadPool, url);
        } else {
            if (netTask.getStatus() == AsyncTask.Status.FINISHED) {
                netTask = new Task<>(accessor, method, params, saveCookie, clz, callback, parseCallback);
                netTask.executeOnExecutor(threadPool, url);
            }
        }
        return netTask;
    }


}
