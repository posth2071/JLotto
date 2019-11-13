package com.example.maptest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;

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
import java.util.Arrays;

public class MapNaver extends FragmentActivity implements OnMapReadyCallback {
    Double lat, lng;
    String[] test = new String[2];
    LatLng latLng1;
    Intent it;
    public Marker[] markers = new Marker[5];
    // 내위치 주변 tag1일 경우
    private String[] name = new String[5];                  //판매 매장 이름
    private String[] addr_road = new String[5];             //도로명 주소
    private String[] addr_jibun = new String[5];            //지번 주소
    private String[] phone = new String[5];                 //매장번호
    private Double[] latitude = new Double[5];                     //위도
    private Double[] longtitude = new Double[5];                     //경도
    private String[] distance = new String[5];              //현재위치와의 거리 (단위 m)

    private BufferedReader br;
    private StringBuilder searchResult;
    private LocationManager locationManager;
    // private GPSListener gpsListener;
    private CameraUpdate cameraUpdate1;
    private LocationListener mLocationListener;
    LocationOverlay locationOverlay;

    NaverMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navermap);
        Log.d("지도", "Map 생성");             //맵 생명주기 (생성시)

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

        mMap = naverMap;
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

    public void setMap(int tag, final NaverMap naverMap) {
        if (tag == 1) {             // 1등매장 위치 찾기가 아닌경우
            Log.d("지도", "tag1 걸림");

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            //searchLotto("로또", naverMap);

            MapTask mtask = new MapTask(naverMap);
            Log.d("지도", "mtask.excute 실행");
            mtask.execute();

            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Double latitude = location.getLatitude();
                    Double longtitude = location.getLongitude();
                    Log.d("지도", latitude + ", " + longtitude);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    naverMap.moveCamera(CameraUpdate.scrollTo(latLng));      //현재 위치에 따라 카메라 이동
                    locationOverlay.setPosition(latLng);                    //위치 오버레이도 이동
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }

                @Override
                public void onProviderEnabled(String s) {
                }

                @Override
                public void onProviderDisabled(String s) {
                }
            };
            Log.d("지도","startLocationService() 실행");
            /*
             startLocationService(naverMap);
            naverMap.setLocationTrackingMode(LocationTrackingMode.Face);            //위치추적 설정
            naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {      //위치 추적 리스너
                @Override
                public void onLocationChange(@NonNull Location location) {
                    naverMap.moveCamera(CameraUpdate.scrollTo(new LatLng(location.getLatitude(), location.getLongitude())));
                    Log.d("지도", "위치추적변경이벤트 x - " + location.getLatitude() + ", y - " + location.getLongitude());
                }
            });
            locationOverlay = naverMap.getLocationOverlay();
            locationOverlay.setPosition(new LatLng(lat, lng));
            locationOverlay.setBearing(90);
            locationOverlay.setVisible(true);
             */



        } else if (tag == 2) {                  //1등 매장위치 보기인 경우
            Log.d("지도", "tag2 걸림");


            lat = it.getDoubleExtra("lat", 1);      //전달받은 위도 추출
            lng = it.getDoubleExtra("lng", 1);      //전달받은 경도 추출
            test = it.getStringArrayExtra("store");
            Toast.makeText(getApplicationContext(), lat + " " + lng, Toast.LENGTH_SHORT).show();

            Marker marker1 = new Marker();
            marker1.setPosition(new LatLng(lat, lng));
            marker1.setCaptionText("마커 표시");             //주캡션 - 마커 밑 텍스트
            marker1.setSubCaptionText(test[0]);             //suboCaption 보조캡션 - 주캡션없으면 표시 안됨
            Log.d("지도", test[0]);
            marker1.setSubCaptionColor(Color.RED);
            marker1.setSubCaptionHaloColor(Color.YELLOW);
            marker1.setSubCaptionTextSize(10);

            marker1.setMap(naverMap);
            Log.d("지도", "마커 생성");
            naverMap.moveCamera(CameraUpdate.scrollTo(new LatLng(lat, lng)));
        }


    }

    public void testmarker(NaverMap naverMap){
        Log.d("지도", "testmarker 실행");
        for(int i=0; i<5; i++){
            markers[i].setMap(naverMap);
        }
        Marker marker= new Marker();
        marker.setPosition(new LatLng(36.995715, 127.135610));
        marker.setCaptionText("테스트 마커");
        marker.setMap(naverMap);
    }
    public void startLocationService(final NaverMap naverMap) {
        Log.d("지도","startLocationService() 진입");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Log.d("지도","getLastKnowLocation 실행");
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);     //마지막 위치(제일최근)얻어와 저장
        if(location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        lat = location.getLatitude();
        lng = location.getLongitude();
        Log.d("지도", "LastLocation - " + location.getLatitude() + ", " + location.getLongitude());

        long minTime = 5000;                // 최소 시간(5초)
        float minDistance = 0;              // 최소 거리
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, mLocationListener);
    }

    /*
    //현위치 주변 로또판매점 찾기(최대 5개)
    public void searchLotto(final String searchObject, final NaverMap naverMap) {
        final String clientId = "y0189tgx11"; // 클라이언트 아이디값
        final String clientSecret = "NK87OTfxcF1JlVUt6acqMimoSKV5toNq5Y8v75IR"; // 시크릿값

        new Thread() {
            public void run() {
                try {
                    String text = URLEncoder.encode(searchObject, "UTF-8"); // searchObject = 로또
                    Log.d("확인", text);
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
                    String locationstr = String.valueOf(location.getLongitude())+","+String.valueOf(location.getLatitude());
                    Log.d("지도","locationstr -"+locationstr);
                    String apiURL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query="+text+"&coordinate="+locationstr; // 좌표 lng, lat 순서
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

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject lottoinfo = jsonArray.getJSONObject(i);
                        name[i] = lottoinfo.getString("name");                  //매장명
                        addr_road[i] = lottoinfo.getString("road_address");     //도로명주소
                        addr_jibun[i] = lottoinfo.getString("jibun_address");   //지번주소
                        phone[i] = lottoinfo.getString("phone_number");         //전화번호
                        x[i] = lottoinfo.getDouble("x");                        //경도
                        y[i] = lottoinfo.getDouble("y");                        //위도
                        distance[i]  = lottoinfo.getString("distance");         //검색 중심 좌표와의 거리
                    }
                    for(int i=0; i<name.length; i++) {
                        Log.d("지도", name[i] + ", " + addr_road[i] + ", " + addr_jibun[i] + ", " + phone[i] + ", " + x[i] + ", " + y[i] + ", " + distance[i]);
                        markers[i] = new Marker();
                        markers[i].setPosition(new LatLng(x[i],y[i]));
                        markers[i].setCaptionText(name[i]);
                        //markertest.setSubCaptionText();             //suboCaption 보조캡션 - 주캡션없으면 표시 안됨
                        //markertest.setSubCaptionColor(Color.RED);
                        //markertest.setSubCaptionHaloColor(Color.YELLOW);
                        //markertest.setSubCaptionTextSize(6);
                    }
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
     */
    class MapTask extends AsyncTask<Void, Void, Boolean> {
        final String clientId = "y0189tgx11"; // 클라이언트 아이디값
        final String clientSecret = "NK87OTfxcF1JlVUt6acqMimoSKV5toNq5Y8v75IR"; // 시크릿값
        NaverMap taskMap;
        public MapTask(NaverMap naverMap){
            this.taskMap = naverMap;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d("지도","MapTask doinBackground 실행");
            String text = null; // searchObject = 로또
            try {
                text = URLEncoder.encode("로또", "UTF-8");
                Log.d("지도", text);
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                }

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location == null)
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                String locationstr = String.valueOf(location.getLongitude()) + "," + String.valueOf(location.getLatitude());
                Log.d("지도", "locationstr -" + locationstr);
                String apiURL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=" + text + "&coordinate=" + locationstr; // 좌표 lng, lat 순서
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
                con.connect();

                int responseCode = con.getResponseCode();

                if (responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {
                    br = new BufferedReader((new InputStreamReader(con.getErrorStream())));
                }
                searchResult = new StringBuilder();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    searchResult.append(inputLine + "\n");
                }
                br.close();
                con.disconnect();

                Log.d("지도","\n"+"\tsearchResult - "+searchResult.toString());
                String data = searchResult.toString();
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("places");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject lottoinfo = jsonArray.getJSONObject(i);
                    name[i] = lottoinfo.getString("name");                  //매장명
                    addr_road[i] = lottoinfo.getString("road_address");     //도로명주소
                    addr_jibun[i] = lottoinfo.getString("jibun_address");   //지번주소
                    phone[i] = lottoinfo.getString("phone_number");         //전화번호
                    latitude[i] = lottoinfo.getDouble("y");                        // y - latitude
                    longtitude[i] = lottoinfo.getDouble("x");                        // x - longtitude
                    distance[i] = lottoinfo.getString("distance");         //검색 중심 좌표와의 거리
                }
                Log.d("지도", Arrays.toString(name));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            Log.d("지도","AsyncTask - onPostExecute진입");
            for(int i=0; i<name.length; i++) {
                Log.d("지도", name[i] + ", " + addr_road[i] + ", " + addr_jibun[i] + ", " + phone[i] + ", " + latitude[i] + ", " + longtitude[i] + ", " + distance[i]);
                //markers[i] = new Marker();
                //markers[i].setPosition(new LatLng(x[i], y[i]));
                //markers[i].setCaptionText(name[i]);
                //markers[i].setMap(mMap);
                markers[i] = new Marker();
                markers[i].setPosition(new LatLng(latitude[i],longtitude[i]));
                markers[i].setCaptionText(name[i]);
                markers[i].setMap(mMap);
            }
            for(int i=0; i<5; i++) {
                Log.d("지도", "Markers "+i+ " - " +markers[i].getCaptionText() + ", " + markers[i].getPosition());
            }
            startLocationService(mMap);
            mMap.setLocationTrackingMode(LocationTrackingMode.Face);            //위치추적 설정
            mMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {      //위치 추적 리스너
                @Override
                public void onLocationChange(@NonNull Location location) {
                    mMap.moveCamera(CameraUpdate.scrollTo(new LatLng(location.getLatitude(), location.getLongitude())));
                    Log.d("지도", "위치추적변경이벤트 x - " + location.getLatitude() + ", y - " + location.getLongitude());
                }
            });
            locationOverlay = mMap.getLocationOverlay();
            locationOverlay.setPosition(new LatLng(lat, lng));
            locationOverlay.setBearing(90);
            locationOverlay.setVisible(true);
            //testmarker(mMap);

            mMap.moveCamera(CameraUpdate.scrollTo(new LatLng(lat, lng)));
        }
    }
    /**
     * Destroy all fragments.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("지도", "Map 소멸");         // 맵 생명주기 (소멸시)

        //request리스너 작동중이면 중지시킴
        if(mLocationListener!=null)
            locationManager.removeUpdates(mLocationListener);
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