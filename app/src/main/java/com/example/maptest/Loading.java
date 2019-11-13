package com.example.maptest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Loading extends Activity{
    Animation loading_anim;
    ImageView loading_iv;
    LinearLayout loading_viewGroup;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.loading);

            loading_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loading_anim);

            //뷰그룹 (Linear)에 애니메이션 추가 (텍스트뷰,이미지뷰 한번에 사용)
            loading_viewGroup = findViewById(R.id.loading_viewgroup);
            loading_viewGroup.setAnimation(loading_anim);

            startLoading();
        }
        private void startLoading() {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        }
}
