package com.hlibrary.net.callback;

/**
 * @author linwenhui
 */
public interface IFileUploadCallback extends IProgressCallback {

    /**
     * 等服务端返回
     */
    void waitServerResponse();

    /**
     * 文件上传完成
     */
    void completed();

}
