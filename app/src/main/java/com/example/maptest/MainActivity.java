package com.example.maptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.OnMapReadyCallback;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    FragOne frag1;
    FragTwo frag2;
    FragThree frag3;

    Button bt1;
    public static Double lat, lng;
    public static String[] storeinfo = new String[2];

    public static String[] lastSet = new String[9];
    public static String[] searchSet = new String[9];
    public static String[] store = new String[8];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frag1 = new FragOne();
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
                Intent it = new Intent(getApplicationContext(), MapNaver.class);
                //it.putExtra("lat",lat);
                //it.putExtra("lng",lng);
                //it.putExtra("store",storeinfo);
                startActivity(it);
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
}
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