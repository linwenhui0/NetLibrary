package com.hlibrary.net.model;


import com.hlibrary.net.listener.ProgressListener;

import java.io.File;


public class FileRequest extends Request {

	private String fileType;
	private ProgressListener listener;

	public FileRequest(String key, String value) {
		super(key, value);
	}

	/**
	 * @param fileType
	 *            建议：可以自己去网络上找对应的Content-Type值 <br>
	 *            上传图片：image/png<br>
	 *            上传音乐文件：audio/mp3<br>
	 *            上传视频文件：video/mpeg4
	 */
	public FileRequest(String key, String value, String fileType) {
		this(key, value);
		this.fileType = fileType;
	}

	/**
	 * @param fileType
	 *            建议：可以自己去网络上找对应的Content-Type值 <br>
	 *            上传图片：image/png<br>
	 *            上传音乐文件：audio/mp3<br>
	 *            上传视频文件：video/mpeg4
	 */
	public FileRequest(String key, File file, String fileType) {
		this(key, file.getAbsoluteFile().toString(), fileType);
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

	public FileRequest setOnProgressListener(ProgressListener listener) {
		this.listener = listener;
		return this;
	}

	public ProgressListener getProgressListener() {
		return listener;
	}

}
