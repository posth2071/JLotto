package com.example.maptest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.journeyapps.barcodescanner.CaptureActivity;

public class QRCodeActivity extends CaptureActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView title_view = new TextView(this);
        title_view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
        title_view.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        title_view.setPadding(100, 100, 0, 0);
        title_view.setTextColor(Color.parseColor("#006C93"));
        title_view.setTextSize(25);
        title_view.setText("동행복권 QR코드 인식");

        this.addContentView(title_view, layoutParams);
    }

}