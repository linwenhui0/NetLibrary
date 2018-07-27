package com.hlibrary.net.model;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.hlibrary.util.Logger;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Request {

    private final static String TAG = "Requests";

    private Map<String, String> params;
    private List<FileRequest> fileRequests;


    public Request() {
        params = new ArrayMap<>();
        fileRequests = new ArrayList<FileRequest>();
    }

    public Request put(String key, String value) {
        params.put(key, value);
        return this;
    }

    public Request remove(String key) {
        if (params.containsKey(key))
            params.remove(key);
        return this;
    }

    public Request put(FileRequest param) {
        fileRequests.add(param);
        return this;
    }

    //    public HttpEntity toEnity(String encoding)
//            throws UnsupportedEncodingException {
//        if (!fileRequests.isEmpty()
//                && !(buildBaseEntity instanceof BuildMultipartEntity))
//            buildBaseEntity = new BuildMultipartEntity();
//        return buildBaseEntity.toEnity(this, encoding);
//    }

    public String encodeUrl() {
        if (params == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!TextUtils.isEmpty(value) || key.equals("description")
                    || key.equals("url")) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }
                try {
                    sb.append(URLEncoder.encode(key, "UTF-8")).append("=")
                            .append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {

                }
            }

        }

        return sb.toString();
    }

    public String encodeJSONUrl() {
        if (params == null && params.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        JSONObject json = new JSONObject();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                json.put(key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            sb.append(URLEncoder.encode("message", "utf-8")).append("=").append(URLEncoder.encode(json.toString(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Logger.getInstance().i(TAG, sb.toString());
        return sb.toString();

    }

    public String[] getBoundaryMessage(String boundary) {
        StringBuffer res = new StringBuffer("--").append(boundary).append(
                "\r\n");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            res.append("Content-Disposition: form-data; name=\"").append(key)
                    .append("\"\r\n").append("\r\n").append(value)
                    .append("\r\n").append("--").append(boundary)
                    .append("\r\n");
        }
        String[] results = new String[2];
        if (!fileRequests.isEmpty()) {
            FileRequest param = fileRequests.get(0);
            String[] arr = param.getValue().split("/");
            String filename = arr[arr.length - 1];
            res.append(String
                    .format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\nContent-Type: %s\r\n\r\n",
                            param.getKey(), filename, param.getFileType()));
            results[1] = param.getValue();
        }
        results[0] = res.toString();
        return results;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public List<FileRequest> getFileRequests() {
        return fileRequests;
    }

    public FileRequest getFileRequest(int index) {
        return fileRequests.get(index);
    }

    public boolean isEmpty() {
        return params.isEmpty() && fileRequests.isEmpty();
    }

}
