package com.hlibrary.net.listener;

public interface FileUploaderProgressListener extends ProgressListener {

    void waitServerResponse();

    void completed();

}
