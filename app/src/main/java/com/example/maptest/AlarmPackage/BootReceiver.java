package com.example.maptest.AlarmPackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.maptest.Activity.MainActivity;
import com.example.maptest.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 알람 실행
        //MainActivity.alarm_set(context, 1);
        if(PreferenceManager.getBoolean(context)){
            PreferenceManager.alarm_Setting(context,true);
        }

    }
}
