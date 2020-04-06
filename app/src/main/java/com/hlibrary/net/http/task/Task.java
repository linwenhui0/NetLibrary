package com.hlibrary.net.http.task;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.hlibrary.net.callback.IFileUploadCallback;
import com.hlibrary.net.callback.IMulResultCallback;
import com.hlibrary.net.callback.IParseCallback;
import com.hlibrary.net.callback.IResultCallback;
import com.hlibrary.net.callback.IResultErrorCallback;
import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.model.Respond;
import com.hlibrary.net.util.Constants;
import com.hlibrary.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by linwenhui on 2018/3/19.
 */

class Task<T, D extends IResultErrorCallback> extends AsyncTask<String, Integer, List<T>> {

    private final static int CONNECT_TIMEOUT = 20;
    private final static int READ_TIMEOUT = 20;
    private int method;
    private Map<String, String> params;
    private IHttpAccessor accessor;
    private Class<T> clz;
    private D callback;
    private IParseCallback parseCallback;
    private boolean saveCookie;
    private Respond respond;


    public Task(IHttpAccessor accessor, int method, Map<String, String> params, boolean saveCookie, Class<T> clz, D callback, IParseCallback parseCallback) {
        this.accessor = accessor;
        this.clz = clz;
        this.method = method;
        this.params = params;
        this.callback = callback;
        this.parseCallback = parseCallback;
        this.saveCookie = saveCookie;
    }

    public Task<T, D> setCallback(D callback) {
        this.callback = callback;
        return this;
    }

    @Override
    protected final List<T> doInBackground(String... params) {
        respond = accessor.executeRequest(
                method, params[0], this.params,
                CONNECT_TIMEOUT, READ_TIMEOUT,
                saveCookie);
        return parse(respond);
    }

    @Override
    protected final void onPostExecute(List<T> results) {
        if (callback == null) {
            return;
        }
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
            if (respond != null) {
                callback.onError(respond.getErrorData());
            }
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
            Logger.Companion.getInstance().defaultTagI(" === parse === code = " + respond.getCode() + " = data = " + respond.getData());

            final String objJSON;
            if (callback instanceof IMulResultCallback) {
                objJSON = parseCallback.getArrayString(respond);
            } else {
                objJSON = parseCallback.getObjectString(respond);
            }
            if (clz.getName().equals(String.class.getName())) {
                List<T> objList = new ArrayList<>();
                objList.add((T) objJSON);
                return objList;
            } else {
                if (objJSON.startsWith(Constants.RESULT_START_JSON_OBJECT_FLAG)) {
                    List<T> objList = new ArrayList<>();
                    objList.add(JSON.parseObject(objJSON, clz));
                    return objList;
                } else if (objJSON.startsWith(Constants.RESULT_START_JSON_ARRAY_FLAG)) {
                    return JSON.parseArray(objJSON, clz);
                } else {
                    List objList = new ArrayList();
                    objList.add(objJSON);
                    return objList;
                }
            }
        } else {
            String errorMsg = parseCallback.errorNotice(respond);
            respond.setErrorData(errorMsg);
        }
        return null;
    }
}
