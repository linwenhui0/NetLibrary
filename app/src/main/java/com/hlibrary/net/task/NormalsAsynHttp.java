package com.hlibrary.net.task;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.config.HttpParamConfig;
import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.listener.parse.IParse;
import com.hlibrary.net.model.Respond;
import com.hlibrary.util.Logger;

import java.util.List;


public class NormalsAsynHttp<T> extends NormalAsynHttp<T> {

    private NetTask netTask;

    public NormalsAsynHttp(HttpConfig httpConfig, Class<T> clz) {
        super(httpConfig, clz);
    }

    public NormalsAsynHttp(HttpConfig httpConfig, IHttpAccessor accessor, Class<T> clz) {
        super(httpConfig, accessor, clz);
    }

    protected List<T> parseArray(Respond respond) {
        IParse parse = HttpParamConfig.getInstance().getParseFormat();
        if (parse.isValidRespond(respond)) {
            Logger.getInstance().i(" === parseArray === code = " + respond.getCode() + " = data = " + respond.getData());
            final String parseArr = parse.getArrayString(respond);
            return JSON.parseArray(parseArr, clz);
        }else {
            httpConfig.setErrorNotice(parse.errorNotice(respond));
        }
        return null;
    }


    public void onCancelled() {
        if (netTask != null)
            netTask.cancel(true);
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

    private class NetTask extends AsyncTask<String, Integer, List<T>> {
        @Override
        protected List<T> doInBackground(String... params) {
            HttpParamConfig config = HttpParamConfig.getInstance();
            Respond respond = accessor.executeNormalTask(
                    httpConfig.getHttpMethod(), params[0], httpConfig.getParams(),
                    config.getConnectTimeout(), config.getReadTimeout(),
                    false, false);
            return parseArray(respond);
        }

        @Override
        protected void onPostExecute(List<T> result) {
            if (getResult() == null)
                return;
            if (result != null) {
                getResults().onSuccee(result);
            } else {
                getResult().onError(httpConfig.getErrorNotice());
            }
        }

        @Override
        protected void onCancelled(List<T> t) {
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
