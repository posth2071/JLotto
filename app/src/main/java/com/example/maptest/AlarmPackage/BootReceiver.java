package com.example.maptest.AlarmPackage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.maptest.MainActivity;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 알람 실행
        MainActivity.alarm_set(context, 1);
    }
}
