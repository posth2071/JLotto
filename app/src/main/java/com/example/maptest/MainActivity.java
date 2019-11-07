package com.example.maptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    public static FragOne frag1;
    public static FragOneTwo frag11;
    FragTwo frag2;
    FragThree frag3;

    Button bt1;
    public static Double lat, lng;
    public static String[] storeinfo = new String[2];

    public static String[] lastSet = new String[9];
    public static String[] searchSet = new String[9];
    public static String[] store = new String[8];

    BufferedReader br;
    StringBuilder searchResult;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frag1 = new FragOne();
        frag11 = new FragOneTwo();
        frag2 = new FragTwo();
        frag3 = new FragThree();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, frag1).commit();      //프래그먼트1 표시

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        TestClass testclass = new TestClass();
        lastSet = testclass.parsing("");

        bt1 = findViewById(R.id.bt1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //searchMap("로또");              //주변 로또판매 매장 검색 (최대 5개)
                Intent it = new Intent(view.getContext(), MapNaver.class);
                it.putExtra("TAG",1);
                view.getContext().startActivity(it);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // 어떤 메뉴 아이템이 터치되었는지 확인합니다.
        switch (menuItem.getItemId()) {
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
        }
        return false;
    }

    public void searchMap(final String searchObject){
        final String clientId = "y0189tgx11"; // 클라이언트 아이디값
        final String clientSecret = "NK87OTfxcF1JlVUt6acqMimoSKV5toNq5Y8v75IR"; // 시크릿값
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
                    con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                    con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
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
/*


 */
    /*
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setMapType(NaverMap.MapType.Basic);
    }
}

     */