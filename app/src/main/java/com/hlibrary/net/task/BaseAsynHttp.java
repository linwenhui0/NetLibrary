package com.hlibrary.net.task;


import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.listener.IResult;
import com.hlibrary.net.listener.IResults;
import com.hlibrary.net.model.Request;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 异步任务开网络请求
 */
public abstract class BaseAsynHttp<T> {

	protected HttpConfig httpConfig;
	private IResult<T> result;
	private IResults<T> results;

	protected ExecutorService threadPool;

	/**
	 * 构造函数
	 * 
	 * @param httpConfig
	 *            网络请求参数
	 */
	public BaseAsynHttp(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	public IResult<T> getResult() {
		return result;
	}

	public void setResult(IResult<T> result) {
		this.result = result;
	}

	public void setResults(IResults<T> results) {
		this.results = results;
	}

	public IResults<T> getResults() {
		return results;
	}

	public void setThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}

	/**
	 * 设置请求参数
	 * 
	 * @param params
	 */
	public void puts(List<Request> params) {
		for (Request param : params) {
			httpConfig.getParams().put(param);
		}
	}

	/**
	 * 设置请求参数
	 * 
	 * @param param
	 */
	public BaseAsynHttp<T> put(Request param) {
		httpConfig.getParams().put(param);
		return this;
	}

}
