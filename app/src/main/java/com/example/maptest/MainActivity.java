package com.example.maptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.naver.maps.map.MapFragment;
import com.naver.maps.map.OnMapReadyCallback;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    public static String[] store = new String[]{
            "헬로마트,서울 도봉구 도봉2동 89-148번지",
            "그랜드마트앞가판점,서울 마포구 노고산동 57-2번지",
            "월드24시,서울 은평구 갈현1동 398-1번지",
            "노다지복권방,부산 북구 구포동(구포제1동) 1060-51",
            "해뜰날수퍼,경기 화성시 기안동 338-3 103호",
            "왕대박복권,충남 논산시 연산면 임리 125-2번지 편의점",
            "로또복권,경북 구미시 송정동 459번지",
            "왕대박복권방,경북 문경시 모전동 81-89번지"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<RecyclerItem> foodInfoArrayList = new ArrayList<>();

        for(int i=0; i<MainActivity.store.length; i++){
            foodInfoArrayList.add(new RecyclerItem(R.drawable.imageone, store[i]));
        }
        RecyclerAdapter myAdapter = new RecyclerAdapter(foodInfoArrayList);

        mRecyclerView.setAdapter(myAdapter);

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