package com.example.maptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    public static final String CLIENT_ID = "y0189tgx11";        // 네이버맵 지오코딩 클라이언트 아이디값
    public static final String CLIENT_SECRET = "NK87OTfxcF1JlVUt6acqMimoSKV5toNq5Y8v75IR"; // 네이버맵 지오코딩 시크릿값
    public static final String INFO_TURN = "      %d회차";      // 메뉴 info - turn 상수
    public static final String INFO_DATE = "      %s";          // 메뉴 info - date 상수
    public static final String INFO_NUMSET = "      %s";        // 메뉴 info - 제외수, 고정수 표시

    public static int[] imgId = new int[45];    //1~45숫자 이미지리소스 ID 저장배열
    public static int[] packid = new int[6];    //Pack1~6 이미지리소스 ID 저장배열

    public static List<Integer> fixedNums = new ArrayList<>();  //최대 7개
    public static List<Integer> exceptNums = new ArrayList<>(); //최대 38개 (45 - 38 = 7)

    private LinearLayout set_linear;    // 세팅 대화상자에 사용 (뷰그룹)
    private TextView set_tv;            // set_linear에 포함될 자식뷰
    private EditText set_ev;            // set_linear에 포함될 자식뷰
    private HashSet<Integer> set_hash = new HashSet<>(); // 제외수,고정수 임시 보관 해시set

    public static FragOne frag1;
    public static FragOneTwo frag11;
    FragTwo frag2;
    FragThree frag3;

    private DrawerLayout mDrawerLayout;         //최상단 DrawerLayout
    private NavigationView mNavigationView;     //네비게이션 뷰 (메뉴)
    private Context context = this;

    public static LottoParsingInfo lastLottoinfo, searchLottoInfo;  // 최신회차, 검색회차 저장

    //네이버맵 지오코딩 보류
    BufferedReader br;
    StringBuilder searchResult;

    private int count = 0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //숫자이미지(1~45) Pack(1~6) 이미지 아이디얻기
        getIdset();

        //상단 툴바연결
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //툴바 설정
        setSupportActionBar(toolbar);
        //시스템 액션바 얻기
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);        // 기존 title 지우기 (false - 지우지않음)
        actionBar.setDisplayHomeAsUpEnabled(true);      // 메뉴버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp); //메뉴버튼 이미지 지정
        //actionBar.setDisplayHomeAsUpEnabled(false);     // 메뉴버튼 없애기


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);    //DrawerLayout 연결(최상단 레이아웃)
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);     //네비게이션 뷰 연결(안보임)
        mNavigationView.setNavigationItemSelectedListener(this);            //네비게이션 뷰 아이템 클릭 처리리스너등록

        // 최신 로또정보 파싱
        TestClass testclass = new TestClass();
        lastLottoinfo = testclass.parsing("");
        searchLottoInfo = lastLottoinfo;

        frag1 = new FragOne();
        frag11 = new FragOneTwo();
        frag2 = new FragTwo();
        frag3 = new FragThree();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, frag1).commit();      //프래그먼트1 표시

        //바텀 네비게이션뷰 연결
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }


    //이미지 int형 id반환함수
    private void getIdset() {
        Resources res = getResources();
        for (int i = 1; i < 46; i++) {                //45번반복
            int stringId = res.getIdentifier("num" + i, "string", getPackageName());     // name에 해당하는 값의 위치 가져옴, 2131492892반환
            String stringId2 = res.getString(stringId);
            imgId[i - 1] = res.getIdentifier(stringId2, "drawable", getPackageName());
        }
        for (int i = 1; i < 7; i++)
            packid[i - 1] = res.getIdentifier("pack" + i, "drawable", getPackageName());
    }
    //숫자집합 오름차순으로 정렬, 홀짝비율 계산
    public static String[] numsetSort(String numset) {
        String[] numberset = numset.split(",");

        int[] changeset = new int[numberset.length];           //numberset배열길이만큼 할당
        int paircount = 0;                                     //짝수개수 보관변수
        for (int i = 0; i < numberset.length; i++) {
            changeset[i] = Integer.parseInt(numberset[i]);
            if (changeset[i] % 2 == 0)                               //짝수인지 확인 나머지가 0인경우
                paircount += 1;                                      //짝수 개수파악
        }
        int hallcount = numberset.length - paircount;                       //전체숫자개수중 짝수개수 제외 나머지는 홀수

        Arrays.sort(changeset);                                //오름차순으로 정렬
        String[] str = new String[2];
        str[0] = Arrays.toString(changeset).
                replace("[", "").replace("]", "").replace(" ", "");
        str[1] = String.valueOf(hallcount) + ":" + String.valueOf(paircount);
        Log.d("나눗셈", str[1]);
        return str;         //String배열반환(0번째 정렬된 숫자정보 / 1번째 홀짝개수)
    }

    //메뉴버튼 눌렀을때 콜백메서드 (여기서 메뉴편집)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("테스트", "onOptionsItemSelected 호출");
        int fragindex = checkFragment();        //현재 프래그먼트 확인용 int
        mNavigationView.getMenu().clear();      //메뉴 비우기(안하면 inflate시 추가로 쌓임)
        switch (item.getItemId()) {
            case android.R.id.home: {            // 왼쪽 상단(메뉴버튼)일때
                switch (fragindex) {             // 현재 프래그먼트 위치별로 메뉴세팅
                    //Frag_One
                    case 1:
                        mNavigationView.inflateMenu(R.menu.navi_menu1);
                        SubMenu subMenu = mNavigationView.getMenu().getItem(1).getSubMenu();
                        if (fixedNums.size() != 0) {
                            String str = String.format(INFO_NUMSET, fixedNums.toString()
                                    .replace("[", "")
                                    .replace("]", "")
                                    .replace(",", " "));
                            subMenu.getItem(1).setTitle(String.format(INFO_NUMSET, str));
                        }
                        if (exceptNums.size() != 0) {
                            String str = String.format(INFO_NUMSET, exceptNums.toString()
                                    .replace("[", "")
                                    .replace("]", "")
                                    .replace(",", " "));
                            subMenu.getItem(3).setTitle(String.format(INFO_NUMSET, str));
                        }
                        break;
                    //Frag_OneTwo
                    case 2:
                        mNavigationView.inflateMenu(R.menu.navi_menu1);
                        break;
                    //Frag_Two
                    case 3:
                        mNavigationView.inflateMenu(R.menu.navi_menu2);
                        SubMenu subMenu1 = mNavigationView.getMenu().getItem(0).getSubMenu();
                        subMenu1.getItem(0).setTitle(String.format(INFO_TURN, searchLottoInfo.getTurn()));
                        subMenu1.getItem(1).setTitle(String.format(INFO_DATE, searchLottoInfo.getDate()));
                        break;
                    //Frag_Three
                    case 4:
                        mNavigationView.inflateMenu(R.menu.navi_menu3);
                        break;
                }
                mDrawerLayout.openDrawer(GravityCompat.START);  //Drawer열기 gravity.START (제일왼쪽위치부터 슬라이드 등장)
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 드로어레이아웃 / 네비게이션바 같이 아이템 선택이벤트 처리
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // 어떤 메뉴 아이템이 터치되었는지 확인
        switch (menuItem.getItemId()) {
            //바텀메뉴바 아이디
            case R.id.bottombar_one:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, frag1) // 표시할 레이아웃, 변경할 프래그먼트 설정
                        .addToBackStack(null)          // 백스택에 변겅전 프래그먼트 저장
                        .commit();                     // 트랜잭션 실행
                return true;
            case R.id.bottombar_two:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, frag2).addToBackStack(null).commit();
                return true;

            case R.id.bottombar_three:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, frag3).addToBackStack(null).commit();
                return true;
            //슬라이드 메뉴바 아이디
            //고정수 설정
            case R.id.menu1_fixednums:
                Toast.makeText(context, menuItem.getTitle().toString() + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                //setting visible조작하기
                //mNavigationView.getMenu().getItem(0).getSubMenu().setGroupVisible(R.id.group2,false);
                //setting 고정,제외 아이템 사이 추가하기
                //mNavigationView.getMenu().getItem(0).getSubMenu().add(R.id.group2,Menu.NONE,3,"테스트추가하기");
                mDrawerLayout.closeDrawer(mNavigationView);
                setNum("고정수", fixedNums);
                break;
            //제외수 설정
            case R.id.menu1_exceptnums:
                //mNavigationView.getMenu().getItem(0).setVisible(true);
                mDrawerLayout.closeDrawer(mNavigationView);
                setNum("제외수", exceptNums);
                break;
            //설정 초기화 (고정수,제외수)
            case R.id.menu1_reset:
                mDrawerLayout.closeDrawer(mNavigationView);
                exceptNums.clear();
                fixedNums.clear();
                Log.d("메뉴", "reset 적용 \n\t exceptNums size - " + exceptNums.size() + "\n\t fixedNums size - " + fixedNums.size());
                Toast.makeText(getApplicationContext(), "설정 초기화", Toast.LENGTH_SHORT);
                break;
            //회차검색
            case R.id.menu2_Search:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                frag2.dialogshow(1);
                break;
            //DB설정
            case R.id.menu2_DBSet:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                frag2.dialogshow(2);
                break;
            case R.id.menu3_map:
                //searchMap("로또");
                Intent it = new Intent(this, MapNaver.class);  //MapNaver액티비티 띄울목적
                it.putExtra("TAG", 1);          //TAG값 전달 (int형)
                startActivity(it);
                break;
        }
        return false;
    }

    //현재 프래그먼트 확인 1-FragOne, 2-FragOneTwo, 3-FragTwo, 4-FragThree
    public int checkFragment() {
        int fragindex = 0;
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);

        Log.d("테스트", "현재 Fragment "         //테스트용 로그출력
                + "\n\t FragOne instanceof - " + (fragment instanceof FragOne)
                + "\n\tFragOnTwo instanceOf - " + (fragment instanceof FragOneTwo)
                + "\n\tFragTwo instanceOf - " + (fragment instanceof FragTwo)
                + "\n\tFragThree instanceOf - " + (fragment instanceof FragThree));

        if (fragment instanceof FragOne) {
            fragindex = 1;
        } else if (fragment instanceof FragOneTwo) {
            fragindex = 2;
        } else if (fragment instanceof FragTwo) {
            fragindex = 3;
        } else if (fragment instanceof FragThree) {
            fragindex = 4;
        }
        return fragindex;
    }

    // 고정수/제외수 설정 대화상자 띄우는 함수
    public void setNum(final String type, List<Integer> setlist) {
        final List<Integer> list = setlist;
        if (type.compareTo("고정수") == 0) {
            count = 7;
        } else if (type.compareTo("제외수") == 0) {
            count = 38;
        }
        set_linear = new LinearLayout(this);
        set_linear.setOrientation(LinearLayout.VERTICAL);
        set_linear.setGravity(View.TEXT_ALIGNMENT_CENTER);

        set_ev = new EditText(this);
        set_ev.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        set_ev.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                //마지막글자가 Dot인경우 숫자 저장
                if (editable.toString().endsWith(".")) {
                    set_hash.clear();

                    String[] str = editable.toString().split("\\.");                    //Dot를 기준으로 배열로 나누기

                    for (int i = 0; i < str.length; i++) {                                            //배열 개수만큼 반복
                        if ((str[i].compareTo("") != 0) && (Integer.valueOf(str[i]) < 46)) {     //입력한 숫자가 0이 아니고, 46보다 작으면 저장

                            if (!set_hash.add(Integer.parseInt(str[i]))) {                                      //해쉬set 저장실패하면 (중복일경우)
                                int end = (editable.toString().length()) - (str[i].length() + 1);
                                set_ev.setText(editable.toString().substring(0, end));      //중복삭제
                                set_ev.setSelection(set_ev.length());                       //포커스 마지막으로 이동
                                Log.d("확인", "해쉬 add실패 - " + str[i]);
                            } else {
                                if (list.size() != 0)                  //이미 설정된 갯수 모두 더해서
                                    set_hash.addAll(list);

                                if (set_hash.size() <= count) {
                                    Log.d("확인", "해쉬 add성공 - " + str[i]);
                                } else {
                                    set_ev.setText(editable.toString().replace(str[i] + ".", ""));
                                    set_ev.setSelection(set_ev.length());
                                }
                            }
                        } else {       //입력숫자가 0이거나 46이상인경우 자동삭제, 포커스 위치 맨끝설정
                            set_ev.setText(editable.toString().replace(str[i] + ".", ""));
                            set_ev.setSelection(set_ev.length());
                        }
                    }
                    Log.d("확인", "afterTextChanged \n\t editable - " + editable.toString()
                            + "\n\t length - " + set_hash.size() + ", set_hash - " + set_hash);
                    set_tv.setText(String.format("입력한 갯수 - %d\n", set_hash.size()));
                }
            }
        });

        set_tv = new TextView(this);
        set_tv.setTextSize(14);
        set_tv.setText("테스트");
        set_linear.addView(set_ev);         //에디트텍스트 추가
        set_linear.addView(set_tv);         //텍스트뷰 추가

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(type + " 설정");
        builder.setMessage("테스트");
        builder.setView(set_linear);      //뷰그룹 추가
        //추가버튼 이벤트리스너 등록
        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("확인", "추가버튼 눌림");
                Log.d("확인", "addAll 전 \n" + exceptNums.size());
                set_hash.clear();
                if (type.compareTo("고정수") == 0) {
                    fixedNums = set_Store(fixedNums);
                } else if (type.compareTo("제외수") == 0) {
                    exceptNums = set_Store(exceptNums);
                }
                if (exceptNums.size() != 0)        //비어있지않다면 Hash에 옮기기
                    set_hash.addAll(exceptNums);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();     //AlertDialog 띄우기
    }

    // 고정수/제외수 설정 적용 함수
    public List<Integer> set_Store(List<Integer> store_List) {
        if (store_List.size() != 0)        //비어있지않다면 Hash에 옮기기
            set_hash.addAll(store_List);

        String[] str = set_ev.getText().toString().split("\\.");
        for (int j = 0; j < str.length; j++) {
            if ((str[j].compareTo("") != 0) && (Integer.parseInt(str[j]) < 46) && (set_hash.size() < count)) {
                set_hash.add(Integer.parseInt(str[j]));
            }
        }
        Log.d("확인", "set_hash - " + set_hash + ", " + set_hash.size());

        store_List.clear();             //기존 exceptNums 리스트 비우기
        store_List.addAll(set_hash);    //다시 덮어쓰기 (중복체크)
        Collections.sort(store_List);   //오름차순 정렬
        Log.d("확인", "sort 후 list - " + store_List);
        return store_List;
    }
}