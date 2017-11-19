package com.hlibrary.net.model;

/**
 * 网络请求实体类
 */
public class Request {

	private String key;
	private String value;

	public Request(String key, String value) {
		this.key = key;
		this.value = value;
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

}
