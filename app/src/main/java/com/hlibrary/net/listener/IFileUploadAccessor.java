package com.hlibrary.net.listener;


import com.hlibrary.net.model.Requests;
import com.hlibrary.net.model.Respond;

public interface IFileUploadAccessor {

	/**
	 * 上传文件
	 * 
	 * @param urlStr
	 *            上传地址
	 * @param params
	 *            上传的参数
	 * @param uploadConnectTimeout
	 *            socket连接超时时间
	 * @param uploadReadTimeout
	 *            连接超时时间
	 * @return
	 */
	public Respond doUploadFile(String urlStr, Requests params,
								int uploadConnectTimeout, int uploadReadTimeout);

	/**
	 * 中断上传
	 */
	public void abort();
}
