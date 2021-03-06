package com.hlibrary.net.http.task;

import android.content.Context;
import android.os.AsyncTask;

import com.hlibrary.net.callback.IFileDownloadCallback;
import com.hlibrary.net.http.common.file.FileDownloadAccessor;
import com.hlibrary.net.model.Respond;

/**
 * 文件下载(以HttpURLConnection方式实现)
 */
public class FileDownloadAsynHttp extends AsyncTask<String, Integer, Boolean> {

    private final static int DOWNLOAD_CONNECT_TIMEOUT = 15 * 1000;
    private final static int DOWNLOAD_READ_TIMEOUT = 60 * 1000;

    protected Context context;

    protected FileDownloadAccessor accessor;
    protected IFileDownloadCallback<Boolean> callback;

    /**
     * 构造函数
     *
     * @param context  网络请求参数
     * @param callback 文件下载监听
     */
    public FileDownloadAsynHttp(Context context, IFileDownloadCallback<Boolean> callback) {
        this.context = context;
        this.callback = callback;
        this.accessor = new FileDownloadAccessor(context);
    }

    /**
     * 文件下载地址
     *
     * @param url      网络地址
     * @param savePath 保存路径
     * @return true 下载文件成功
     */
    protected Boolean doGetSaveFile(String url, String savePath) {
        return accessor.doGetSaveFile(url, savePath,
                DOWNLOAD_CONNECT_TIMEOUT, DOWNLOAD_READ_TIMEOUT, callback);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if (params != null && params.length > 1) {
            return doGetSaveFile(params[0], params[1]);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (callback == null) {
            return;
        }
        if (result) {
            callback.onSuccee(true);
        } else {
            callback.onError(Respond.NET_ERROR);
        }
    }


    public void setCallback(IFileDownloadCallback<Boolean> callback) {
        this.callback = callback;
    }

    @Override
    protected void onCancelled(Boolean result) {
        // TODO Auto-generated method stub
        super.onCancelled(result);
        accessor.abort();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        accessor.abort();
    }

}
