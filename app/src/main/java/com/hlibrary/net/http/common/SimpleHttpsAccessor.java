package com.hlibrary.net.http.common;

import android.content.Context;
import android.text.TextUtils;

import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.model.Respond;
import com.hlibrary.net.util.Constants;
import com.hlibrary.net.util.CookieManage;
import com.hlibrary.net.util.RequestUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SimpleHttpsAccessor extends BaseHttpAccessor<HttpsURLConnection> implements
        IHttpAccessor {


    private static final int CONNECT_TIMEOUT = 10 * 1000;

    private static final int READ_TIMEOUT = 10 * 1000;

    public class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
        }
    }};

    public SimpleHttpsAccessor(Context mCtx) {

        super(mCtx);
        // allow Android to use an untrusted certificate for SSL/HTTPS
        // connection
        // so that when you debug app, you can use Fiddler http://fiddler2.com
        // to logs all HTTPS traffic
        try {
            if (Constants.debug) {
                HttpsURLConnection
                        .setDefaultHostnameVerifier(new NullHostNameVerifier());
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc
                        .getSocketFactory());
            }
        } catch (Exception e) {
        }

    }

    @Override
    public Respond executeRequest(int httpMethod, String url, Map<String, String> params, int connectTimeOut, int readTimeOut, boolean isSaveCookie) {
        if (httpMethod == Constants.GET) {
            return doGet(url, params, connectTimeOut, readTimeOut, isSaveCookie);
        }
        return doPost(url, params, connectTimeOut, readTimeOut, isSaveCookie);
    }

    private static Proxy getProxy() {
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        if (!TextUtils.isEmpty(proxyHost) && !TextUtils.isEmpty(proxyPort)) {
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                    proxyHost, Integer.valueOf(proxyPort)));
        } else {
            return null;
        }
    }

    public Respond doPost(String urlAddress, Map<String, String> param,
                          int connectTimeOut, int readTimeOut, boolean isSaveCookie) {
        Respond respond = null;
        try {
            URL url = new URL(urlAddress);
            Proxy proxy = getProxy();
            if (proxy != null) {
                urlConnection = (HttpsURLConnection) url.openConnection(proxy);
            } else {
                urlConnection = (HttpsURLConnection) url.openConnection();
            }

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

            DataOutputStream out = new DataOutputStream(
                    urlConnection.getOutputStream());
            if (param != null && !param.isEmpty()) {
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
            if (param != null && !param.isEmpty()) {
                urlBuilder.append("?").append(RequestUtils.encodeUrl(param));
            }
            URL url = new URL(urlBuilder.toString());
            Proxy proxy = getProxy();
            if (proxy != null) {
                urlConnection = (HttpsURLConnection) url.openConnection(proxy);
            } else {
                urlConnection = (HttpsURLConnection) url.openConnection();
            }

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(connectTimeOut);
            urlConnection.setReadTimeout(readTimeOut);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
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
