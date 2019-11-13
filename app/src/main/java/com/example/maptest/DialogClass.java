package com.example.maptest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by charlie on 2017. 8. 18..
 */

public class DialogClass extends Dialog implements View.OnClickListener {
    private MyDialogListener dialogListener;

    private Context mContext = null;

    private static final int LAYOUT_SEARCH = R.layout.dialog_activity;      //회차검색 대화상자 LayoutID
    private static final int LAYOUT_DBLIST = R.layout.dialog_dblist;        //DB리스트설정 대화상자 LayoutID

    //다이얼로그 타입구분용 int
    private int type;

    // 타입1 - 회차검색
    private EditText dlone_et;
    private TextView dlone_comment, dlone_search, dlone_cancel;
    private int lastturn;

    // 타입2 - 설정 타입
    private ExpandableListView dltwo_exlist;
    public static ExpandableAdapter dltwo_adapter;
    public static ArrayList<ArrayList<DBinfo>> dltwo_listitem = new ArrayList<>();
    private TextView dltwo_cancel, dltwo_check;
    private DBOpenHelper dbOpenHelper;


    //다이얼로그 생성자
    public DialogClass(@NonNull Context context, int type) {
        super(context);
        this.mContext = context;
        this.type = type;
    }

    //다이얼로그 리스너 설정
    public void setDialogListener(MyDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("다이얼로그", "onCreate 진입 type - " + type);
        switch (type) {
            case 1:
                dialog_set(1);          // 타입1 - 회차검색 대화상자 세팅
                break;
            case 2:
                dialog_set(2);          // 타입2 - DB리스트 대화상자 세팅
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.DLOne_Cancel:
                cancel();
                break;
            case R.id.DLOne_Search:
                //입력한 회차 얻어오기
                String num = dlone_et.getText().toString();

                //마지막회차랑 입력한 회차 비교 (존재않는 회차일경우 검색x)
                if (Integer.parseInt(num) > lastturn || Integer.parseInt(num) == 0) {
                    Toast.makeText(getContext(), "미추첨 회차", Toast.LENGTH_SHORT).show();
                    dlone_et.setText("");
                } else {
                    //존재회차일경우 onPositiveClicked 메소드 실행
                    dialogListener.onPositiveClicked(num);
                    dismiss();      //다이얼로그 닫기
                }
                break;
            case R.id.DLTwo_Cancel:
                cancel();
                break;
            case R.id.DLTwo_Check:
                dismiss();
                break;
        }
    }

    private void dialog_set(int type) {
        Log.d("다이얼로그", "dialog_set실행, type -" + type);
        switch (type) {
            // 타입1 설정
            case 1:
                Log.d("다이얼로그", "case 1 진입, type -" + type);
                setContentView(LAYOUT_SEARCH);

                dlone_et = (EditText) findViewById(R.id.DLOne_et);
                dlone_comment = (TextView) findViewById(R.id.DLOne_comment);

                dlone_cancel = (TextView) findViewById(R.id.DLOne_Cancel);
                dlone_cancel.setOnClickListener(this);

                dlone_search = (TextView) findViewById(R.id.DLOne_Search);
                dlone_search.setOnClickListener(this);

                lastturn = MainActivity.lastLottoinfo.getTurn();
                dlone_comment.setText("원하는 회차 입력하세요 (last - " + lastturn + ")");      //제일최근회차 가져오기
                break;
            // 타입2 설정
            case 2:
                Log.d("다이얼로그", "case 2 진입, type - " + type);
                setContentView(LAYOUT_DBLIST);

                dltwo_exlist = findViewById(R.id.DLTwo_ExList);

                dltwo_cancel = findViewById(R.id.DLTwo_Cancel);
                dltwo_cancel.setOnClickListener(this);
                dltwo_check = findViewById(R.id.DLTwo_Check);
                dltwo_check.setOnClickListener(this);

                dbOpenHelper = new DBOpenHelper(getContext());

                Log.d("다이얼로그", "dltwo_Listitem 생성 id/size - " + dltwo_listitem + ", " + dltwo_listitem.size());
                dltwo_listitem = dbOpenHelper.selectListAllDB();
                Log.d("다이얼로그", "dltwo_Listitem 데이터삽입 id/size - " + dltwo_listitem + ", " + dltwo_listitem.size());
                if (dltwo_listitem.size() > 0) {
                    Log.d("다이얼로그", "dltwo_adapter 생성 - " + dltwo_adapter);
                    dltwo_adapter = new ExpandableAdapter(getContext(), dltwo_listitem, this);
                    Log.d("다이얼로그", "dltwo_adapter 생성 - " + dltwo_adapter);
                    dltwo_exlist.setAdapter(dltwo_adapter);
                } else {
                    Toast.makeText(mContext, "DB저장 기록 없음", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
