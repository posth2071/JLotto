package com.example.maptest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.maptest.Activity.MainActivity;
import com.example.maptest.AlarmPackage.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PreferenceManager {
    private static final String PREFERENCES_NAME = "Setting";
    private static final String KEY = "Alarm_State";
    private static final boolean DEFAULT_VALUE_BOOLEAN = true;

    private static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void setBoolean(Context context, boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY, value);
        editor.commit();

        alarm_Setting(context, value);
    }

    public static boolean getBoolean(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean state = sharedPreferences.getBoolean(KEY, DEFAULT_VALUE_BOOLEAN);
        return state;
    }

    // 알람이 현재 존재중인지 확인 -> 미존재 일경우 알람 설정값을 확인 후 알람 설정
    public static void first_Check(Context context){
        Intent alarm_Intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pending_Intent = PendingIntent.getBroadcast(context, MainActivity.ALARM_ID,alarm_Intent, PendingIntent.FLAG_NO_CREATE);
        // 알람이 존재하지않으면 getBroadcast null 반환
        if(pending_Intent == null){
            if(PreferenceManager.getBoolean(context)){
                PreferenceManager.alarm_Setting(context,true);
            }
        }
    }


    public static void alarm_Setting(Context context, boolean state){
        // int action, 1 = 알람 Start / 2 = 알람 Cancel
        String log_Text = " \n\t알람 %s \n\t현재시간 %s \n\t설정시간 %s";

        // 현재 시간얻기
        Calendar cal = Calendar.getInstance();
        Date dateTest = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일, HH시 mm분 ss초", Locale.getDefault());

        Intent alarm_Intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pending_Intent = PendingIntent.getBroadcast(context, MainActivity.ALARM_ID,alarm_Intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm_Manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // if(state) == true 알람 설정 (매주 토요일 21시)
        if(state){
            // 현재 요일얻기 - 1일 2월 3화 4수 5목 6금 7토
            int day_week = cal.get(Calendar.DAY_OF_WEEK);
            // 토요일이라면 21시 기준으로 구분 (21시 이전, 오늘 알람 실행 / 21시 이후, 다음주 알람 실행)
            if(day_week == 7){
                if(cal.get(Calendar.HOUR_OF_DAY) >= 21){
                    cal.add(Calendar.DAY_OF_MONTH,7); // 일주일 뒤 설정, add()는 마지막 Day가 넘어갈시 월단위 추가 계산
                }
                // 현재 요일이 토요일이 아닌경우, 알람 토요일로 설정
            } else {
                cal.add(Calendar.DAY_OF_MONTH, 7-day_week);
            }

            // 시간 설정 - 21시 정각 (9시)
            cal.set(Calendar.HOUR_OF_DAY, 21);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            //Calendar test_cal = Calendar.getInstance();
            //test_cal.add(Calendar.SECOND, 5);

            // 디바이스 API레벨 별로 분기 실행 (메소드 차이 set() / setExact() / setExactAllowWhileidle() )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Date test_date = test_cal.getTime();
                //String test_Text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일, HH시 mm분 ss초", Locale.getDefault()).format(cal.getTime());
                //alarm_Manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, test_cal.getTimeInMillis(), pending_Intent);

                String test_Text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일, HH시 mm분 ss초", Locale.getDefault()).format(cal.getTime());
                alarm_Manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending_Intent);
                Log.d("알람", " \n\t알람시작 \t API23 <= VERSION (마쉬멜로우 6.0 이상) \n\t"+ test_Text);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarm_Manager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending_Intent);
                Log.d("알람", " \n\t알람시작 \t API19 <= VERSION < API23 (KitKat 4.4 이상 - 마쉬멜로우 6.0 미만)");
            } else {
                alarm_Manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending_Intent);
                Log.d("알람", " \n\t알람시작 \t  VERSION < API19 (KitKat 4.4 미만)");
            }

            log_Text = String.format(
                    log_Text,
                    "생성",
                    dateFormat.format(dateTest),
                    dateFormat.format(cal.getTime())
            );

            Toast.makeText(context,
                    new SimpleDateFormat("알람 설정\nMM월 dd일 EE요일, HH시 mm분", Locale.getDefault()).format(cal.getTime()),
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            // if(state) == false 알람 해제
            alarm_Manager.cancel(pending_Intent);

            log_Text = String.format(
                    log_Text,
                    "해제",
                    dateFormat.format(dateTest),
                    dateFormat.format(dateTest)
            );
            Toast.makeText(context, "알람 해제", Toast.LENGTH_SHORT).show();
        }
        // 알람설정, 현재시간, 알람 설정시간 보여주기
        Log.d("알람", log_Text);
    }
    public static void remove(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY);
        editor.commit();
    }

    public static void clear(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
