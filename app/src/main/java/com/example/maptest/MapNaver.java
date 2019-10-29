package com.example.maptest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

public class MapNaver extends FragmentActivity implements OnMapReadyCallback {
    Double lat, lng;
    String[] test = new String[2];
    LatLng latLng1;
    Intent it;

    private LocationManager locationManager;
    private GPSListener gpsListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navermap);
        Log.d("지도", "Map 생성");             //맵 생명주기 (생성시)

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

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

        it = getIntent();
        setMap(it.getIntExtra("TAG", 0), naverMap);
        /*
        if(tag.compareTo("1")==0){
            latLng1 = new LatLng(36.995143f,127.133372f);
            Log.d("지도", "if(tag==1 걸림");             //맵 생명주기 (생성시)
        } else if(tag.compareTo("2") == 0){
            lat = it.getDoubleExtra("lat",1);
            lng = it.getDoubleExtra("lng",1);
            test = it.getStringArrayExtra("store");
            Toast.makeText(getApplicationContext(),lat+" "+lng,Toast.LENGTH_SHORT).show();
            latLng1 = new LatLng( lat,lng);
            Log.d("지도", "if(tag==2 걸림");             //맵 생명주기 (생성시)
        }
        else{
            Log.d("지도", "if 안걸림");             //맵 생명주기 (생성시)
        }

        CameraUpdate cameraUpdate1 = CameraUpdate.scrollTo(latLng1);
        naverMap.moveCamera(cameraUpdate1);


         */
        //Marker marker1 = new Marker();
        //marker1.setPosition(latLng1);
        //marker1.setMap(naverMap);

        //marker1.setSubCaptionText(test[0]);
        //marker1.setSubCaptionColor(Color.RED);
        //marker1.setSubCaptionHaloColor(Color.YELLOW);
        //marker1.setSubCaptionTextSize(10);

        /*
        if(tag == "2"){
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

         */
    }

    public void setMap(int tag, NaverMap naverMap) {
        if (tag == 1) {
            startLocationService();
            Log.d("지도", "tag==1 걸림");             //맵 생명주기 (생성시)
        } else if (tag == 2) {
            lat = it.getDoubleExtra("lat", 1);
            lng = it.getDoubleExtra("lng", 1);
            test = it.getStringArrayExtra("store");
            Toast.makeText(getApplicationContext(), lat + " " + lng, Toast.LENGTH_SHORT).show();
            Log.d("지도", "tag==2 걸림");             //맵 생명주기 (생성시)
        }

        CameraUpdate cameraUpdate1 = CameraUpdate.scrollTo(new LatLng(lat,lng));
        naverMap.moveCamera(cameraUpdate1);

        if (tag == 1) {
            Marker marker1 = new Marker();
            marker1.setPosition(new LatLng(lat,lng));
            marker1.setCaptionText("마커 표시");
            marker1.setSubCaptionText("평택대학교");
            Log.d("지도", marker1.getCaptionText());
            marker1.setSubCaptionColor(Color.RED);
            marker1.setSubCaptionHaloColor(Color.YELLOW);
            marker1.setSubCaptionTextSize(10);

            marker1.setMap(naverMap);
            Log.d("지도", "마커 생성");
        } else if (tag == 2) {
            Marker marker1 = new Marker();
            marker1.setPosition(new LatLng(lat,lng));
            marker1.setCaptionText("마커 표시");             //주캡션 - 마커 밑 텍스트
            marker1.setSubCaptionText(test[0]);             //suboCaption 보조캡션 - 주캡션없으면 표시 안됨
            Log.d("지도", test[0]);
            marker1.setSubCaptionColor(Color.RED);
            marker1.setSubCaptionHaloColor(Color.YELLOW);
            marker1.setSubCaptionTextSize(10);

            marker1.setMap(naverMap);
            Log.d("지도", "마커 생성");
        }
    }

    public void startLocationService() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lat = location.getLatitude();
        lng = location.getLongitude();
        Log.d("지도", location.getLatitude()+", "+location.getLongitude());

        gpsListener = new GPSListener();
        long minTime = 3000;
        float minDistance = 0;

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);

    }

    private class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longtitude = location.getLongitude();
            Log.d("지도", latitude+", "+longtitude);
        }
        @Override
        public void onProviderEnabled(String s) { }

        @Override
        public void onProviderDisabled(String s) { }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
    }
    /**
     * Destroy all fragments.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("지도", "Map 소멸");         // 맵 생명주기 (소멸시)

        //request리스너 작동중이면 중지시킴
        if(gpsListener!=null)
            locationManager.removeUpdates(gpsListener);
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