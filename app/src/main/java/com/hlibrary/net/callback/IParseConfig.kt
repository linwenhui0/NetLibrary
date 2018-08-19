package com.hlibrary.net.callback

import com.hlibrary.net.listener.IHttpAccessor

/**
 * @author linwenhui
 */
interface IParseConfig {

    /**
     * 网络请求类
     */
    fun getHttpAccessor(): IHttpAccessor

    /**
     * 网络数据解析类class
     */
    fun getParseClass(): Class<Any>

    /**
     * 网络数据解析类的实例方法
     */
    fun getParseInstanceMethod(): String

    /**
     * 网络数据解析类的实例方法是否带参数
     */
    fun parseHaveParam(): Boolean

    /**
     * 状态码key，多层以|分隔
     */
    fun getNetResponseCodeKey(): String

    /**
     * 状态码value
     */
    fun getNetResponseCodeSuc(): String

    /**
     * 错误码key，多层以|分隔
     */
    fun getNetResponseErrorMsgKey(): String

    /**
     * 返回实体数据key，多层以|分隔
     */
    fun getNetResponseData(): String

    /**
     * 返回实体数组数据key，多层以|分隔
     */
    fun getNetResponseArrayData(): String
}