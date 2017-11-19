package com.hlibrary.net.listener;

/**
 * 网络请求回调接口
 */
public interface IResult<T> extends IResultError {

    void onSuccee(T t);


}
