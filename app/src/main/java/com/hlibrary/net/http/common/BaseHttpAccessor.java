package com.hlibrary.net.http.common;

import android.content.Context;
import android.text.TextUtils;

import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.model.Respond;
import com.hlibrary.net.util.Constants;
import com.hlibrary.net.util.CookieManage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

/**
 * @param <T>
 * @author linwenhui
 */
public abstract class BaseHttpAccessor<T extends HttpURLConnection> implements IHttpAccessor {

    Context mCtx;
    protected T urlConnection;

    public BaseHttpAccessor(Context mCtx) {
        this.mCtx = mCtx;
    }

    public void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {

            }
        }
    }

    protected Respond handleResponse(HttpURLConnection httpURLConnection,
                                     boolean isSaveCookie) {
        int status = 0;
        try {
            status = httpURLConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            httpURLConnection.disconnect();
            return new Respond(Respond.FALSE, Respond.NET_ERROR, e);
        }

        if (status != HttpURLConnection.HTTP_OK) {
            return handleError(httpURLConnection);
        }

        return readResult(httpURLConnection, isSaveCookie);
    }

    protected Respond handleError(HttpURLConnection urlConnection) {

        String result = readError(urlConnection);
        String err = null;
        int errCode = 0;
        try {
            JSONObject json = new JSONObject(result);
            err = json.optString("error_description", "");
            if (TextUtils.isEmpty(err)) {
                err = json.getString("error");
            }
            errCode = json.getInt("error_code");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Respond(Respond.FALSE, errCode + " " + err);
    }

    protected Respond readResult(HttpURLConnection urlConnection,
                                 boolean isSaveCookie) {
        InputStream is = null;
        BufferedReader buffer = null;
        Respond respond = null;
        try {
            is = urlConnection.getInputStream();

            String contentEncode = urlConnection.getContentEncoding();

            if (!TextUtils.isEmpty(contentEncode) && contentEncode.equals(Constants.GZIP)) {
                is = new GZIPInputStream(is);
            }

            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            respond = new Respond(Respond.SUCCEE, strBuilder.toString());
            if (isSaveCookie) {
                CookieManage cookieMange = CookieManage.getInstance(mCtx);
                cookieMange.saveCookie(urlConnection);
            }
        } catch (IOException e) {
            e.printStackTrace();
            respond = new Respond(Respond.FALSE, Respond.TIME_OUT);
        } finally {
            closeSilently(is);
            closeSilently(buffer);
            urlConnection.disconnect();
        }
        return respond;
    }

    protected String readError(HttpURLConnection urlConnection) {
        InputStream is = null;
        BufferedReader buffer = null;
        String errorStr = Respond.TIME_OUT;

        try {
            is = urlConnection.getErrorStream();

            if (is == null) {
                errorStr = Respond.NET_ERROR;
            }

            String contentEncode = urlConnection.getContentEncoding();

            if (!TextUtils.isEmpty(contentEncode) && contentEncode.equals(Constants.GZIP)) {
                is = new GZIPInputStream(is);
            }

            if (is != null) {
                buffer = new BufferedReader(new InputStreamReader(is));
                StringBuilder strBuilder = new StringBuilder();
                String line;
                while ((line = buffer.readLine()) != null) {
                    strBuilder.append(line);
                }
                return strBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
            closeSilently(buffer);
            urlConnection.disconnect();
        }
        return errorStr;
    }

    @Override
    public void abort() {
        if (urlConnection != null) {
            urlConnection.disconnect();
            urlConnection = null;
        }
    }

}
