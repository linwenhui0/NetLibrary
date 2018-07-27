package com.hlibrary.net.http.common;

import android.content.Context;

import com.hlibrary.net.model.HttpMethod;
import com.hlibrary.net.model.Request;
import com.hlibrary.net.model.Respond;
import com.hlibrary.net.util.CookieManage;
import com.hlibrary.util.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleHttpAccessor extends BaseHttpAccessor {

    private final static String TAG = "SimpleHttpAccessor";

    private static final int CONNECT_TIMEOUT = 10 * 1000;

    private static final int READ_TIMEOUT = 10 * 1000;


    public SimpleHttpAccessor(Context mCtx) {
        super(mCtx);
    }

    @Override
    public Respond executeNormalTask(HttpMethod httpMethod, String url,
                                     Request param, int connectTimeOut, int readTimeOut,
                                     boolean isSaveCookie) {
        if (httpMethod == HttpMethod.GET)
            return doGet(url, param, connectTimeOut, readTimeOut, isSaveCookie);
        return doPost(url, param, connectTimeOut, readTimeOut, isSaveCookie);
    }


    public Respond doPost(String urlAddress, Request param,
                          int connectTimeOut, int readTimeOut, boolean isSaveCookie) {
        Respond respond = null;
        try {
            URL url = new URL(urlAddress);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(connectTimeOut);
            urlConnection.setReadTimeout(readTimeOut);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection
                    .setRequestProperty("Accept-Encoding", "gzip, deflate");
            CookieManage cookieMange = CookieManage.getInstance(mCtx);
            String SessionId = cookieMange.getSession(urlAddress);
            if (SessionId != null)
                urlConnection.setRequestProperty("cookie", SessionId);
            urlConnection.connect();

            DataOutputStream out = new DataOutputStream(
                    urlConnection.getOutputStream());
            out.write(param.encodeUrl().getBytes());
            out.flush();
            out.close();
            respond = handleResponse(urlConnection, isSaveCookie);
        } catch (IOException e) {
            e.printStackTrace();
            respond = new Respond(Respond.FALSE, Respond.TIME_OUT);
        } finally {
            abort();
        }
        return respond;
    }


    public Respond doGet(String urlStr, Request param, int connectTimeOut,
                         int readTimeOut, boolean isSaveCookie) {
        Respond respond = null;
        try {
            StringBuilder urlBuilder = new StringBuilder(urlStr);
            if (param != null)
                urlBuilder.append("?").append(param.encodeUrl());
            Logger.getInstance().i(TAG, urlBuilder.toString());
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(connectTimeOut);
            urlConnection.setReadTimeout(readTimeOut);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection
                    .setRequestProperty("Accept-Encoding", "gzip, deflate");
            CookieManage cookieMange = CookieManage.getInstance(mCtx);
            String SessionId = cookieMange.getSession(urlStr);
            if (SessionId != null)
                urlConnection.setRequestProperty("cookie", SessionId);
            urlConnection.connect();
            respond = handleResponse(urlConnection, isSaveCookie);
        } catch (IOException e) {
            e.printStackTrace();
            respond = new Respond(Respond.FALSE, Respond.TIME_OUT);
        } finally {
            abort();
        }
        return respond;
    }


}
