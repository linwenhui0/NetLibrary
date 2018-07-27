package com.hlibrary.net.callback;

/**
 * 文件下载回调接口
 */
public interface IFileDownloadCallback<T> extends IProgressCallback, IResultCallback<T> {
    void completed();

    void cancel();
}
