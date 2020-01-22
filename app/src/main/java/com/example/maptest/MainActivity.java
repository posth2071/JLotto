package com.example.maptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maptest.AlarmPackage.AlarmReceiver;
import com.example.maptest.QRCord.CustomDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

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
    public static FragTwo frag2;
    public static FragThree frag3;

    private DrawerLayout mDrawerLayout;         //최상단 DrawerLayout
    private NavigationView mNavigationView;     //네비게이션 뷰 (메뉴)

    public static Activity activity;
    public static Context context;

    public static LottoParsingInfo lastLottoinfo, searchLottoInfo;  // 최신회차, 검색회차 저장

    //네이버맵 지오코딩 보류
    BufferedReader br;
    StringBuilder searchResult;

    private int count = 0;
    private String[] permissions_list = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    protected void onResume() {
        // 최신 로또정보 파싱
        super.onResume();
        TestClass testclass = new TestClass();
        lastLottoinfo = testclass.parsing("");
        searchLottoInfo = lastLottoinfo;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        context = this;

        //로딩화면 띄우기
        Intent loading_it = new Intent(this, Loading.class);
        startActivity(loading_it);
        Log.d("네트워크", "로딩화면 인텐트 시작 startActivity(loading_it)");

        permissionCheck(permissions_list);

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


        Log.d("네트워크", "최신 로또정보 파싱 실행");

        /*
        // 최신 로또정보 파싱
        TestClass testclass = new TestClass();
        lastLottoinfo = testclass.parsing("");
        searchLottoInfo = lastLottoinfo;
         */
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
    public static String numsetSort(String numset) {
        String[] numberset = numset.split(",");
        int[] changeset = new int[numberset.length];           //numberset배열길이만큼 할당

        for (int i = 0; i < numberset.length; i++) {
            changeset[i] = Integer.parseInt(numberset[i]);
        }
        Arrays.sort(changeset);                                //오름차순으로 정렬

        String str = Arrays.toString(changeset)
                .replace("[", "")       //괄호제거
                .replace("]", "")       //괄호제거
                .replace(" ", "");      //공백 제거
        return str;         //String배열반환(0번째 정렬된 숫자정보 / 1번째 홀짝개수)
    }

    //홀짝계산 함수
    public static String checkHallPair (String numset){
        String[] nums = numset.split(",");
        int paircount = 0;
        for(int i=0; i<nums.length; i++){
            if(Integer.parseInt(nums[i])%2==0){
                paircount +=1;
            }
        }
        int hallcount = nums.length - paircount;
        return String.format("%d:%d",hallcount,paircount);
    }

    //당첨결과 확인 함수
    public static String[] checkResult(String numset, LottoParsingInfo lottoParsingInfo){
        String[] nums = numset.split(",");
        String[] searchnumset = lottoParsingInfo.getLottoInfo();
        HashSet<Integer> checkHash = new HashSet<>();
        //보너스번호 제외 6개만 넣기
        for(int i=0; i<6; i++){
            checkHash.add(Integer.parseInt(searchnumset[i]));
        }
        int resultcount = 0;
        int bonuscount = 0;
        int bonusnum = Integer.parseInt(searchnumset[6]);
        for(int i=0;i<6; i++){
            int addnum = Integer.parseInt(nums[i]);
            if(!checkHash.add(addnum)){  //해쉬add가 실패한경우(이미있는경우) 동일숫자 존재
                resultcount +=1;        //resultcount 1증가
            }
            if(addnum==bonusnum){
                bonuscount +=1;
            }
        }
        Log.d("확인", "\n" +
                "\t" + numset +" : "+searchnumset.toString() +
                "result count - "+resultcount+", bonusCount - "+bonuscount);

        String[] resultstr = new String[2];
        switch (resultcount){
            case 6:
                resultstr[0] = "1등";
                resultstr[1] = "#00FF00";
                return resultstr;

            case 5:
                if(bonuscount!=0){
                    resultstr[0] = "2등";
                    resultstr[1] = "#0000FF";
                    return resultstr;
                } else{
                    resultstr[0] = "3등";
                    resultstr[1] = "#FF5733";
                    return resultstr;
                }

            case 4:
                resultstr[0] = "4등";
                resultstr[1] = "#E9967A";
                return resultstr;

            default:
                resultstr[0] = "미당첨";
                resultstr[1] = "#000000";
                return resultstr;
        }
    }

    //메뉴버튼 눌렀을때 콜백메서드 (여기서 메뉴편집)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("테스트", "onOptionsItemSelected 호출");
        int fragindex = checkFragment();        //현재 프래그먼트 확인용 int
        mNavigationView.getMenu().clear();      //메뉴 비우기(안하면 inflate시 추가로 쌓임)
        switch (item.getItemId()) {
            case android.R.id.home: {            // 왼쪽 상단(메뉴버튼)일때
                // 현재 프래그먼트 위치별로 메뉴세팅
                switch (fragindex) {
                    //Frag_One
                    case 1:
                        mNavigationView.inflateMenu(R.menu.navi_menu1);
                        set_mode("수동모드", true);

                        SubMenu subMenu = mNavigationView.getMenu().getItem(2).getSubMenu();
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
                        set_mode("자동모드", false);    //모드, 그룹활성화 세팅
                        break;
                    //Frag_Two
                    case 3:
                        mNavigationView.inflateMenu(R.menu.navi_menu2);
                        SubMenu subMenu2 = mNavigationView.getMenu().getItem(0).getSubMenu();
                        subMenu2.getItem(0).setTitle(String.format(INFO_TURN, searchLottoInfo.getTurn()));
                        subMenu2.getItem(1).setTitle(String.format(INFO_DATE, searchLottoInfo.getDate()));
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
            // 메뉴1 - 모드변경 눌린경우
            case R.id.menu1_mode:
                mDrawerLayout.closeDrawer(mNavigationView);
                int fragindex = checkFragment();
                if(fragindex==1){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, MainActivity.frag11).addToBackStack(null).commit();
                } else if(fragindex==2){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, MainActivity.frag1).addToBackStack(null).commit();
                }
                break;

            // 메뉴1 - 고정수 설정
            case R.id.menu1_fixednums:
                mDrawerLayout.closeDrawer(mNavigationView);
                setNum("고정수", fixedNums);
                break;

            // 메뉴1 - 제외수 설정
            case R.id.menu1_exceptnums:
                //mNavigationView.getMenu().getItem(0).setVisible(true);
                mDrawerLayout.closeDrawer(mNavigationView);
                setNum("제외수", exceptNums);
                break;

            // 메뉴1 -설정 초기화 (고정수,제외수)
            case R.id.menu1_reset:
                mDrawerLayout.closeDrawer(mNavigationView);
                exceptNums.clear();
                fixedNums.clear();
                Log.d("메뉴", "reset 적용 \n\t exceptNums size - " + exceptNums.size() + "\n\t fixedNums size - " + fixedNums.size());
                break;

            // 메뉴2 - 회차검색
            case R.id.menu2_Search:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                // 인터넷 연결상태 확인 후 실행
                NetworkStatus.Check_NetworkStatus(context, 2, null);

                //frag2.dialogshow(1);
                break;

            // 메뉴2 - DB설정
            case R.id.menu2_DBSet:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                frag2.dialogshow(2);
                break;

            // 메뉴3 - 현재위치 주변매장 찾기
            case R.id.menu3_map:
                //searchMap("로또");
                mDrawerLayout.closeDrawer(GravityCompat.START);

                // 인터넷 연결상태 확인 후 실행
                NetworkStatus.Check_NetworkStatus(context, 3, null);

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

    public void set_mode(String title, boolean state){
        //Mode타이틀 설정
        mNavigationView.getMenu().getItem(0).getSubMenu().getItem(0).setTitle(title);
        //설정그룹 버튼들 비활성화/활성화
        mNavigationView.getMenu().getItem(1).getSubMenu().getItem(0).setEnabled(state); //고정수 설정
        mNavigationView.getMenu().getItem(1).getSubMenu().getItem(1).setEnabled(state); //제외수 설정
        mNavigationView.getMenu().getItem(1).getSubMenu().getItem(2).setEnabled(state); //reset 설정
        //setting그룹 제외수,고정수 리스트 숨기기/보이기
        mNavigationView.getMenu().getItem(2).getSubMenu().getItem(0).setVisible(state);
        mNavigationView.getMenu().getItem(2).getSubMenu().getItem(1).setVisible(state);
        mNavigationView.getMenu().getItem(2).getSubMenu().getItem(2).setVisible(state);
        mNavigationView.getMenu().getItem(2).getSubMenu().getItem(3).setVisible(state);
    }

    // 고정수/제외수 설정 대화상자 띄우는 함수
    public void setNum(final String type, List<Integer> setlist) {
        final List<Integer> list = setlist;
        if (type.compareTo("고정수") == 0) {
            count = 6;
        } else if (type.compareTo("제외수") == 0) {
            count = 39;
        }
        set_linear = new LinearLayout(this);
        set_linear.setOrientation(LinearLayout.VERTICAL);
        set_linear.setGravity(View.TEXT_ALIGNMENT_CENTER);

        set_ev = new EditText(this);
        set_ev.setKeyListener(DigitsKeyListener.getInstance("0123456789."));        //숫자와 Dot만 입력할수있도록 (Dot가 구분자)
        set_ev.addTextChangedListener(new TextWatcher() {       // 키입력마다 검사하기위해 TextWatcher 등록)
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                //마지막글자가 Dot인경우 숫자 저장
                if (editable.toString().endsWith(".")) {
                    set_hash.clear();
                    HashSet<Integer> testHash = new HashSet<>();
                    String[] str = editable.toString().split("\\.");                    //Dot를 기준으로 배열로 나누기

                    for (int i = 0; i < str.length; i++) {                                            //배열 개수만큼 반복
                        if ((Integer.parseInt(str[i])!= 0) && (Integer.valueOf(str[i]) < 46)) {     //입력한 숫자가 0이 아니고, 46보다 작으면 저장
                            testHash.addAll(list);
                            // if (testHash.add(Integer.parseInt(str[i]))) {
                            if (!(set_hash.add(Integer.parseInt(str[i])) && (testHash.add(Integer.parseInt(str[i]))))) {                       //해쉬set 저장실패하면 (중복일경우)
                                int end = (editable.toString().length()) - (str[i].length() + 1);
                                set_ev.setText(editable.toString().substring(0, end));      //중복삭제
                                set_ev.setSelection(set_ev.length());                       //포커스 마지막으로 이동
                                Log.d("확인", "해쉬 add실패 - " + str[i]);
                            } else {
                                if (list.size() != 0) {                  //이미 설정된 값이있다면 모두더하기
                                    testHash.addAll(set_hash);
                                    testHash.addAll(list);
                                }
                                if (testHash.size() <= count) {
                                    Log.d("확인", "해쉬 add성공 - " + str[i]);
                                } else {
                                    set_ev.setText(editable.toString().replace(str[i] + ".", ""));
                                    set_ev.setSelection(set_ev.length());
                                    Log.d("확인", "해쉬 add실패 이미 중복값- " + str[i]);
                                }
                            }
                        } else {        //입력숫자가 0이거나 46이상인경우 자동삭제, 포커스 위치 맨끝설정
                            set_ev.setText(editable.toString().replace(str[i] + ".", ""));
                            set_ev.setSelection(set_ev.length());
                        }
                    }
                    Log.d("확인", "afterTextChanged \n\t editable - " + editable.toString()
                            + "\n\t length - " + set_hash.size() + ", set_hash - " + set_hash);
                    set_tv.setText(String.format("입력한 갯수 : %d", set_hash.size()));
                } else {
                    set_tv.setText(String.format("입력한 갯수 : %d", (editable.toString().split("\\.").length)));
                }
            }
        });

        set_tv = new TextView(this);
        set_tv.setTextSize(14);
        set_tv.setText("");
        set_linear.addView(set_ev);         //에디트텍스트 추가
        set_linear.addView(set_tv);         //텍스트뷰 추가

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(type + " 설정");
        builder.setMessage("");

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            String qr_result = scanResult.getContents();
            Log.d("바코드", qr_result);
            // 동행복권 QR코드인지 검사
            if(qr_result.contains("dhlottery.co.kr")){
                CustomDialog customDialog = new CustomDialog(this, qr_result);
            } else {
                // 동행복권 QR코드 아닌경우
                Toast.makeText(this, "QR코드 오류", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    // 권한설정 함수
    public void permissionCheck(String[] permissions){
        // 권한 설정요청할 String형 List 생성 - 개수를 알수없어 List로 선언
        ArrayList<String> permission_list = new ArrayList<>();

        // 권한 설정상태 체크 (권한 개수만큼 반복)
        for(int i=0; i<permissions.length; i++) {
            // 권한 설정상태 체크 결과 반환 (허용 PERMISSION_GRANTED / 비허용 PERMISSION_DENIED)
            int check_result = ContextCompat.checkSelfPermission(this, permissions[i]);
            // 권한 상태 비허용일 경우, check_result = PERMISSION_DENIED 일때
            if (check_result != PackageManager.PERMISSION_GRANTED) {
                // 권한 요청을 위해 list에 추가
                permission_list.add(permissions[i]);
            }
        }

        // 권한요청할 권한이 하나라도 있는 경우
        if(permission_list.size()>0){
            // 권한요청을 위해 ArrayList -> String[] 변환
            String[] permissions_Strings = permission_list.toArray(new String[permission_list.size()]);
            // 권한요청 requestPErmissions 실행
            ActivityCompat.requestPermissions(this, permissions_Strings, 1);
        }
    }


    // 알람 설정 Alarm
    public final static void alarm_set(Context context, int action){
        // int action, 1 = 알람 Start / 2 = 알람 Cancel

        String log_Text = " \n\t알람 %s, \n\t현재시간 %s \n\t설정시간 %s";

        // 현재 시간얻기
        Calendar cal = Calendar.getInstance();
        Date dateTest = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일, hh시 mm분 ss초", Locale.getDefault());

        Intent alarm_Intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pending_Intent = PendingIntent.getBroadcast(context,1,alarm_Intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarm_Manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //cal.set(Calendar.SECOND, 0);
        //cal.add(Calendar.MINUTE, 1);

        /*
        // 현재 요일얻기 - 1일 2월 3화 4수 5목 6금 7토
        int day_week = cal.get(Calendar.DAY_OF_WEEK);

        // 토요일이라면 21시 기준으로 구분 (21시 이전, 오늘 알람 실행 / 21시 이후, 다음주 알람 실행)
        if(day_week == 7){
            if(cal.get(Calendar.HOUR_OF_DAY) >= 21){
                cal.add(Calendar.DAY_OF_MONTH,7); // 일주일 뒤 설정, add()는 마지막 Day가 넘어갈시 월단위 추가 계산
            }
            // 현재 요일이 토요일이 아닌경우, 알람 토요일로 설정
        } else {
            cal.add(Calendar.DAY_OF_MONTH, 7-day_week);
        }

        // 시간 설정 - 21시 정각 (9시)
        cal.set(Calendar.HOUR_OF_DAY, 21);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
         */

        switch (action){
            case 1:     // action = 1, 알람 생성 Start
                Calendar test_cal = Calendar.getInstance();
                test_cal.add(Calendar.SECOND, 5);

                // 디바이스 API레벨 별로 분기 실행 (메소드 차이 set() / setExact() / setExactAllowWhileidle() )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //jAlarmManager1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), jPendingIntent1);
                    //Log.d("알람", " \n\t알람시작 \t API23 <= VERSION (마쉬멜로우 6.0 이상) \n\t"+ date_Text);
                    Date test_date = test_cal.getTime();
                    String test_Text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일, hh시 mm분 ss초", Locale.getDefault()).format(test_date);
                    alarm_Manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, test_cal.getTimeInMillis(), pending_Intent);
                    Log.d("알람", " \n\t알람시작 \t API23 <= VERSION (마쉬멜로우 6.0 이상) \n\t"+ test_Text);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarm_Manager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()+5000, pending_Intent);
                    Log.d("알람", " \n\t알람시작 \t API19 <= VERSION < API23 (KitKat 4.4 이상 - 마쉬멜로우 6.0 미만)");
                } else {
                    alarm_Manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()+5000, pending_Intent);
                    Log.d("알람", " \n\t알람시작 \t  VERSION < API19 (KitKat 4.4 미만)");
                }

                log_Text = String.format(
                        log_Text,
                        "생성",
                        dateFormat.format(dateTest),
                        dateFormat.format(test_cal.getTime())
                );
                break;

            case 2:     // action = 2, 알람 중지 Cancel
                alarm_Manager.cancel(pending_Intent);

                log_Text = String.format(
                        log_Text,
                        "해제",
                        dateFormat.format(dateTest),
                        dateFormat.format(dateTest)
                );
                break;
        }
        // 알람설정, 현재시간, 알람 설정시간 보여주기
        Log.d("알람", log_Text);
    }
}