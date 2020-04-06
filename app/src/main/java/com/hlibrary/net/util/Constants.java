package com.hlibrary.net.util;

/**
 * @author linwenhui
 */
public final class Constants {

    public static final String CANCEL = "请求中断";
    public static final String COOKIE = "set-cookie";

    public static final String METHOD = "METHOD";
    public static final int GET = 1;
    public static final int POST = 2;
    public static final String GZIP = "gzip";

    public static final String REQUEST_PARAM_FILE_KEY = "REQUEST_PARAM_FILE_KEY";
    public static final String REQUEST_PARAM_FILE_NAME = "REQUEST_PARAM_FILE_NAME";
    /**
     * @param fileType 建议：可以自己去网络上找对应的Content-Type值 <br>
     * 上传图片：image/png<br>
     * 上传音乐文件：audio/mp3<br>
     * 上传视频文件：video/mpeg4
     */
    public static final String REQUEST_PARAM_FILE_TYPE = "REQUEST_PARAM_FILE_TYPE";
    public static final String REQUEST_PARAM_FILE_SEPARATE = "|";

    public static final String RESULT_START_JSON_OBJECT_FLAG = "{";
    public static final String RESULT_START_JSON_ARRAY_FLAG = "[";

    /**
     * 网络语法解析配置方法
     */
    public static final String NET_PARSE_HTTP_ACCESSOR = "getHttpAccessor";
    /**
     * 网络数据解析类的方法名
     */
    public static final String NET_PARSE_CLASS_KEY = "getParseClass";
    /**
     * 网络数据解析类实例的方法名
     */
    public static final String NET_PARSE_INSTANCE_METHOD_KEY = "getParseInstanceMethod";
    /**
     * 网络数据解析类实例方法/构造是否带参数的方法名
     */
    public static final String NET_PARSE_HAVE_PARAM_KEY = "parseHaveParam";
    /**
     * 状态码key的方法名，多层以|分隔
     */
    public static final String NET_RESPONSE_CODE_KEY = "getNetResponseCodeKey";
    /**
     * 状态码value的方法名
     */
    public static final String NET_RESPONSE_CODE_SUC = "getNetResponseCodeSuc";
    /**
     * 错误码key的方法名，多层以|分隔
     */
    public static final String NET_RESPONSE_ERROR_MSG = "getNetResponseErrorMsgKey";
    /**
     * 返回实体数据key的方法名，多层以|分隔
     */
    public static final String NET_RESPONSE_DATA = "getNetResponseData";
    /**
     * 返回实体数组数据key的方法名，多层以|分隔
     */
    public static final String NET_RESPONSE_ARRAY_DATA = "getNetResponseArrayData";
    /**
     * 参数分隔字符|分隔
     */
    public static final String NET_RESPONSE_SEPARATE = "|";


    private Constants() {
    }
}
