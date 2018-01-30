package com.liujian.chart;

import android.app.Application;

/**
 * @author : liujian
 * @since : 2018/1/21
 */

public class BaseApplication extends Application {
    public static Application INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
