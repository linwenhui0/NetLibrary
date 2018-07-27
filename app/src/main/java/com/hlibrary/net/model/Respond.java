package com.hlibrary.net.model;

/**
 * 网络请求返回数据类型
 * 
 * @since 2013-10-28
 */
public class Respond {

	public final static String URL_ERROR = "url 解析出错";
	public final static String NO_RESPOND = "服务器未响应";
	public final static String TIME_OUT = "连接超时";
	public final static String NET_ERROR = "网络节点又出故障，换个网络试试";

	// 失败
	public final static int FALSE = -1;
	// 成功
	public final static int SUCCEE = 0;

	// 网络请求返回状态
	private int code;
	// 返回数据
	private String data;
	private String errorData;

	/**
	 * @param code
	 *            返回状态码 0 成功 其它各种失败
	 * @param data
	 *            返回的数据
	 */
	public Respond(int code, String data) {
		this.code = code;
		this.data = data;
	}

	/**
	 * 网络请求返回状态码
	 * 
	 * @return
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 网络请求标记
	 * 
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * 返回数据
	 * 
	 * @return
	 */
	public String getData() {
		return data;
	}

	/**
	 * 返回数据
	 * 
	 * @param data
	 */
	public void setData(String data) {
		this.data = data;
	}

	public String getErrorData() {
		return errorData;
	}

	public void setErrorData(String errorData) {
		this.errorData = errorData;
	}
}
