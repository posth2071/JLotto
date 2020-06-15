package com.JLotto.JLotto.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.JLotto.JLotto.NetworkStatus;
import com.JLotto.JLotto.R;


// 메인액티비티 시작
public class Loading_Activity extends Activity{
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
                NetworkStatus.Check_NetworkStatus(context, 1, null);
            } else {
                startLoading();
            }
        }

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
