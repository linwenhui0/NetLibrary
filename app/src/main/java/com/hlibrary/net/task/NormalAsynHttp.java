package com.hlibrary.net.task;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.hlibrary.net.common.SimpleHttpAccessor;
import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.config.HttpParamConfig;
import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.listener.parse.IParse;
import com.hlibrary.net.model.Respond;
import com.hlibrary.util.Logger;


public class NormalAsynHttp<T> extends BaseAsynHttp<T> {

    protected IHttpAccessor accessor;
    private NetTask netTask;
    protected Class<T> clz;

    /**
     * 构造函数
     *
     * @param httpConfig 网络请求参数
     */
    public NormalAsynHttp(HttpConfig httpConfig, Class<T> clz) {
        this(httpConfig, new SimpleHttpAccessor(httpConfig.getContext()), clz);
    }

    /**
     * 构造函数
     *
     * @param httpConfig 网络请求参数
     * @param accessor   网络请求的实现类
     */
    public NormalAsynHttp(HttpConfig httpConfig, IHttpAccessor accessor, Class<T> clz) {
        super(httpConfig);
        this.accessor = accessor;
        this.clz = clz;
    }

    /**
     * 是否执行完成
     *
     * @return
     */
    public boolean isFinish() {
        if (netTask != null && netTask.getStatus() == AsyncTask.Status.FINISHED)
            return true;
        return false;
    }

    /**
     * 取消网线操作
     */
    public void onCancelled() {
        if (netTask != null)
            netTask.cancel(true);
    }

    /**
     * 发起请求
     *
     * @param url
     */
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

    /**
     * 返回解析
     *
     * @param respond
     * @return
     */
    protected T parse(Respond respond) {
        IParse parse = HttpParamConfig.getInstance().getParseFormat();
        if (parse.isValidRespond(respond)) {
            Logger.i(" === parse === code = " + respond.getCode() + " = data = " + respond.getData());
            final String objJSON = HttpParamConfig.getInstance().getParseFormat().getObjectString(respond);
            return JSON.parseObject(objJSON, clz);
        } else {
            httpConfig.setErrorNotice(parse.errorNotice(respond));
        }
        return null;
    }

    private class NetTask extends AsyncTask<String, Integer, T> {
        @Override
        protected T doInBackground(String... params) {
            Respond respond = accessor.executeNormalTask(
                    httpConfig.getHttpMethod(), params[0], httpConfig.getParams(),
                    HttpParamConfig.getInstance().getConnectTimeout(), HttpParamConfig.getInstance().getReadTimeout(),
                    false, false);
            return parse(respond);
        }

        @Override
        protected void onPostExecute(T result) {
            if (getResult() == null)
                return;
            if (result != null) {
                getResult().onSuccee(result);
            } else {
                getResult().onError(httpConfig.getErrorNotice());
            }
        }

        @Override
        protected void onCancelled(T t) {
            super.onCancelled(t);
            accessor.abort();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            accessor.abort();
        }
    }

}
