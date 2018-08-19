package com.hlibrary.net.callback;

/**
 * @author linwenhui
 */
public interface IProgressCallback extends IResultErrorCallback {

    /**
     * 下载进度回调
     * @param ratio 下载进度
     */
    void onProgress(double ratio);

    /**
     * 文件取消下载
     */
    void cancel();

}
