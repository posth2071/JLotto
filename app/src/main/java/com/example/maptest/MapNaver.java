package com.example.maptest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;

public class MapNaver extends FragmentActivity implements OnMapReadyCallback {
    Double lat, lng;
    String[] test = new String[2];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navermap);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull com.naver.maps.map.NaverMap naverMap) {
        naverMap.setMapType(com.naver.maps.map.NaverMap.MapType.Basic);
        naverMap.setSymbolScale(1.5f); // 심볼크기

        Intent it = getIntent();
        lat = it.getDoubleExtra("lat",1);
        lng = it.getDoubleExtra("lng",1);
        test = it.getStringArrayExtra("store");
        Toast.makeText(getApplicationContext(),lat+" "+lng,Toast.LENGTH_SHORT).show();

        LatLng latLng1 = new LatLng( lat,lng);
        CameraUpdate cameraUpdate1 = CameraUpdate.scrollTo(latLng1);
        naverMap.moveCamera(cameraUpdate1);

        Marker marker1 = new Marker();
        marker1.setPosition(latLng1);
        marker1.setMap(naverMap);

        marker1.setSubCaptionText(test[0]);
        marker1.setSubCaptionColor(Color.RED);
        marker1.setSubCaptionHaloColor(Color.YELLOW);
        marker1.setSubCaptionTextSize(10);

        InfoWindow infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this){
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return test[0]+"\n"+test[1];
            }
        });
        infoWindow.open(marker1);
    }
}
            /*
            naverMap.setSymbolScale(1.5f); // 심볼크기

        Intent it = getIntent();
        lat = it.getDoubleExtra("lat",1);
        lng = it.getDoubleExtra("lng",1);
        test = it.getStringArrayExtra("store");
        Toast.makeText(getApplicationContext(),lat+" "+lng,Toast.LENGTH_SHORT).show();

            LatLng latLng1 = new LatLng( lat,lng);
            CameraUpdate cameraUpdate1 = CameraUpdate.scrollTo(latLng1);
            naverMap.moveCamera(cameraUpdate1);

        Marker marker1 = new Marker();
        marker1.setPosition(latLng1);
        marker1.setMap(naverMap);

        marker1.setSubCaptionText(test[0]);
        marker1.setSubCaptionColor(Color.RED);
        marker1.setSubCaptionHaloColor(Color.YELLOW);
        marker1.setSubCaptionTextSize(10);

        InfoWindow infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this){
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return test[0]+"\n"+test[1];
            }
        });
        infoWindow.open(marker1);
    }
}


             */
// 36.995143 127.133372 평택대학교 좌표