package com.hdlang.net.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;

import com.hdlang.net.test.entity.Weather;
import com.hlibrary.net.callback.IMulResultCallback;
import com.hlibrary.net.http.task.NetRequestManager;
import com.hlibrary.util.Logger;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Map<String, String> params = new ArrayMap<>();
        params.put("city", "北京");
        NetRequestManager netRequestManager = NetRequestManager.getInstance(this);
        time = System.currentTimeMillis();
        netRequestManager.executeRequest("http://wthrcdn.etouch.cn/weather_mini", params, Weather.class, new IMulResultCallback<Weather>() {

            @Override
            public void onError(String msg) {

            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onSuccee(List<Weather> t) {
                if (t != null) {
                    Logger.getInstance().defaultTagD(" == onSuccee == ", t.size(), " == ", t);
                }
            }
        });

    }
}
