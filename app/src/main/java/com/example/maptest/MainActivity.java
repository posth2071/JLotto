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

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String CLIENT_ID = "y0189tgx11"; // 클라이언트 아이디값
    public static final String CLIENT_SECRET = "NK87OTfxcF1JlVUt6acqMimoSKV5toNq5Y8v75IR"; // 시크릿값

    public static FragOne frag1;
    public static FragOneTwo frag11;
    FragTwo frag2;
    FragThree frag3;
    private Fragment fragment;
    public static int[] imgId = new int[45];
    public static int[] packid = new int[6];

    private DrawerLayout mDrawerLayout;         //최상단 DrawerLayout
    private NavigationView mNavigationView;     //네비게이션 뷰 (메뉴)
    private Context context = this;

    // 최신회차, 검색회차 저장
    public static LottoParsingInfo lastLottoinfo, searchLottoInfo;

    //네이버맵 지오코딩 보류
    BufferedReader br;
    StringBuilder searchResult;

    private int count = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //숫자이미지 1~45 아이디얻기
        getIdset();

        //상단 툴바연결
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //툴바 설정
        setSupportActionBar(toolbar);
        //시스템 액션바 얻기
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);        // 기존 title 지우기 (false - 지우지않음)
        actionBar.setDisplayHomeAsUpEnabled(true);      // 메뉴버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.plus); //메뉴버튼 이미지 지정
        //actionBar.setDisplayHomeAsUpEnabled(false);     // 메뉴버튼 없애기


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);    //DrawerLayout 연결(최상단 레이아웃)
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);     //네비게이션 뷰 연결(안보임)
        mNavigationView.setNavigationItemSelectedListener(this);            //네비게이션 뷰 아이템 클릭 처리리스너등록


        //로또 파싱하기
        TestClass testclass = new TestClass();
        lastLottoinfo = testclass.parsing("");
        searchLottoInfo = lastLottoinfo;

        frag1 = new FragOne();
        frag11 = new FragOneTwo();
        frag2 = new FragTwo();
        frag3 = new FragThree();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, frag1).commit();      //프래그먼트1 표시

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // 어떤 메뉴 아이템이 터치되었는지 확인
        switch (menuItem.getItemId()) {
            //바텀메뉴바 아이디
            case R.id.menuitem_bottombar_up:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, frag1) // 표시할 레이아웃, 변경할 프래그먼트 설정
                        .addToBackStack(null)          // 백스택에 변겅전 프래그먼트 저장
                        .commit();                     // 트랜잭션 실행
                return true;
            case R.id.menuitem_bottombar_down:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, frag2).addToBackStack(null).commit();
                return true;

            case R.id.menuitem_bottombar_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, frag3).addToBackStack(null).commit();
                return true;
                //슬라이드 메뉴바 아이디
            //고정수 설정
            case R.id.menu1_fixednums:
                Toast.makeText(context, menuItem.getTitle().toString() + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                SubMenu subMenu = mNavigationView.getMenu().getItem(1).getSubMenu();
                //setting visible조작하기
                //mNavigationView.getMenu().getItem(0).getSubMenu().setGroupVisible(R.id.group2,false);
                //setting 고정,제외 아이템 사이 추가하기
                //mNavigationView.getMenu().getItem(0).getSubMenu().add(R.id.group2,Menu.NONE,3,"테스트추가하기");
                subMenu.getItem(1).setTitle("고정수 변경하기");
                break;
            //제외수 설정
            case R.id.menu1_exceptnums:
                Toast.makeText(context, menuItem.getTitle().toString() + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                mNavigationView.getMenu().getItem(0).setVisible(true);
                break;
            //설정 초기화 (고정수,제외수)
            case R.id.menu1_reset:
                Toast.makeText(context, menuItem.getTitle().toString() + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                break;
            //회차검색
            case R.id.menu2_Search:
                Toast.makeText(context, menuItem.getTitle().toString() + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                frag2.dialogshow(1);
                break;
            //DB설정
            case R.id.menu2_DBSet:
                frag2.dialogshow(2);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Toast.makeText(context, menuItem.getTitle().toString() + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    //이미지 int형 id반환함수
    private void getIdset() {
        Resources res = getResources();
        for (int i = 1; i < 46; i++) {                //45번반복
            int stringId = res.getIdentifier("num" + i, "string", getPackageName());     // name에 해당하는 값의 위치 가져옴, 2131492892반환
            String stringId2 = res.getString(stringId);
            imgId[i - 1] = res.getIdentifier(stringId2, "drawable", getPackageName());
        }
        for(int i=1; i<7; i++)
            packid[i-1] = res.getIdentifier("pack"+i, "drawable", getPackageName());
    }

    //숫자집합 오름차순으로 정렬, 홀짝비율 계산
    public static String[] numsetSort(String numset){
        String[] numberset =  numset.split(",");

        int[] changeset = new int[numberset.length];           //numberset배열길이만큼 할당
        int paircount = 0;                                     //짝수개수 보관변수
        for(int i=0; i<numberset.length; i++){
            changeset[i] = Integer.parseInt(numberset[i]);
            if(changeset[i]%2==0)                               //짝수인지 확인 나머지가 0인경우
                paircount += 1;                                      //짝수 개수파악
        }
        int hallcount = numberset.length - paircount;                       //전체숫자개수중 짝수개수 제외 나머지는 홀수

        Arrays.sort(changeset);                                //오름차순으로 정렬
        String[] str = new String[2];
        str[0] = Arrays.toString(changeset).
                replace("[","").replace("]","").replace(" ","");
        str[1] = String.valueOf(hallcount)+":"+String.valueOf(paircount);
        Log.d("나눗셈",str[1]);
        return str;         //String배열반환(0번째 정렬된 숫자정보 / 1번째 홀짝개수)
    }

    //메뉴버튼 눌렀을때 콜백메서드 (여기서 메뉴편집)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("테스트", "onOptionsItemSelected 호출");
        int fragindex = checkFragment();        //현재 프래그먼트 확인용 int

        mNavigationView.getMenu().clear();      //메뉴 비우기(안하면 inflate시 추가로 쌓임)

        switch (item.getItemId()){
            case android.R.id.home:{            // 왼쪽 상단(메뉴버튼)일때
                switch (fragindex){             // 현재 프래그먼트 위치별로 메뉴세팅
                    //Frag_One
                    case 1:
                        mNavigationView.inflateMenu(R.menu.navi_menu1);
                        //mNavigationView.getMenu().getItem(0).setVisible(false);
                        //mNavigationView.getMenu().getItem(R.id.test1).getSubMenu().setGroupVisible(R.id.group2, false);
                        break;
                    //Frag_OneTwo
                    case 2:
                        mNavigationView.inflateMenu(R.menu.navi_menu1);
                        break;
                    //Frag_Two
                    case 3:
                        mNavigationView.inflateMenu(R.menu.navi_menu2);
                        break;
                    //Frag_Three
                    case 4:
                        mNavigationView.inflateMenu(R.menu.navi_menu2);
                        break;
                }
                mDrawerLayout.openDrawer(GravityCompat.START);  //Drawer열기 gravity.START (제일왼쪽위치부터 슬라이드 등장)
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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





    public void searchMap(final String searchObject){
        //final String client_Id = "y0189tgx11"; // 클라이언트 아이디값
        //final String client_Secret = "NK87OTfxcF1JlVUt6acqMimoSKV5toNq5Y8v75IR"; // 시크릿값
        final int display = 5;

        new Thread() {
            public void run() {
                try{
                    String text = URLEncoder.encode(searchObject, "UTF-8"); // searchObject = 로또
                    Log.d("확인",text);
                    String apiURL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query="+text+"&coordinate=127.1333510,36.995126"; // 좌표 lng, lat 순서
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
                    con.setRequestProperty("X-NCP-APIGW-API-KEY", CLIENT_SECRET);
                    con.connect();

                    int responseCode = con.getResponseCode();

                    if(responseCode == 200) {
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } else {
                        br = new BufferedReader((new InputStreamReader(con.getErrorStream())));
                    }
                    searchResult = new StringBuilder();
                    String inputLine;
                    while ((inputLine = br.readLine())!= null) {
                        searchResult.append(inputLine+ "\n");
                    }
                    br.close();
                    con.disconnect();

                    String data = searchResult.toString();
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("places");
                    String[] name = new String[5];
                    String[] addr_road = new String[5];
                    String[] addr_jibun = new String[5];
                    String[] phone = new String[5];
                    String[] x = new String[5];
                    String[] y = new String[5];
                    String[] distance = new String[5];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject lottoinfo = jsonArray.getJSONObject(i);
                        name[i] = lottoinfo.getString("name");
                        addr_road[i] = lottoinfo.getString("road_address");     //
                        addr_jibun[i] = lottoinfo.getString("jibun_address");   //지번주소
                        phone[i] = lottoinfo.getString("phone_number");         //전화번호
                        x[i] = lottoinfo.getString("x");                        //경도
                        y[i] = lottoinfo.getString("y");                        //위도
                        distance[i]  = lottoinfo.getString("distance");          //검색 중심 좌표와의 거리
                    }
                    for(int i=0; i<name.length; i++)
                        Log.d("확인", name[i]+", "+addr_road[i]+", "+addr_jibun[i]+", "+phone[i]+", "+x[i]+", "+y[i]+", "+distance[i]);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}