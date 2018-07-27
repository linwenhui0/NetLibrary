package com.hlibrary.net.task;


import com.hlibrary.net.callback.IFileUploadCallback;
import com.hlibrary.net.http.common.file.FileUploadAccessor;
import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.listener.IHttpAccessor;


/**
 * 文件上传
 */
public class FileUploaderAsynHttp extends NormalAsynHttp<String, IFileUploadCallback> {


    /**
     * 构造函数
     *
     * @param httpConfig 网络请求参数
     */
    public FileUploaderAsynHttp(HttpConfig httpConfig) {
        super(httpConfig, new FileUploadAccessor(httpConfig.getContext()), String.class);
    }

    /**
     * 构造函数
     *
     * @param httpConfig 网络请求参数
     * @param accessor   上传文件实现类
     */
    public FileUploaderAsynHttp(HttpConfig httpConfig, IHttpAccessor accessor) {
        super(httpConfig, accessor, String.class);
    }

}
