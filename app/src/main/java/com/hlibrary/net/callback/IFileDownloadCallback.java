package com.hlibrary.net.callback;

/**
 * 文件下载回调接口
 *
 * @author linwenhui
 */
public interface IFileDownloadCallback<T> extends IProgressCallback, IResultCallback<T> {
    /**
     * 文件下载完成
     */
    void completed();


}
