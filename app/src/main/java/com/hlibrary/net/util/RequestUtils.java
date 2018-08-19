package com.hlibrary.net.util;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author linwenhui
 */
public class RequestUtils {
    private RequestUtils() {
    }

    public static String encodeUrl(Map<String, String> params) {
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

    public static String encodeJSONUrl(Map<String, String> params) {
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
        return sb.toString();

    }

    public static String[] getBoundaryMessage(String boundary, Map<String, String> params) {
        StringBuffer res = new StringBuffer("--").append(boundary).append(
                "\r\n");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (Constants.REQUEST_PARAM_FILE_TYPE.equals(key) || Constants.REQUEST_PARAM_FILE_NAME.equals(key) ||
                    Constants.REQUEST_PARAM_FILE_KEY.equals(key)) {
                continue;
            }
            res.append("Content-Disposition: form-data; name=\"").append(key)
                    .append("\"\r\n").append("\r\n").append(value)
                    .append("\r\n").append("--").append(boundary)
                    .append("\r\n");
        }
        String[] results = new String[2];
        if (params.containsKey(Constants.REQUEST_PARAM_FILE_NAME)) {
            String key = params.get(Constants.REQUEST_PARAM_FILE_KEY);
            String filename = params.get(Constants.REQUEST_PARAM_FILE_NAME);
            if (!TextUtils.isEmpty(filename) && !TextUtils.isEmpty(key)) {
                File file = new File(filename);
                String uploadFileName = file.getName();
                String fileType = params.get(Constants.REQUEST_PARAM_FILE_TYPE);
                if (TextUtils.isEmpty(fileType)) {
                    FileNameMap filenameMap = URLConnection.getFileNameMap();
                    String contentType = filenameMap.getContentTypeFor(filename);
                    if (contentType == null) {
                        fileType = "application/octet-stream";
                    }
                }
                res.append(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\nContent-Type: %s\r\n\r\n",
                        key, uploadFileName, fileType));
                results[1] = filename;
            }
        }
        results[0] = res.toString();
        return results;
    }
}
