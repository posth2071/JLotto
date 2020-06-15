package com.JLotto.JLotto.AlarmPackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.JLotto.JLotto.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 알람 실행
        if(PreferenceManager.getBoolean(context)){
            PreferenceManager.alarm_Setting(context,true);
        }
    }
}
