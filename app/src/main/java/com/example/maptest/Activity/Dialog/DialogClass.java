package com.example.maptest.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.maptest.Activity.FragMent_Three.Map.MapNaver;
import com.example.maptest.Activity.Loading_Activity;
import com.example.maptest.Adapter.Expandable.ExpandableAdapter;
import com.example.maptest.DataBase.DBOpenHelper;
import com.example.maptest.DataBase.DBinfo;
import com.example.maptest.Activity.MainActivity;
import com.example.maptest.NetworkStatus;
import com.example.maptest.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by charlie on 2017. 8. 18..
 */

public class DialogClass extends Dialog implements View.OnClickListener {
    private MyDialogListener dialogListener;

    private Context mContext = null;

    private static final int LAYOUT_SEARCH = R.layout.dialog_activity;      //회차검색 대화상자 LayoutID
    private static final int LAYOUT_DBLIST = R.layout.dialog_dblist;        //DB리스트설정 대화상자 LayoutID
    private static final int LAYOUT_NETWORK = R.layout.dialog_network;      //인터넷 연결에러 대화상자 LayoutID
    private static final int LAYOUT_NUM_SETTING = R.layout.dialog_setting_numbers; //고정수 / 제외수 설정 대화상자 LayoutID
    private static final int LAYOUT_DB_DELETE = R.layout.dialog_dbdelete;

    private static final String[] ERROR_MESSAGE = new String[]{"", "%s 이미 존재", "%s 이미 존재", "입력 값 중복", "%s 최대개수 초과", "숫자범위 초과"};

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

    // 타입3 - 인터넷 연결 에러
    private Button dlthree_cancel, dlthree_retry;
    private int network_type;
    private String[] store;
    private String num_type;
    private List<Integer> num_list, num_list_child;

    // 타입4 - 고정수, 제외수 설정
    private TextView dl_numbers_title, dl_numbers_list, dl_numbers_hint, dl_numbers_error, dl_numbers_cancel, dl_numbers_ok;
    private EditText dl_numbers_et;
    private HashSet<Integer> set_hash = new HashSet<>(); // 제외수,고정수 임시 보관 해시set
    private int num_count, error_type = 0;
    private String num_type_child;

    // 타입5 - DB 기록삭제버튼클릭
    private DBinfo dBinfo;

    //다이얼로그 생성자
    public DialogClass(@NonNull Context context, int type) {
        super(context);
        this.mContext = context;
        this.type = type;
    }

    public DialogClass(@NonNull Context context, int type, DBinfo dBinfo) {
        super(context);
        this.mContext = context;
        this.type = type;
        this.dBinfo = dBinfo;
    }

    public DialogClass(Context context, int type, int network_type, String[] store){
        super(context);
        this.mContext = context;
        this.type = type;
        this.network_type = network_type;
        if(network_type == 4){
            this.store = store;
        }
    }

    public DialogClass(Context context, int type, String num_type){
        super(context);
        Log.d("확인", "dialogClass 생성자 " + type + ", " + num_type);
        this.mContext = context;
        this.type = type;

        if (num_type.compareTo("고정수") == 0) {
            this.num_type = num_type;
            this.num_type_child = "제외수";

            num_list = MainActivity.fixedNums;
            num_list_child = MainActivity.exceptNums;
            num_count = 6;

        } else if (num_type.compareTo("제외수") == 0) {
            this.num_type = num_type;
            this.num_type_child = "고정수";

            num_list = MainActivity.exceptNums;
            num_list_child = MainActivity.fixedNums;
            num_count = 39;
        }
    }

    //다이얼로그 리스너 설정
    public void setDialogListener(MyDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("다이얼로그", "onCreate 진입 type - " + type);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog_set(type);
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
                dlone_comment.setText(String.format("회차 범위 [ 1 - %d ]", lastturn));      //제일최근회차 가져오기
                break;
            // 타입2 설정
            case 2:
                Log.d("다이얼로그", "case 2 진입, type - " + type);
                setContentView(LAYOUT_DBLIST);

                // Expandable ListView (확장 리스트뷰) 객체
                dltwo_exlist = findViewById(R.id.DLTwo_ExList);
                // 취소 버튼
                dltwo_cancel = findViewById(R.id.DLTwo_Cancel);
                dltwo_cancel.setOnClickListener(this);
                // 확인 버튼
                dltwo_check = findViewById(R.id.DLTwo_Check);
                dltwo_check.setText("확인");
                dltwo_check.setOnClickListener(this);

                dbOpenHelper = new DBOpenHelper(getContext());

                Log.d("다이얼로그", "dltwo_Listitem 생성 id/size - " + dltwo_listitem + ", " + dltwo_listitem.size());
                dltwo_listitem = dbOpenHelper.selectListAllDB();
                Log.d("다이얼로그", "dltwo_Listitem 데이터삽입 id/size - " + dltwo_listitem + ", " + dltwo_listitem.size());
                if (dltwo_listitem.size() > 0) {
                    dltwo_adapter = new ExpandableAdapter(getContext(), dltwo_listitem, this);
                    dltwo_exlist.setAdapter(dltwo_adapter);
                    Log.d("다이얼로그", "dltwo_adapter 생성 - " + dltwo_adapter);
                } else {
                    Toast.makeText(mContext, "DB저장 기록 없음", Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                Log.d("다이얼로그", "case 3 진입, type - " + type);
                setContentView(LAYOUT_NETWORK);

                setCancelable(false);

                dlthree_cancel = findViewById(R.id.DLThree_Cancel);
                dlthree_cancel.setOnClickListener(this);

                dlthree_retry = findViewById(R.id.DLThree_Retry);
                dlthree_retry.setOnClickListener(this);
                break;

            case 4:
                Log.d("다이얼로그", "case 4 진입, type - " + type);
                setContentView(LAYOUT_NUM_SETTING);
                setCancelable(false);

                dl_numbers_title = findViewById(R.id.DL_numbers_title);
                dl_numbers_title.setText(num_type + " 설정");

                dl_numbers_list = findViewById(R.id.DL_numbers_list);
                if(!num_list.isEmpty()){
                    String text = num_list.toString().replace("[","").replace("]","").replace(",","");
                    dl_numbers_list.setText(String.format("설정된 숫자 %s", text));
                } else {
                    dl_numbers_list.setVisibility(View.GONE);
                }
                dl_numbers_hint = findViewById(R.id.DL_numbers_hint);

                dl_numbers_error = findViewById(R.id.DL_numbers_error);

                dl_numbers_cancel = findViewById(R.id.DL_numbers_cancel);
                dl_numbers_cancel.setOnClickListener(this);

                dl_numbers_ok = findViewById(R.id.DL_numbers_ok);
                dl_numbers_ok.setOnClickListener(this);

                dl_numbers_et = findViewById(R.id.DL_numbers_et);
                dl_numbers_et.setKeyListener(DigitsKeyListener.getInstance("0123456789."));        //숫자와 Dot만 입력할수있도록 (Dot가 구분자)
                dl_numbers_et.addTextChangedListener(new TextWatcher() {       // 키입력마다 검사하기위해 TextWatcher 등록)
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable editable) {
                        String test = editable.toString();
                        if(test.startsWith(".")){
                            dl_numbers_et.setText(test.substring(1));
                        }

                        Log.d("확인", "afterTextChanged");
                        //마지막글자가 Dot인경우 숫자 저장
                        if (test.endsWith(".")) {
                            if(test.length() > 1){
                                if(test.charAt(test.length()-2) == 46){
                                    while (test.contains("..")) {
                                        test = test.replace("..", ".");
                                    }
                                    dl_numbers_et.setText(test);      //중복삭제
                                    dl_numbers_et.setSelection(dl_numbers_et.length());
                                }
                            }
                            set_hash.clear();
                            String[] str = editable.toString().split("\\.");                    //Dot를 기준으로 배열로 나누기

                            //배열 개수만큼 반복
                            for (int i = 0; i < str.length; i++) {

                                if (!str[i].isEmpty()) {
                                    Log.d("확인", "if (!str[i].isEmpty()) 진입");
                                    int number = Integer.parseInt(str[i]);
                                    //입력한 숫자가 0이 아니고, 46보다 작으면 저장
                                    if ((0 < number) && (number < 46)) {
                                        Log.d("확인", "if ((0 < number) && (number < 46)) 진입");

                                        // 이미설정된 리스트 | 다른종류 리스트 | 임시해쉬
                                        // 해당번호가 존재한지 검사, 둘중 하나라도 존재하면 if문 들어감
                                        //if ( (1 <= error_type) && (error_type >= 3)) {
                                        if (num_list.contains(number) || num_list_child.contains(number) || set_hash.contains(number) ) {
                                            int end = (editable.toString().length()) - (str[i].length() + 1);
                                            test = editable.toString().substring(0, end);

                                            dl_numbers_et.setText(test);      //중복삭제
                                            dl_numbers_et.setSelection(dl_numbers_et.length());                       //포커스 마지막으로 이동
                                            if(num_list.contains(number)){
                                                Log.d("확인", "num_list 존재 - result 1");
                                                error_type = 1;
                                                //dl_numbers_error.setText(String.format("%s 이미 존재", num_type));
                                            } else if(num_list_child.contains(number)){
                                                Log.d("확인", "num_list_child 존재 - result 2");
                                                //dl_numbers_error.setText(String.format("%s 이미 존재", num_type_child));
                                                error_type = 2;
                                            } else if(set_hash.contains(number)){
                                                Log.d("확인", "set_hash 존재 - result 3");
                                                //dl_numbers_error.setText("입력 값 중복");
                                                error_type = 3;
                                            }
                                            Log.d("확인", "해쉬 add실패 (중복) - " + str[i]);
                                        } else {
                                            // 2개모두 존재안할경우
                                            if ((num_list.size() + set_hash.size() + 1) <= num_count) {
                                                // 설정된 숫자 개수 + 입력한 숫자 개수가 제한개수보다 작거나 같으면 추가
                                                set_hash.add(number);
                                                Log.d("확인", "해쉬 add성공 - " + str[i]);
                                                error_type = 0;
                                                //dl_numbers_error.setText("");
                                            } else {
                                                // 제한개수보다 큰경우는 삭제
                                                test = editable.toString().replace(str[i] + ".", "");

                                                dl_numbers_et.setText(test);      //중복삭제
                                                dl_numbers_et.setSelection(dl_numbers_et.length());

                                                Log.d("확인", "해쉬 add실패 (개수초과) - " + str[i]);

                                                error_type = 4;
                                                //dl_numbers_error.setText(String.format("%s 최대개수 초과", num_type));
                                            }
                                        }
                                    } else {
                                        //입력숫자가 0이거나 46이상인경우 자동삭제, 포커스 위치 맨끝설정
                                        test = editable.toString().replace(str[i] + ".", "");

                                        dl_numbers_et.setText(test);      //중복삭제
                                        dl_numbers_et.setSelection(dl_numbers_et.length());

                                        Log.d("확인", "해쉬 add실패 (숫자범위 초과) - " + str[i]);
                                        error_type = 5;
                                        //dl_numbers_error.setText("숫자범위 초과");
                                    }
                                    Log.d("확인", "error_type - " + error_type);
                                    Log.d("확인", "afterTextChanged \n\t editable - " + editable.toString()
                                            + "\n\t length - " + set_hash.size() + ", set_hash - " + set_hash);
                                    if((error_type == 1) || (error_type == 4)){
                                        dl_numbers_error.setText(String.format(ERROR_MESSAGE[error_type], num_type));
                                    } else if (error_type == 2){
                                        dl_numbers_error.setText(String.format(ERROR_MESSAGE[error_type], num_type_child));
                                    } else {
                                        dl_numbers_error.setText(ERROR_MESSAGE[error_type]);
                                    }
                                } else {
                                }
                            }
                        }
                        if((test.isEmpty()) || test.equals(".")){
                            dl_numbers_hint.setText(String.format("입력한 갯수 : %d", 0));
                        } else {
                            dl_numbers_hint.setText(String.format("입력한 갯수 : %d", (test.split("\\.").length)));
                        }

                    }
                });
                break;

            case 5:
                // DB 기록 삭제버튼시 다이얼로그 설정
                Log.d("다이얼로그", "case 4 진입, type - " + type);
                setContentView(LAYOUT_DB_DELETE);
                setCancelable(false);

                TextView dl_delete_turn = findViewById(R.id.dialog_dbdelete_turn);
                dl_delete_turn.setText(String.format("%d 회",dBinfo.getTurn()));

                String[] numset = dBinfo.getNumset().split(",");
                Resources res = mContext.getResources();

                ImageView[] imageViews = new ImageView[6];
                for (int i=0; i<imageViews.length; i++){
                    int id = res.getIdentifier("dialog_delete_num_"+(i+1), "id", mContext.getPackageName());
                    imageViews[i] = findViewById(id);
                    imageViews[i].setImageResource(MainActivity.num_ID[Integer.parseInt(numset[i])-1]);
                }

                Button dl_delete_cancel = findViewById(R.id.dialog_dbdelete_cancel);
                dl_delete_cancel.setOnClickListener(this);

                Button dl_delete_ok = findViewById(R.id.dialog_dbdelete_ok);
                dl_delete_ok.setOnClickListener(this);
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
                if(num.isEmpty()) {
                    break;
                } else if (Integer.parseInt(num) > lastturn || Integer.parseInt(num) == 0) {
                    //마지막회차랑 입력한 회차 비교 (존재않는 회차일경우 검색x)
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

            case R.id.DLThree_Cancel:
                if(network_type == 1){
                    ActivityCompat.finishAffinity(MainActivity.activity);
                    System.exit(0);
                } else {
                    dismiss();
                }
                break;
            case R.id.DLThree_Retry:
                dismiss();
                NetworkStatus.Check_NetworkStatus(mContext, network_type, store);
                break;


            case R.id.DL_numbers_ok:
                Log.d("확인", "추가버튼 눌림");
                set_hash.clear();
                String str = dl_numbers_et.getText().toString();
                if(!str.isEmpty()){
                        set_Store(num_type, str.split("\\."));
                }
                dismiss();
                break;

            case R.id.DL_numbers_cancel:
                Log.d("다이얼로그", "numbers_cancel 클릭");
                dismiss();
                break;

            case R.id.dialog_dbdelete_cancel:
                Toast.makeText(mContext,"취소클릭",Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.dialog_dbdelete_ok:
                Toast.makeText(mContext,"삭제클릭",Toast.LENGTH_SHORT).show();
                DBOpenHelper dbOpenHelper = new DBOpenHelper(mContext);
                dbOpenHelper.deleteDB(dBinfo.getNumset());
                dismiss();
                break;
        }
    }

    private void set_Store(String num_type, String[] numbers_et) {
        HashSet<Integer> hash = new HashSet<>();
        if (num_list.size() != 0) {        //비어있지않다면 Hash에 옮기기
            hash.addAll(num_list);
        }

        for (int i = 0; i < numbers_et.length; i++) {
            int number = Integer.parseInt(numbers_et[i]);
            if((num_list.contains(number) || num_list_child.contains(number) || hash.contains(number))){
               // 이미 존재한다면
            } else {
                if((0 < number) && (number < 46) && ((hash.size()+1) < num_count)) {
                    hash.add(number);
                }
            }

        }
        Log.d("확인", "set_hash - " + set_hash + ", " + set_hash.size());

        num_list.clear();             //기존 exceptNums 리스트 비우기
        num_list.addAll(hash);    //다시 덮어쓰기 (중복체크)
        Collections.sort(num_list);   //오름차순 정렬
        Log.d("확인", "sort 후 list - " + num_list);

        if (num_type.equals("고정수")){
            MainActivity.fixedNums = num_list;
        } else if (num_type.equals("제외수")){
            MainActivity.exceptNums = num_list;
        }
    }
}
