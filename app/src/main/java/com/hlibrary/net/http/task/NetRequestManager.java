package com.hlibrary.net.http.task;

import android.content.Context;

import com.hlibrary.net.callback.IResultErrorCallback;
import com.hlibrary.net.util.Constants;

import java.util.Map;

/**
 * @author linwenhui
 * @date 20180819
 */
public class NetRequestManager {
    private static NetRequestManager instance;
    private Context context;

    private NetRequestManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static NetRequestManager getInstance(Context context) {
        if (instance == null) {
            synchronized (NetRequestManager.class) {
                if (instance == null) {
                    instance = new NetRequestManager(context);
                }
            }
        }
        return instance;
    }

    public <T, D extends IResultErrorCallback> NormalAsynHttp<T, D> executeRequest(String url, Map<String, String> params,
                                                                                   Class<T> cls, D callback) {
        return executeRequest(Constants.GET, url, params, false, cls, callback);
    }

    public <T, D extends IResultErrorCallback> NormalAsynHttp<T, D> executeRequest(int httpMethod, String url, Map<String, String> params,
                                                                                   Class<T> cls, D callback) {
        return executeRequest(httpMethod, url, params, false, cls, callback);
    }

    public <T, D extends IResultErrorCallback> NormalAsynHttp<T, D> executeRequest(int httpMethod, String url, Map<String, String> params,
                                                                                   boolean isSaveCookie, Class<T> cls, D callback) {
        NormalAsynHttp<T, D> normalAsynHttp = new NormalAsynHttp<>(context, httpMethod, params, isSaveCookie, cls);
        normalAsynHttp.callback = callback;
        normalAsynHttp.doPost(url);
        return normalAsynHttp;
    }

}
