package com.delta.joydeep.flickr;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    public static boolean ENABLE_LOGGING;
    private static App mInstance;

    public static App getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ENABLE_LOGGING = BuildConfig.DEBUG;

        mInstance = this;
    }
}