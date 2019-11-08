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

    private int lastturn;

    //다이얼로그 생성자
    public DialogClass(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    //다이얼로그 리스너 설정
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

        lastturn = MainActivity.lastLottoinfo.getTurn();
        comment_tv.setText("원하는 회차 입력하세요 (last - " +lastturn+")");      //제일최근회차 가져오기
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Cancel_tv:
                cancel();
                break;
            case R.id.Search_tv:
                //입력한 회차 얻어오기
                String num = num_et.getText().toString();

                //마지막회차랑 입력한 회차 비교 (존재않는 회차일경우 검색x)
                if(Integer.parseInt(num) > lastturn || Integer.parseInt(num)==0){
                    Toast.makeText(getContext(),"미추첨 회차", Toast.LENGTH_SHORT).show();
                    num_et.setText("");
                }
                else {
                    //존재회차일경우 onPositiveClicked 메소드 실행
                    dialogListener.onPositiveClicked(num);
                    dismiss();      //다이얼로그 닫기
                }
                break;
        }
    }
}