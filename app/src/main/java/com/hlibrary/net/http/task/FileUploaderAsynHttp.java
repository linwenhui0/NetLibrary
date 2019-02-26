package com.hlibrary.net.http.task;


import android.content.Context;

import com.hlibrary.net.callback.IFileUploadCallback;
import com.hlibrary.net.http.common.file.FileUploadAccessor;
import com.hlibrary.net.util.Constants;

import java.util.Map;


/**
 * 文件上传
 */
public class FileUploaderAsynHttp extends NormalAsynHttp<String, IFileUploadCallback> {


    /**
     * 构造函数
     *
     * @param params 网络请求参数
     */
    public FileUploaderAsynHttp(Context context, Map<String, String> params, int type) {
        super(context, Constants.POST, params, false, String.class, type);
        this.accessor = new FileUploadAccessor(context);
    }


}
