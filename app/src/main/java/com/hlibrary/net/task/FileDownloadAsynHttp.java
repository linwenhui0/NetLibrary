package com.hlibrary.net.task;

import android.os.AsyncTask;

import com.hlibrary.net.common.file.FileDownloadAccessor;
import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.config.HttpParamConfig;
import com.hlibrary.net.listener.FileDownloadListener;
import com.hlibrary.net.listener.IResult;

/**
 * 文件下载(以HttpURLConnection方式实现)
 */
public class FileDownloadAsynHttp extends AsyncTask<String, Integer, Boolean> {

    protected HttpConfig httpConfig;

    protected FileDownloadAccessor accessor;
    private FileDownloadListener downloadListener;
    protected IResult<Boolean> result;

    /**
     * 构造函数
     *
     * @param httpConfig       网络请求参数
     * @param downloadListener 文件下载监听
     */
    public FileDownloadAsynHttp(HttpConfig httpConfig,
                                FileDownloadListener downloadListener) {
        this.httpConfig = httpConfig;
        this.downloadListener = downloadListener;
        this.accessor = new FileDownloadAccessor(httpConfig.getContext());
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
                HttpParamConfig.getInstance().getDownloadConnectTimeout(),
                HttpParamConfig.getInstance().getDownloadReadTimeout(), downloadListener);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if (params != null && params.length > 1)
            return doGetSaveFile(params[0], params[1]);
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (getResult() == null)
            return;
        if (result)
            getResult().onSuccee(true);
        else
            getResult().onError(httpConfig.getErrorNotice());
    }

    public IResult<Boolean> getResult() {
        return result;
    }

    /**
     * 回调接口设置
     *
     * @param result
     */
    public void setResult(IResult<Boolean> result) {
        this.result = result;
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
