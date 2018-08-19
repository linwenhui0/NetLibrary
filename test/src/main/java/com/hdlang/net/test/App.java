package com.hdlang.net.test;

import android.app.Application;

import com.hlibrary.net.callback.IFileUploadCallback;
import com.hlibrary.net.callback.IParseConfig;
import com.hlibrary.net.http.okhttp.OKHttpAccessor;
import com.hlibrary.net.listener.IHttpAccessor;

import org.jetbrains.annotations.NotNull;

public class App extends Application implements IParseConfig{

    @NotNull
    @Override
    public String getNetResponseCodeKey() {
        return "desc";
    }

    @NotNull
    @Override
    public String getNetResponseCodeSuc() {
        return "OK";
    }

    @NotNull
    @Override
    public String getNetResponseErrorMsgKey() {
        return "errorMsg";
    }

    @NotNull
    @Override
    public String getNetResponseData() {
        return "data";
    }

    @NotNull
    @Override
    public String getNetResponseArrayData() {
        return "data";
    }

    @NotNull
    @Override
    public Class getParseClass() {
        return null;
    }

    @NotNull
    @Override
    public String getParseInstanceMethod() {
        return "";
    }

    @Override
    public boolean parseHaveParam() {
        return true;
    }

    @NotNull
    @Override
    public IHttpAccessor getHttpAccessor() {
        return new OKHttpAccessor<IFileUploadCallback>(this);
    }
}
