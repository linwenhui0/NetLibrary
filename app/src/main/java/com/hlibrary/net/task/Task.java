package com.hlibrary.net.task;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.hlibrary.net.callback.IFileUploadCallback;
import com.hlibrary.net.callback.IMulResultCallback;
import com.hlibrary.net.callback.IParseCallback;
import com.hlibrary.net.callback.IResultCallback;
import com.hlibrary.net.callback.IResultErrorCallback;
import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.model.Respond;
import com.hlibrary.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linwenhui on 2018/3/19.
 */

class Task<T, D extends IResultErrorCallback> extends AsyncTask<String, Integer, List<T>> {

    private final static int CONNECT_TIMEOUT = 10000;
    private final static int READ_TIMEOUT = 10000;
    private IHttpAccessor accessor;
    private Class<T> clz;
    private HttpConfig httpConfig;
    private D callback;
    private IParseCallback parseCallback;

    public Task(IHttpAccessor accessor, Class<T> clz, HttpConfig httpConfig, IParseCallback parseCallback) {
        this(accessor, clz, httpConfig, null, parseCallback);
    }

    public Task(IHttpAccessor accessor, Class<T> clz, HttpConfig httpConfig, D callback, IParseCallback parseCallback) {
        this.accessor = accessor;
        this.clz = clz;
        this.httpConfig = httpConfig;
        this.callback = callback;
        this.parseCallback = parseCallback;
    }

    public Task<T, D> setCallback(D callback) {
        this.callback = callback;
        return this;
    }

    @Override
    protected final List<T> doInBackground(String... params) {
        Respond respond = accessor.executeNormalTask(
                httpConfig.getHttpMethod(), params[0], httpConfig.getParams(),
                CONNECT_TIMEOUT, READ_TIMEOUT,
                httpConfig.isSaveCookie());
        return parse(respond);
    }

    @Override
    protected final void onPostExecute(List<T> results) {
        if (callback == null)
            return;
        if (results != null) {
            if (results.isEmpty()) {
                callback.onEmpty();
            } else {
                if (callback instanceof IResultCallback) {
                    IResultCallback<T> iResult = (IResultCallback<T>) callback;
                    iResult.onSuccee(results.get(0));
                } else if (callback instanceof IMulResultCallback) {
                    IMulResultCallback<T> iResults = (IMulResultCallback<T>) callback;
                    iResults.onSuccee(results);
                } else if (callback instanceof IFileUploadCallback) {
                    IFileUploadCallback fileUploadCallback = (IFileUploadCallback) callback;
                    fileUploadCallback.completed();
                }
            }
        } else {
            callback.onError(httpConfig.getErrorNotice());
        }
    }


    @Override
    protected void onCancelled(List<T> t) {
        super.onCancelled(t);
        accessor.abort();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        accessor.abort();
    }

    /**
     * 返回解析
     *
     * @param respond
     * @return
     */
    List<T> parse(Respond respond) {
        if (parseCallback.isValidRespond(respond)) {
            Logger.getInstance().defaultTagI(" === parse === code = " + respond.getCode() + " = data = " + respond.getData());
            final String objJSON = parseCallback.getObjectString(respond);
            if (clz.getName().equals(String.class.getName())) {
                List<T> objList = new ArrayList<>();
                objList.add((T) objJSON);
                return objList;
            } else {
                if (objJSON.startsWith("{")) {
                    List<T> objList = new ArrayList<>();
                    objList.add(JSON.parseObject(objJSON, clz));
                    return objList;
                } else if (objJSON.startsWith("[")) {
                    return JSON.parseArray(objJSON, clz);
                } else {
                    List objList = new ArrayList();
                    objList.add(objJSON);
                    return objList;
                }
            }
        } else {
            httpConfig.setErrorNotice(parseCallback.errorNotice(respond));
        }
        return null;
    }
}
