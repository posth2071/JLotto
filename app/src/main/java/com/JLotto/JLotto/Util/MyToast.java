package com.JLotto.JLotto.Util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.JLotto.JLotto.Activity.MainActivity;
import com.JLotto.JLotto.R;

// Custom Toast Class - 싱글톤 구현 (SingleTon)
public class MyToast {

    private static MyToast ourInstance = null; // 싱글톤 구현을 위한 private Instance
    private LinearLayout numsLayout = null;
    private ImageView[] nums = new ImageView[6];
    private TextView text = null;
    private Toast toast = null;

    private MyToast(Context context) {
        if (ourInstance == null) {
            LayoutInflater inflater = LayoutInflater.from(context);

            // RootView
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup)((AppCompatActivity)context).findViewById(R.id.toast_layout), false);

            // TextView 설정
            text = layout.findViewById(R.id.toast_text);
            text.setText("Toast 테스트");
            text.setTextSize(18);

            // Toast 인스턴스 설정
            toast = new Toast(context);
            toast.setGravity(Gravity.CENTER, 0, 0); // 보여줄 위치, 중앙기준 x [0] y[0]
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);  // Toast - RootView 연결

            // ImageViews RootLayout
            numsLayout = layout.findViewById(R.id.toast_num_Layout);

            // ImageViews
            Resources res = context.getResources();
            for (int i = 0; i < nums.length; i++) {
                int id = res.getIdentifier("toast_num"+ (i+1), "id", context.getPackageName());
                nums[i] = layout.findViewById(id);
            }
        }
    }

    // 일반적인 Toast
    public static void makeText(Context context, String msg) {
        // 싱글톤 구현위한 null Check, private 생성자로 외부에서 생성자 호출 불가
        if (ourInstance == null) {
            ourInstance = new MyToast(context);
        }
        ourInstance.toastsetting(TOAST_STATE.BASIC, msg);
    }

    // 로또 번호까지 출력하는 Toast
    public static void makeText(Context context, String msg, String numset) {
        // 싱글톤 구현위한 null Check, private 생성자로 외부에서 생성자 호출 불가
        if (ourInstance == null) {
            ourInstance = new MyToast(context);
        }

        String[] nums = numset.split(",");

        // 6개의 로또번호 IamgeView 세팅
        for (int i = 0; i < ourInstance.nums.length; i++) {
            ourInstance.nums[i].setImageResource(MainActivity.num_ID[Integer.parseInt(nums[i])]);
        }

        ourInstance.toastsetting(TOAST_STATE.NUMBER, msg);
    }

    private void toastsetting(TOAST_STATE state, String msg) {
        switch (state) {
            case BASIC:
                ourInstance.numsLayout.setVisibility(View.GONE);
                break;
            case NUMBER:
                ourInstance.numsLayout.setVisibility(View.VISIBLE);
                break;
        }
        ourInstance.text.setText(msg);  // TextView 보여줄 msg 설정
        ourInstance.toast.show();       // Toast 인스턴스 띄우기

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 700);
    }
}

enum TOAST_STATE {
    BASIC, NUMBER
}