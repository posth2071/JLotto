package com.JLotto.JLotto.Util;

import android.util.Log;

import com.JLotto.JLotto.BuildConfig;

public class Logger {

    public static final void d(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static final void w(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, message);
        }
    }

    public static final void i(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, message);
        }
    }

    public static final void v(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, message);
        }
    }

    public static final void e(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        }
    }
}
