package com.example.chyl411.mvptest;

import android.app.Application;
import android.content.Context;

/**
 * Created by chyl411 on 2017/12/12.
 */

public class MyApplication extends Application {
    private static Context context_instance;

    @Override
    public void onCreate() {
        super.onCreate();

        context_instance = this.getApplicationContext();
    }

    public static Context getAppContext()
    {
        return context_instance;
    }
}
