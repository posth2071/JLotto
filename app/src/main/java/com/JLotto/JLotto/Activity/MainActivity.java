package com.JLotto.JLotto.Activity;

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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.JLotto.JLotto.Activity.Dialog.DialogClass;
import com.JLotto.JLotto.AsyncTask.LottoParsingInfo;
import com.JLotto.JLotto.AsyncTask.TestClass;
import com.JLotto.JLotto.Activity.FragMent_One.FragOne;
import com.JLotto.JLotto.Activity.FragMent_One.FragOneTwo;
import com.JLotto.JLotto.Activity.FragMent_Three.FragThree;
import com.JLotto.JLotto.Activity.FragMent_Two.FragTwo;
import com.JLotto.JLotto.NetworkStatus;
import com.JLotto.JLotto.PreferenceManager;
import com.JLotto.JLotto.R;
import com.JLotto.JLotto.Util.Logger;
import com.facebook.stetho.Stetho;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    public static final String INFO_TURN = "      %d회차";      // 메뉴 info - turn 상수
    public static final String INFO_DATE = "      %s";          // 메뉴 info - date 상수
    public static final String INFO_NUMSET = "      %s";        // 메뉴 info - 제외수, 고정수 표시

    public static final int ALARM_ID = 1;

    public static int[] num_ID = new int[45];    //1~45숫자 이미지리소스 ID 저장배열
    public static int[] rotation_num_ID = new int[45];    // Rotation 1~45숫자 이미지리소스 ID 저장배열
    public static int num_null;
    public static int[] pack_ID = new int[8];    //Pack1~6 이미지리소스 ID 저장배열

    public static List<Integer> fixedNums = new ArrayList<>();  //최대 6개
    public static List<Integer> exceptNums = new ArrayList<>(); //최대 38개 (45 - 39 = 6)

    public static FragOne fragOne;
    public static FragOneTwo fragOneTwo;
    public static FragTwo fragTwo;
    public static FragThree fragThree;

    private DrawerLayout mDrawerLayout;         //최상단 DrawerLayout
    private NavigationView mNavigationView;     //네비게이션 뷰 (메뉴)

    public static Activity activity;
    public static Context context;

    public static LottoParsingInfo lastLottoinfo, searchLottoInfo;  // 최신회차, 검색회차 저장

    //네이버맵 지오코딩 보류
    BufferedReader br;
    StringBuilder searchResult;

    private Switch alarm_Switch;
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

        Stetho.initializeWithDefaults(this);

        activity = this;
        context = this;

        //로딩화면 띄우기
        Intent loading_it = new Intent(this, com.JLotto.JLotto.Activity.Loading_Activity.class);
        startActivity(loading_it);
        Logger.d("네트워크", "로딩화면 인텐트 시작 startActivity(loading_it)");

        permissionCheck(permissions_list);

        //숫자이미지(1~45) Pack(1~6) 이미지 아이디얻기
        getIdset();

        //상단 툴바연결
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //툴바 설정
        setSupportActionBar(toolbar);

        // 알람이 현재 존재중인지 확인 -> 미존재 일경우 알람 설정값을 확인 후 알람 설정
        PreferenceManager.first_Check(context);

        //시스템 액션바 얻기
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);        // 기존 title 지우기 (false - 지우지않음)
        actionBar.setDisplayHomeAsUpEnabled(true);      // 메뉴버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp); //메뉴버튼 이미지 지정


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);    //DrawerLayout 연결(최상단 레이아웃)
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);     //네비게이션 뷰 연결(안보임)
        mNavigationView.setNavigationItemSelectedListener(this);            //네비게이션 뷰 아이템 클릭 처리리스너등록
    
        Logger.d("네트워크", "최신 로또정보 파싱 실행");

        fragOne = new FragOne();
        fragOneTwo = new FragOneTwo();
        fragTwo = new FragTwo();
        fragThree = new FragThree();

        // 첫 프래그먼트 - FragTwo 표시
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragOne).commit();

        //바텀 네비게이션뷰 연결
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }


    //이미지 int형 id반환함수
    private void getIdset() {
        Resources res = getResources();
        num_null = res.getIdentifier("num_null", "drawable", getPackageName());
        for (int i = 1; i < 46; i++) {                //45번반복
            //int stringId = res.getIdentifier("num" + i, "string", getPackageName());     // name에 해당하는 값의 위치 가져옴, 2131492892반환
            String stringId2 = res.getString(res.getIdentifier("num" + i, "string", getPackageName()));
            num_ID[i - 1] = res.getIdentifier(stringId2, "drawable", getPackageName());

            //stringId = res.getIdentifier("rotation_num" + i, "string", getPackageName());     // name에 해당하는 값의 위치 가져옴, 2131492892반환
            stringId2 = res.getString(res.getIdentifier("rotation_num" + i, "string", getPackageName()));
            rotation_num_ID[i - 1] = res.getIdentifier(stringId2, "drawable", getPackageName());
        }
        for (int i = 1; i < 9; i++)
            pack_ID[i - 1] = res.getIdentifier("pack" + i, "drawable", getPackageName());
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

        String[] nums = numset.replaceAll(" ","").split(",");
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
        Logger.d("확인", "\n" +
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

            case 3:
                resultstr[0] = "5등";
                resultstr[1] = "#111111";
                return resultstr;
            default:
                resultstr[0] = "미당첨";
                resultstr[1] = "#b4b4b4";
                return resultstr;
        }
    }

    //메뉴버튼 눌렀을때 콜백메서드 (여기서 메뉴편집)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.d("메뉴", "onOptionsItemSelected 호출");
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
                        alarm_Setting(mNavigationView.getMenu().getItem(1).getSubMenu().getItem(0).getActionView());
                        break;

                    //Frag_OneTwo
                    case 2:
                        mNavigationView.inflateMenu(R.menu.navi_menu1);
                        set_mode("자동모드", false);    //모드, 그룹활성화 세팅
                        alarm_Setting(mNavigationView.getMenu().getItem(1).getSubMenu().getItem(0).getActionView());
                        break;
                    //Frag_Two
                    case 3:
                        mNavigationView.inflateMenu(R.menu.navi_menu2);
                        SubMenu subMenu2 = mNavigationView.getMenu().getItem(0).getSubMenu();
                        subMenu2.getItem(0).setTitle(String.format(INFO_TURN, searchLottoInfo.getTurn()));
                        subMenu2.getItem(1).setTitle(String.format(INFO_DATE, searchLottoInfo.getDate()));

                        alarm_Setting(mNavigationView.getMenu().getItem(1).getSubMenu().getItem(0).getActionView());
                        break;
                    //Frag_Three
                    case 4:
                        mNavigationView.inflateMenu(R.menu.navi_menu3);
                        alarm_Setting(mNavigationView.getMenu().getItem(1).getSubMenu().getItem(0).getActionView());
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
                        .replace(R.id.fragment, fragOne) // 표시할 레이아웃, 변경할 프래그먼트 설정
                        .addToBackStack(null)          // 백스택에 변겅전 프래그먼트 저장
                        .commit();                     // 트랜잭션 실행
                return true;
            case R.id.bottombar_two:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragTwo).addToBackStack(null).commit();
                return true;

            case R.id.bottombar_three:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragThree).addToBackStack(null).commit();
                return true;


            //슬라이드 메뉴바 아이디
            // 메뉴1 - 모드변경 눌린경우
            case R.id.menu1_mode:
                mDrawerLayout.closeDrawer(mNavigationView);
                int fragindex = checkFragment();
                if(fragindex==1){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, MainActivity.fragOneTwo).addToBackStack(null).commit();
                } else if(fragindex==2){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, MainActivity.fragOne).addToBackStack(null).commit();
                }
                break;

            // 메뉴1 - 고정수 설정
            case R.id.menu1_fixednums:
                mDrawerLayout.closeDrawer(mNavigationView);
                new DialogClass(context, 4, "고정수").show();
                break;

            // 메뉴1 - 제외수 설정
            case R.id.menu1_exceptnums:
                mDrawerLayout.closeDrawer(mNavigationView);
                new DialogClass(context, 4, "제외수").show();
                break;

            // 메뉴1 -설정 초기화 (고정수,제외수)
            case R.id.menu1_reset:
                mDrawerLayout.closeDrawer(mNavigationView);
                exceptNums.clear();
                fixedNums.clear();
                Logger.d("메뉴", "reset 적용 \n\t exceptNums size - " + exceptNums.size() + "\n\t fixedNums size - " + fixedNums.size());
                break;

            // 메뉴2 - 회차검색
            case R.id.menu2_Search:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                fragTwo.dialogshow(1);

                //// 인터넷 연결상태 확인 후 실행
                //NetworkStatus.Check_NetworkStatus(context, 2, null);

                //frag2.dialogshow(1);
                break;

            // 메뉴2 - DB설정
            case R.id.menu2_DBSet:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                fragTwo.dialogshow(2);
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

        Logger.d("테스트", String.format(
                new StringBuffer("현재 프래그먼트")
                        .append("\n\tFragOne instanceOf '%b'")
                        .append("\n\tFragOneTwo instanceOf '%b'")
                        .append("\n\tFragTwo instanceOf '%b'")
                        .append("\n\tFragThree instanceOf '%b'")
                        .toString(),
                fragment instanceof FragOne,
                fragment instanceof FragOneTwo,
                fragment instanceof FragTwo,
                fragment instanceof FragThree));

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
        mNavigationView.getMenu().getItem(1).getSubMenu().getItem(1).setEnabled(state); //고정수 설정
        mNavigationView.getMenu().getItem(1).getSubMenu().getItem(2).setEnabled(state); //제외수 설정
        mNavigationView.getMenu().getItem(1).getSubMenu().getItem(3).setEnabled(state); //reset 설정
        //setting그룹 제외수,고정수 리스트 숨기기/보이기
        mNavigationView.getMenu().getItem(2).getSubMenu().getItem(0).setVisible(state);
        mNavigationView.getMenu().getItem(2).getSubMenu().getItem(1).setVisible(state);
        mNavigationView.getMenu().getItem(2).getSubMenu().getItem(2).setVisible(state);
        mNavigationView.getMenu().getItem(2).getSubMenu().getItem(3).setVisible(state);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            String qr_result = scanResult.getContents();

            NetworkStatus.Check_NetworkStatus(context, 5, new String[]{qr_result});
            /*
            Logger.d("바코드", qr_result);
            // 동행복권 QR코드인지 검사
            if(qr_result.contains("dhlottery.co.kr")){
                CustomDialog customDialog = new CustomDialog(this, qr_result);
            } else {
                // 동행복권 QR코드 아닌경우
                MyToast.makeText(this, "QR코드 오류");(this, "QR코드 오류", Toast.LENGTH_SHORT).show();
            }

             */
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

    private void alarm_Setting(View view){
        alarm_Switch = view.findViewById(R.id.menu_swtich);
        boolean alarm_State = PreferenceManager.getBoolean(context);
        alarm_Switch.setChecked(alarm_State);
        alarm_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceManager.setBoolean(context, isChecked);
            }
        });
    }


}
