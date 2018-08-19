package com.hlibrary.net.task;


import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.hlibrary.net.callback.IParseCallback;
import com.hlibrary.net.callback.IParseConfig;
import com.hlibrary.net.callback.IResultErrorCallback;
import com.hlibrary.net.http.common.SimpleHttpAccessor;
import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.parse.CommonParse;
import com.hlibrary.net.util.Constants;
import com.hlibrary.util.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hlibrary.net.util.Constants.debug;

/**
 * 异步任务开网络请求
 *
 * @author linwenhui
 */
public abstract class BaseAsynHttp<T extends IResultErrorCallback> {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    IHttpAccessor accessor;
    int method;
    protected Map<String, String> params;
    boolean saveCookie;
    protected T callback;
    protected IParseCallback parseCallback;

    static ThreadPoolExecutor threadPool;
    private static final BlockingQueue<Runnable> S_POOL_WORK_QUEUE =
            new LinkedBlockingQueue<>(128);
    private static final ThreadFactory S_THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                S_POOL_WORK_QUEUE, S_THREAD_FACTORY);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        threadPool = threadPoolExecutor;

    }

    /**
     * 构造函数
     *
     * @param method     请求类型
     * @param params     请求参数
     * @param saveCookie 是否保存cookie
     */
    public BaseAsynHttp(Context context, int method, Map<String, String> params, boolean saveCookie) {
        this.method = method;
        this.params = params;
        this.saveCookie = saveCookie;

        if (context.getApplicationContext() instanceof Application) {
            initParseCallback((Application) context.getApplicationContext());
        } else {
            parseCallback = CommonParse.Companion.getInstance(context);
            this.accessor = new SimpleHttpAccessor(context);
        }
    }

    private void initParseCallback(Application application) {
        if (application instanceof IParseConfig) {
            IParseConfig parseConfig = (IParseConfig) application;
            initApplicationParseCallback(parseConfig);
        } else {
            initDefaultParseCallback(application);
        }
        if (this.accessor == null) {
            this.accessor = new SimpleHttpAccessor(application);
        }
        if (parseCallback == null) {
            if (debug) {
                Logger.getInstance().defaultTagD("解析失败使用，默认解析类");
            }
            parseCallback = CommonParse.Companion.getInstance(application);
        }

    }

    private void initApplicationParseCallback(IParseConfig parseConfig) {
        Class parseCls = parseConfig.getParseClass();
        if (parseCls != null) {
            boolean haveParams = parseConfig.parseHaveParam();
            String instanceMethodText = parseConfig.getParseInstanceMethod();
            if (!TextUtils.isEmpty(instanceMethodText)) {
                if (haveParams) {
                    try {
                        Method method = parseCls.getMethod(instanceMethodText, Context.class);
                        parseCallback = (IParseCallback) method.invoke(null, parseConfig);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Method method = parseCls.getMethod(instanceMethodText);
                        parseCallback = (IParseCallback) method.invoke(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (haveParams) {
                    try {
                        Constructor constructor = parseCls.getConstructor(Context.class);
                        parseCallback = (IParseCallback) constructor.newInstance(parseConfig);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        parseCallback = (IParseCallback) parseCls.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.accessor = parseConfig.getHttpAccessor();
    }

    private void initDefaultParseCallback(Application application) {
        Class cls = application.getClass();
        try {
            Method method = cls.getDeclaredMethod(Constants.NET_PARSE_CLASS_KEY);
            Class parseCls = (Class) method.invoke(application);
            if (parseCls != null) {
                method = cls.getDeclaredMethod(Constants.NET_PARSE_INSTANCE_METHOD_KEY);
                String instanceMethodText = (String) method.invoke(application);
                method = cls.getDeclaredMethod(Constants.NET_PARSE_HAVE_PARAM_KEY);
                boolean haveParams = (boolean) method.invoke(application);
                if (!TextUtils.isEmpty(instanceMethodText)) {
                    if (haveParams) {
                        method = parseCls.getMethod(instanceMethodText, Context.class);
                        parseCallback = (IParseCallback) method.invoke(null, application);
                    } else {
                        method = parseCls.getMethod(instanceMethodText);
                        parseCallback = (IParseCallback) method.invoke(null);
                    }
                } else {
                    if (haveParams) {
                        Constructor constructor = parseCls.getConstructor(Context.class);
                        parseCallback = (IParseCallback) constructor.newInstance(application);
                    } else {
                        parseCallback = (IParseCallback) parseCls.newInstance();
                    }
                }
            }
            method = cls.getDeclaredMethod(Constants.NET_PARSE_HTTP_ACCESSOR);
            accessor = (IHttpAccessor) method.invoke(application);

        } catch (Exception e) {
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
        if (params == null) {
            params = new ArrayMap<>();
        }
        params.put(key, value);
        return this;
    }


}
