package com.hlibrary.net.config;

import com.hlibrary.net.listener.parse.IParse;

/**
 * Created by linwenhui on 2017/2/28.
 */

public class HttpParamConfig {
    private int connectTimeout = 10000;
    private int readTimeout = 10000;
    private int uploadConntectTimeout = 15 * 1000;
    private int uploadReadTimeout = 60 * 1000;
    private int downloadConnectTimeout = 15 * 1000;
    private int downloadReadTimeout = 60 * 1000;

    private static HttpParamConfig instance;
    private static Object lock = new Object();
    private IParse parseFormat;

    private HttpParamConfig() {

    }

    public static HttpParamConfig getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null)
                    instance = new HttpParamConfig();
            }
        }
        return instance;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public HttpParamConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public HttpParamConfig setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public int getUploadConntectTimeout() {
        return uploadConntectTimeout;
    }

    public void setUploadConntectTimeout(int uploadConntectTimeout) {
        this.uploadConntectTimeout = uploadConntectTimeout;
    }

    public int getUploadReadTimeout() {
        return uploadReadTimeout;
    }

    public void setUploadReadTimeout(int uploadReadTimeout) {
        this.uploadReadTimeout = uploadReadTimeout;
    }

    public int getDownloadConnectTimeout() {
        return downloadConnectTimeout;
    }

    public void setDownloadConnectTimeout(int downloadConnectTimeout) {
        this.downloadConnectTimeout = downloadConnectTimeout;
    }

    public int getDownloadReadTimeout() {
        return downloadReadTimeout;
    }

    public void setDownloadReadTimeout(int downloadReadTimeout) {
        this.downloadReadTimeout = downloadReadTimeout;
    }

    public IParse getParseFormat() {
        return parseFormat;
    }

    public void setParseFormat(IParse parseFormat) {
        this.parseFormat = parseFormat;
    }
}
