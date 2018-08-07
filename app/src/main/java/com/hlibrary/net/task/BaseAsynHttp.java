package com.hlibrary.net.task;


import android.content.Context;
import android.text.TextUtils;

import com.hlibrary.net.callback.IParseCallback;
import com.hlibrary.net.callback.IResultErrorCallback;
import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.parse.CommonParse;

import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步任务开网络请求
 */
public abstract class BaseAsynHttp<T extends IResultErrorCallback> {

    protected IHttpAccessor accessor;
    protected HttpConfig httpConfig;
    protected T callback;
    protected IParseCallback parseCallback;
    public static boolean debug = false;

    protected static ExecutorService threadPool = Executors.newFixedThreadPool(30);

    /**
     * 构造函数
     *
     * @param httpConfig 网络请求参数
     */
    public BaseAsynHttp(HttpConfig httpConfig, IHttpAccessor accessor) {
        this.httpConfig = httpConfig;
        this.accessor = accessor;
        initParseCallback();
    }

    protected void initParseCallback() {
        Context context = httpConfig.getContext();
        try {
            Class cls = Class.forName(context.getPackageName() + ".BuildConfig");
            //判断是否自定义了解析规则类
            try {
                Field field = null;
                try {
                    field = cls.getField("KEY_CUSTOM_PARSE_JSON");
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                if (field != null) {
                    String parseJson = (String) field.get(null);
                    JSONObject parse = new JSONObject(parseJson);
                    String clsNamaeString = parse.optString("CLS");
                    if (TextUtils.isEmpty(clsNamaeString)) {
                        parseCallback = CommonParse.Companion.getInstance(context);
                        return;
                    }
                    String constructorString = parse.optString("CONSTRUCTOR");
                    String instanceMethodString = parse.optString("INSTANCE_METHOD");
                    boolean needInstanceParams = parse.optBoolean("NEED_INSTANCE_PARAMS", false);
                    Class parseCls = Class.forName(clsNamaeString);
                    Constructor parseConstructor = null;
                    Method parseMethod = null;
                    if (needInstanceParams) {
                        if (TextUtils.isEmpty(constructorString)) {
                            parseMethod = parseCls.getMethod(instanceMethodString, Context.class);
                        } else {
                            parseConstructor = parseCls.getConstructor(Context.class);
                        }
                    } else {
                        if (TextUtils.isEmpty(constructorString)) {
                            parseMethod = parseCls.getMethod(instanceMethodString);
                        } else {
                            parseConstructor = parseCls.getConstructor();
                        }
                    }

                    if (parseConstructor != null) {
                        if (needInstanceParams) {
                            parseCallback = (IParseCallback) parseConstructor.newInstance(context);
                        } else {
                            parseCallback = (IParseCallback) parseConstructor.newInstance();
                        }
                    } else if (parseMethod != null) {
                        if (needInstanceParams) {
                            parseCallback = (IParseCallback) parseMethod.invoke(null, context);
                        } else {
                            parseCallback = (IParseCallback) parseMethod.invoke(null);
                        }
                    }
                } else {
                    parseCallback = CommonParse.Companion.getInstance(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setCallback(T callback) {
        this.callback = callback;
    }

    public void setParseCallback(IParseCallback parseCallback) {
        this.parseCallback = parseCallback;
    }

    /**
     * 设置请求参数
     *
     * @param key
     * @param value
     */
    public BaseAsynHttp<T> put(String key, String value) {
        httpConfig.putParam(key, value);
        return this;
    }


}
