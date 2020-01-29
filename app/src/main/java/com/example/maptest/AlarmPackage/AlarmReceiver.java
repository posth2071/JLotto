package com.example.maptest.AlarmPackage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.maptest.DataBase.DBOpenHelper;
import com.example.maptest.DataBase.DBinfo;
import com.example.maptest.AsyncTask.LottoParsingInfo;
import com.example.maptest.Activity.MainActivity;
import com.example.maptest.PreferenceManager;
import com.example.maptest.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String NOTI_CHANNEL_ID = "10000";
    Context context;

    // 지정한 Alarm 시간이 되면 실행되는 Receiver 콜백메서드
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        // Notification 실행 메서드
        start_Notification();

        // 알람 재설정 (반복)
        PreferenceManager.alarm_Setting(context, true);
        //MainActivity.alarm_set(context, 1);
    }


    // Notification 알림 상자 설정
    public void start_Notification(){
        // 동행복권 마지막회차 파싱을 위해 Task 객체 선언
        Alarm_TaskClass alarm_taskClass = new Alarm_TaskClass();
        // 파싱결과를 저장할 LottoParsingInfo 객체 선언
        LottoParsingInfo lottoParsingInfo = new LottoParsingInfo();
        try {
            // 파싱 시작, get() 함수로 파싱이 완료될때까지 대기
            lottoParsingInfo = alarm_taskClass.execute().get();
            Log.d("알람", "파싱 결과 \n\t"
                    + lottoParsingInfo.getTurn() + "회차, 추첨날짜 "
                    + lottoParsingInfo.getDate() + ", 당첨 번호 "
                    + Arrays.toString(lottoParsingInfo.getLottoInfo()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // 알림 클릭시 실행할 PendingIntent
        PendingIntent noti_pending = PendingIntent.getActivity(
                context,
                0,
                intent,     // 클릭시 띄워줄 Activity 설정한 Intent
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        // Notification 기본상태 Custom 레이아웃 연결 RemoteViews
        RemoteViews remote_normal = new RemoteViews(context.getPackageName(), R.layout.alarm_notification_normal);
        // Notification 확장상태 Custom 레이아웃 연결 RemoteViews
        RemoteViews remote_expand = new RemoteViews(context.getPackageName(), R.layout.alarm_notification_expanded);

        // RemoteViews 셋팅
        setting_RemoteViews(lottoParsingInfo, remote_normal, remote_expand);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTI_CHANNEL_ID)
                        .setAutoCancel(true)                                // 알림 클릭시 자동 제거
                        .setPriority(NotificationCompat.PRIORITY_MAX)       // 알림 우선순위
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)    // 알림 진동 설정 - 퍼미션 추가 필요
                        .setContentIntent(noti_pending)                     // 알림 클릭시 실행할 인텐트
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())        // 스타일 설정 - Custom 레이아웃
                        .setCustomContentView(remote_normal)                // 기본상태 레이아웃 설정
                        .setCustomBigContentView(remote_expand);            // 확장상태 레이아웃 설정

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 오레오 OREO API 26 이상부터는 채널 Chennel 필요
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d("알람", "SDK레벨 > 오레오(O) 이상");
            // 오레오 Oreo 이상버전에서 Mipmap 사용시, 시스템 UI 에러발생
            builder.setSmallIcon(R.drawable.pack1);
            // Notification Channel 생성
            notificationManager.createNotificationChannel(new NotificationChannel(NOTI_CHANNEL_ID, "JLotto 채널", NotificationManager.IMPORTANCE_HIGH));
        } else {
            // 오레오 Oreo 이하버전에서 Mipmap 사용안할시 Couldn't create Icon: StatusBarIcon 에러 발생
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }
        // Notification 알림 생성
        notificationManager.notify(((int) System.currentTimeMillis()/1000), builder.build());
    }


    // Custom Layout 세팅 - RemoteViews
    private void setting_RemoteViews(LottoParsingInfo lottoParsingInfo, RemoteViews remote_normal, RemoteViews remote_expand){
        int turn = lottoParsingInfo.getTurn(); // 조회한 회차정보
        String[] numset = lottoParsingInfo.getLottoInfo();  // 당첨번호 저장할 String[] 배열
        String result_score = "";                // 당첨등수 저장할 String 변수
        String result_money = "";                // 당첨금액 저장할 String 변수

        int[] imgId = new int[45];             // 이미지 리소스(1~45) ID저장할 Int배열
        Resources res = context.getResources();
        for (int i = 1; i < 46; i++) {                //45번반복
            int stringId = res.getIdentifier("num" + i, "string", context.getPackageName());     // name에 해당하는 값의 위치 가져옴, 2131492892반환
            String stringId2 = res.getString(stringId);
            imgId[i - 1] = res.getIdentifier(stringId2, "drawable", context.getPackageName());
        }

        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        // DB에서 해당회차와 동일한 번호들 가져오기
        ArrayList<DBinfo> db_List = dbOpenHelper.selectDB(turn);
        // 가져온 목록이 있으면 == db_List가 비어있지않으면 실행
        if(!(db_List.isEmpty())){
            for(DBinfo dBinfo : db_List){
                // 당첨결과 비교 (비교할 숫자세트, 비교대상(당첨번호))
                String[] result_str = MainActivity.checkResult(dBinfo.getNumset(), lottoParsingInfo);
                // 결과가 미당첨이 아닐경우 해당 당첨 등수 추가
                if(result_str[0].contains("미당첨") == false){
                    // 당첨등수
                    result_score += result_str[0]+ " 당첨\n";
                    // 당첨금액
                    result_money += lottoParsingInfo.getSubInfo().get((result_str[0].charAt(0)-'0')).get(3) +"\n";
                }
            }
        }

        // 결과가 모두 미당첨일 경우
        if(result_score.isEmpty()){
            result_score += "미당첨";
        } else {
            // 미당첨이 아닌경우 당첨등수 색상 변화하기 mainColor
            remote_expand.setTextColor(R.id.noti_expand_score, Color.parseColor("#006C93"));
        }

        remote_normal.setTextViewText(R.id.noti_normal_turn, turn + "회차");
        remote_expand.setTextViewText(R.id.noti_expand_turn, turn + "회차 당첨번호");
        remote_expand.setTextViewText(R.id.noti_expand_score, result_score);            // 확장상태 - 당첨등수 설정
        remote_expand.setTextViewText(R.id.noti_expand_money, result_money);            // 확장상태 - 당첨금액 설정

        for(int i=1; i<8; i++){
            // 당첨번호 저장할 int 변수
            int result_Value = Integer.parseInt(numset[i-1]);
            // Notification_Normal - 7개의 imageView 각각 해당 당첨번호에 맞는 Image 세팅
            int imageView_normal = res.getIdentifier("noti_normal_"+i, "id", context.getPackageName());
            remote_normal.setInt(imageView_normal, "setImageResource", imgId[result_Value-1]);
            // Notification_Expand - 7개의 imageView 각각 해당 당첨번호에 맞는 Image 세팅
            int imageView_expand = res.getIdentifier("noti_expand_"+i, "id", context.getPackageName());
            remote_expand.setInt(imageView_expand, "setImageResource", imgId[result_Value-1]);
        }
    }
}
