package com.hdlang.net.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;

import com.hlibrary.net.callback.IResultCallback;
import com.hlibrary.net.task.NormalAsynHttp;
import com.hlibrary.net.util.Constants;
import com.hlibrary.util.Logger;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Map<String, String> params = new ArrayMap<>();
        params.put("city", "北京");
        NormalAsynHttp<String, IResultCallback<String>> normalAsynHttp = new NormalAsynHttp<>(getApplicationContext(), Constants.GET, params, false,  String.class);
        normalAsynHttp.setCallback(new IResultCallback<String>() {
            @Override
            public void onSuccee(String s) {
                time = System.currentTimeMillis() - time;
                Logger.getInstance().defaultTagD(s, "time = " + time);
            }

            @Override
            public void onError(String msg) {

            }

            @Override
            public void onEmpty() {

            }
        });
//        normalAsynHttp.put("city", "北京");
        time = System.currentTimeMillis();
        normalAsynHttp.doPost("http://wthrcdn.etouch.cn/weather_mini");
    }
}
