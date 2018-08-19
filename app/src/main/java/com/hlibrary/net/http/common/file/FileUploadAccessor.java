package com.hlibrary.net.http.common.file;

import android.content.Context;

import com.hlibrary.net.callback.IFileUploadCallback;
import com.hlibrary.net.model.Respond;
import com.hlibrary.net.util.Constants;
import com.hlibrary.net.util.RequestUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @author linwenhui
 */
public class FileUploadAccessor extends BaseFileAccessor {

    private IFileUploadCallback fileUploadCallback;
    private boolean abort;

    public FileUploadAccessor(Context mCtx) {
        super(mCtx);
    }

    public FileUploadAccessor setFileUploadCallback(IFileUploadCallback fileUploadCallback) {
        this.fileUploadCallback = fileUploadCallback;
        return this;
    }

    @Override
    public Respond executeRequest(int httpMethod, String urlStr, Map<String, String> params, int connectTimeOut, int readTimeOut, boolean isSaveCookie) {
        byte[] barry = null;
        String BOUNDARYSTR = getBoundry();
        int contentLength = 0;
        String sendStr = "";
        String[] results = null;
        File targetFile = null;
        try {
            barry = ("--" + BOUNDARYSTR + "--\r\n").getBytes("UTF-8");
            results = RequestUtils.getBoundaryMessage(BOUNDARYSTR, params);
            sendStr = results[0];
            targetFile = new File(results[1]);
            contentLength += sendStr.getBytes("UTF-8").length
                    + targetFile.length() + 2 * barry.length;
        } catch (UnsupportedEncodingException e) {
            return new Respond(Respond.FALSE,Respond.ENCODING_ERROR);
        }

        String lenStr = Integer.toString(contentLength);

        BufferedOutputStream out = null;
        FileInputStream fis = null;
        Respond respond;
        try {
            URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(connectTimeOut);
            urlConnection.setReadTimeout(readTimeOut);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Content-type", "multipart/form-data;boundary=" + BOUNDARYSTR);
            urlConnection.setRequestProperty("Content-Length", lenStr);
            urlConnection.setFixedLengthStreamingMode(contentLength);
            urlConnection.connect();

            out = new BufferedOutputStream(urlConnection.getOutputStream());

            int bytesRead;
            final int maxBufferSize = 1024;
            byte[] buffer = new byte[maxBufferSize];
            long transferred = 0;
            final Thread thread = Thread.currentThread();
            out.write(sendStr.getBytes("UTF-8"));
            fis = new FileInputStream(targetFile);
            while ((bytesRead = fis.read(buffer)) > -1) {
                if (thread.isInterrupted()) {
                    throw new InterruptedIOException();
                }
                if (abort) {
                    if (fileUploadCallback != null) {
                        fileUploadCallback.cancel();
                    }
                    throw new Exception(Constants.CANCEL);
                }
                out.write(buffer, 0, bytesRead);
                transferred += bytesRead;
                if (transferred % 50 == 0) {
                    out.flush();
                }
                if (fileUploadCallback != null) {
                    fileUploadCallback.onProgress(1.0 * transferred / targetFile.length());
                }
            }
            out.write(barry);
            out.flush();
            out.close();
            if (fileUploadCallback != null) {
                fileUploadCallback.waitServerResponse();
            }
            respond = handleResponse(urlConnection, false);
        } catch (Exception e) {
            e.printStackTrace();
            respond = new Respond(Respond.FALSE, Respond.TIME_OUT);
        } finally {
            closeSilently(fis);
            closeSilently(out);
            abort();
        }
        return respond;
    }

    @Override
    public void abort() {
        abort = true;
        super.abort();
    }
}
