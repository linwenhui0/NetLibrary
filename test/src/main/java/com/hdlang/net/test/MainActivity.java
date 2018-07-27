package com.hdlang.net.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hlibrary.net.callback.IResultCallback;
import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.http.okhttp.OKHttpAccessor;
import com.hlibrary.net.task.NormalAsynHttp;
import com.hlibrary.util.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HttpConfig httpConfig = new HttpConfig(this);
        NormalAsynHttp<String, IResultCallback<String>> normalAsynHttp = new NormalAsynHttp<>(httpConfig, new OKHttpAccessor<>(this), String.class);
        normalAsynHttp.setCallback(new IResultCallback<String>() {
            @Override
            public void onSuccee(String s) {
                Logger.getInstance().defaultTagD(s);
            }

            @Override
            public void onError(String msg) {

            }

            @Override
            public void onEmpty() {

            }
        });
        normalAsynHttp.put("city", "北京");
        normalAsynHttp.doPost("http://wthrcdn.etouch.cn/weather_mini");
    }
}
