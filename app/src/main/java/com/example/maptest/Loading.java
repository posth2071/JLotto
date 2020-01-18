package com.example.maptest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;


// 메인액티비티 시작
public class Loading extends Activity{
    Animation loading_anim;
    ImageView loading_iv;
    LinearLayout loading_viewGroup;

    Handler handler;
    public static Context context;
    public static Activity activity;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            context = this;
            activity = this;
            super.onCreate(savedInstanceState);
            setContentView(R.layout.loading);

            loading_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loading_anim);

            //뷰그룹 (Linear)에 애니메이션 추가 (텍스트뷰,이미지뷰 한번에 사용)
            loading_viewGroup = findViewById(R.id.loading_viewgroup);
            loading_viewGroup.setAnimation(loading_anim);

            if (NetworkStatus.getConnectivity_Status(context) == 0) {
                NetworkStatus.Check_NetworkStatus(context, 1);
            } else {
                startLoading();
            }
            //startLoading();
        }

        /*
        private void Check_Network(){
            if (NetworkStatus.getConnectivity_Status(context) == 0) {
                Log.d("네트워크", "while문 진입");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("인터넷 에러");
                builder.setMessage("인터넷 연결 없음");
                builder.setCancelable(false);

                builder.setPositiveButton("재시도", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Check_Network();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 앱전부 종료
                        ActivityCompat.finishAffinity(MainActivity.activity);
                        System.exit(0);

                    }
                });
                Log.d("네트워크", "builder 세팅 완료");
                builder.show();
                Log.d("네트워크", "builder 띄우기");
            } else {
                finish();
            }
        }


         */
        private void startLoading() {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        }
}
