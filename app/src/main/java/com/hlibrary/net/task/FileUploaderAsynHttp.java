package com.hlibrary.net.task;


import android.os.AsyncTask;

import com.hlibrary.net.common.file.FileUploadAccessor;
import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.config.HttpParamConfig;
import com.hlibrary.net.listener.IFileUploadAccessor;
import com.hlibrary.net.listener.parse.IParse;
import com.hlibrary.net.model.Respond;


/**
 * 文件上传
 */
public class FileUploaderAsynHttp extends BaseAsynHttp<String> {


    protected IFileUploadAccessor accessor;
    private NetTask netTask;

    /**
     * 构造函数
     *
     * @param httpConfig 网络请求参数
     */
    public FileUploaderAsynHttp(HttpConfig httpConfig) {
        super(httpConfig);
        this.accessor = new FileUploadAccessor(httpConfig.getContext());
    }

    /**
     * 构造函数
     *
     * @param httpConfig 网络请求参数
     * @param accessor   上传文件实现类
     */
    public FileUploaderAsynHttp(HttpConfig httpConfig,
                                IFileUploadAccessor accessor) {
        super(httpConfig);
        this.accessor = accessor;
    }



    public AsyncTask doPost(String url) {
        if (netTask == null)
            netTask = new NetTask();
        if (netTask != null && netTask.getStatus() == AsyncTask.Status.FINISHED) {
            netTask = new NetTask();
            netTask.executeOnExecutor(threadPool, url);
        }
        netTask.executeOnExecutor(threadPool, url);
        return netTask;
    }


    private class NetTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return doUploadFile(params[0]).getData();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (getResult() == null)
                return;
            if (result != null) {
                getResult().onSuccee(result);
            } else {
                getResult().onError(httpConfig.getErrorNotice());
            }
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            accessor.abort();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            accessor.abort();
        }

        protected Respond doUploadFile(String url) {
            Respond respond = accessor.doUploadFile(url, httpConfig.getParams(),
                    HttpParamConfig.getInstance().getUploadConntectTimeout(),
                    HttpParamConfig.getInstance().getUploadReadTimeout());
            IParse parse = HttpParamConfig.getInstance().getParseFormat();
            if (parse.isValidRespond(respond)) {
                respond.setData(parse.getObjectString(respond));
            } else {
                respond.setData(null);
                httpConfig.setErrorNotice(parse.errorNotice(respond));
            }
            return respond;
        }
    }

}
