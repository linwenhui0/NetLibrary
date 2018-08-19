package com.hlibrary.net.http.common;

import android.content.Context;

import com.hlibrary.net.model.Respond;
import com.hlibrary.net.util.Constants;
import com.hlibrary.net.util.CookieManage;
import com.hlibrary.net.util.RequestUtils;
import com.hlibrary.util.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.hlibrary.net.util.Constants.debug;

/**
 * @author linwenhui
 */
public class SimpleHttpAccessor extends BaseHttpAccessor {

    private final static String TAG = "SimpleHttpAccessor";

    private static final int CONNECT_TIMEOUT = 10 * 1000;

    private static final int READ_TIMEOUT = 10 * 1000;


    public SimpleHttpAccessor(Context mCtx) {
        super(mCtx);
    }

    @Override
    public Respond executeRequest(int httpMethod, String url, Map<String, String> param, int connectTimeOut, int readTimeOut, boolean isSaveCookie) {
        if (httpMethod == Constants.GET) {
            return doGet(url, param, connectTimeOut * 1000, readTimeOut * 1000, isSaveCookie);
        }
        return doPost(url, param, connectTimeOut * 1000, readTimeOut * 1000, isSaveCookie);
    }


    public Respond doPost(String urlAddress, Map<String, String> param,
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
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            CookieManage cookieMange = CookieManage.getInstance(mCtx);
            String sessionId = cookieMange.getSession(urlAddress);
            if (sessionId != null) {
                urlConnection.setRequestProperty("cookie", sessionId);
            }
            urlConnection.connect();
            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            if (param != null) {
                out.write(RequestUtils.encodeUrl(param).getBytes());
            }
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


    public Respond doGet(String urlStr, Map<String, String> param, int connectTimeOut,
                         int readTimeOut, boolean isSaveCookie) {
        Respond respond = null;
        try {
            StringBuilder urlBuilder = new StringBuilder(urlStr);
            if (param != null) {
                urlBuilder.append("?").append(RequestUtils.encodeUrl(param));
            }
            if (debug) {
                Logger.getInstance().i(TAG, urlBuilder.toString());
            }
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
            String sessionId = cookieMange.getSession(urlStr);
            if (sessionId != null) {
                urlConnection.setRequestProperty("cookie", sessionId);
            }
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
