package com.hlibrary.net.callback;

/**
 * @author linwenhui
 */
public interface IResultErrorCallback {

    /**
     * 出错回调接口
     *
     * @param msg 错误信息
     */
    void onError(String msg);

    /**
     * 接口无数据返回
     */
    void onEmpty();
}
