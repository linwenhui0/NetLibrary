package com.hlibrary.net.listener;

/**
 * 文件下载回调接口
 */
public interface FileDownloadListener extends ProgressListener {
    void completed();

    void cancel();
}
