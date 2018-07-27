package com.hlibrary.net.http.okhttp;

import android.content.Context;

import com.hlibrary.net.callback.IFileUploadCallback;
import com.hlibrary.net.callback.IResultErrorCallback;
import com.hlibrary.net.http.okhttp.body.ProgressRequestBody;
import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.model.FileRequest;
import com.hlibrary.net.model.HttpMethod;
import com.hlibrary.net.model.Request;
import com.hlibrary.net.model.Respond;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttpAccessor<T extends IResultErrorCallback> implements IHttpAccessor {

    private Context context;
    private Call call = null;
    private T callback;

    public OKHttpAccessor(Context context) {
        this.context = context;
    }

    public void setCallback(T callback) {
        this.callback = callback;
    }

    @Override
    public Respond executeNormalTask(HttpMethod httpMethod, String url, Request params, int connectTimeOut, int readTimeOut, boolean isSaveCookie) {
        if (httpMethod == HttpMethod.GET) {
            return doGet(url, params, connectTimeOut, readTimeOut, isSaveCookie);
        }
        return doPost(url, params, connectTimeOut, readTimeOut, isSaveCookie);
    }

    private OkHttpClient buildClient(int connectTimeOut, int readTimeOut, boolean isSaveCookie) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
                .readTimeout(readTimeOut, TimeUnit.SECONDS)
                .writeTimeout(readTimeOut, TimeUnit.SECONDS);
        if (isSaveCookie) {
            builder.cookieJar(new OKHttpCookie(context));
        }
        OkHttpClient client = builder.build();
        return client;
    }

    private Respond doGet(String url, Request params, int connectTimeOut, int readTimeOut, boolean isSaveCookie) {
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("?").append(params.encodeUrl());
        Respond respond;
        OkHttpClient client = buildClient(connectTimeOut, readTimeOut, isSaveCookie);

        //构造Request对象
        //采用建造者模式，链式调用指明进行Get请求,传入Get的请求地址
        okhttp3.Request request = new okhttp3.Request.Builder().get().url(urlBuilder.toString()).build();
        call = client.newCall(request);
        try {
            Response response = call.execute();
            if (response.code() == HttpURLConnection.HTTP_OK) {
                respond = new Respond(Respond.SUCCEE, response.body().string());
            } else {
                respond = new Respond(Respond.FALSE, response.body().string());
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
            respond = new Respond(Respond.FALSE, Respond.TIME_OUT);
        }
        return respond;
    }

    private Respond doPost(String url, Request params, int connectTimeOut, int readTimeOut, boolean isSaveCookie) {
        RequestBody requestBody;
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(url);
        if (params.getFileRequests().isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.getParams().entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            requestBody = builder.build();
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            List<FileRequest> fileRequests = params.getFileRequests();
            final int filesCount = fileRequests.size();
            File updateFile;
            FileRequest fileRequest;
            for (Map.Entry<String, String> entry : params.getParams().entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
            for (int i = 0; i < filesCount; i++) {
                fileRequest = params.getFileRequest(i);
                updateFile = new File(fileRequest.getValue());
                builder.addFormDataPart("image", updateFile.getName(),
                        RequestBody.create(MediaType.parse(fileRequest.getFileType()), updateFile));
            }
            IFileUploadCallback fCallback = null;
            if (callback instanceof IFileUploadCallback) {
                fCallback = (IFileUploadCallback) callback;
            }
            requestBody = new ProgressRequestBody(builder.build(), fCallback);
        }

        okhttp3.Request request = requestBuilder.post(requestBody).build();
        OkHttpClient client = buildClient(connectTimeOut, readTimeOut, isSaveCookie);
        Respond respond;
        call = client.newCall(request);
        try {
            Response response = call.execute();
            if (response.code() == HttpURLConnection.HTTP_OK) {
                respond = new Respond(Respond.SUCCEE, response.body().string());
            } else {
                respond = new Respond(Respond.FALSE, response.body().string());
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
            respond = new Respond(Respond.FALSE, Respond.TIME_OUT);
        }
        return respond;
    }

    @Override
    public void abort() {
        if (call != null) {
            if (!call.isCanceled())
                call.cancel();
            call = null;
        }
    }
}
