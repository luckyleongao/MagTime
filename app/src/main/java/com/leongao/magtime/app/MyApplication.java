package com.leongao.magtime.app;

import android.app.Application;
import android.content.Context;


import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;

import com.leongao.magtime.BuildConfig;

import timber.log.Timber;

public class MyApplication extends Application {
    private static Context applicationContext;
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // 初始化DataStore Preferences
//        DataStoreSingleton dataStoreSingleton = DataStoreSingleton.getInstance();
//        if (null == dataStoreSingleton.getDataStore()) {
//            dataStoreSingleton.setDataStore(
//                    new RxPreferenceDataStoreBuilder(this, "magtime_datastore").build());
//        }
    }

    public static Context getAppContext() {
        return applicationContext;
    }
}
