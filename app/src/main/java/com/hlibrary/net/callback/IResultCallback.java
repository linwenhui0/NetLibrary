package com.hlibrary.net.callback;

/**
 * 网络请求回调接口
 */
public interface IResultCallback<T> extends IResultErrorCallback {

    void onSuccee(T t);

}
