package com.hlibrary.net.callback;

public interface IFileUploadCallback extends IProgressCallback {

    void waitServerResponse();

    void completed();

}
