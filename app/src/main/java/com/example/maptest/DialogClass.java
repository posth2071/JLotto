package com.example.maptest;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

/**
 * Created by charlie on 2017. 8. 18..
 */

public class DialogClass extends Dialog implements View.OnClickListener{
    private MyDialogListener dialogListener;

    private static final int LAYOUT = R.layout.dialog_activity;

    private Context mContext = null;

    private EditText num_et;

    private TextView search_tv, cancel_tv, comment_tv;

    private String last;

    public DialogClass(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public void setDialogListener(MyDialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        num_et = (EditText) findViewById(R.id.Dialog_et);

        cancel_tv = (TextView) findViewById(R.id.Cancel_tv);
        search_tv = (TextView) findViewById(R.id.Search_tv);
        comment_tv = (TextView) findViewById(R.id.comment_tv);

        cancel_tv.setOnClickListener(this);
        search_tv.setOnClickListener(this);

        last = MainActivity.lastSet[0];
        comment_tv.setText("원하는 회차 입력하세요 (last - " +last+")");      //MainActivity클래스 내 array[0]문자열 얻기
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Cancel_tv:
                cancel();
                break;
            case R.id.Search_tv:
                String num = num_et.getText().toString();
                last = last.replace("회","");
                last.trim();
                // 0회차나 마지막회차보다 큰 횟수일경우 파싱 미실행
                if(Integer.parseInt(num)> Integer.parseInt(last) || Integer.parseInt(num)==0){
                    Toast.makeText(getContext(),"미추첨 회차", Toast.LENGTH_SHORT).show();
                    num_et.setText("");
                }
                else {
                    dialogListener.onPositiveClicked(num);
                    dismiss();
                }
                break;
        }
    }
}