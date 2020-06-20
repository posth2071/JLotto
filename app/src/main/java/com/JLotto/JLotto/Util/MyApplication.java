package com.JLotto.JLotto.Util;

import android.app.Application;
import android.content.ContentProvider;
import android.os.Build;

import com.facebook.stetho.Stetho;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Stetho 설정
        Stetho.initializeWithDefaults(this);
    }
}
