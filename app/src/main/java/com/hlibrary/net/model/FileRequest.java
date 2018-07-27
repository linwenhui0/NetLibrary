package com.hlibrary.net.model;


import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;


public class FileRequest {

    private String key;
    private String value;
    private String fileType;

    public FileRequest(String key, String value) {
        this(key, value, getMimeType(value));
    }

    /**
     * @param fileType 建议：可以自己去网络上找对应的Content-Type值 <br>
     *                 上传图片：image/png<br>
     *                 上传音乐文件：audio/mp3<br>
     *                 上传视频文件：video/mpeg4
     */
    FileRequest(String key, String value, String fileType) {
        this.key = key;
        this.value = value;
        this.fileType = fileType;
    }


    public FileRequest(String key, File file) {
        this(key, file.getAbsoluteFile().toString());
    }

    /**
     * Content-Type的值
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Content-Type的值
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获取文件MimeType
     *
     * @param filename
     * @return
     */
    private static String getMimeType(String filename) {
        FileNameMap filenameMap = URLConnection.getFileNameMap();
        String contentType = filenameMap.getContentTypeFor(filename);
        if (contentType == null) {
            contentType = "application/octet-stream"; //* exe,所有的可执行程序
        }
        return contentType;
    }
}
