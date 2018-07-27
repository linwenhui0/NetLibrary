package com.hlibrary.net.callback;

public interface IProgressCallback extends IResultErrorCallback {

    void onProgress(double ratio);

}
