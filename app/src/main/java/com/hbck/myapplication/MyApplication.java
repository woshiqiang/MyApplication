package com.hbck.myapplication;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * @Date 2018-11-06.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
