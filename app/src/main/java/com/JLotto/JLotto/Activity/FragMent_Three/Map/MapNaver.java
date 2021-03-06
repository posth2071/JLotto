package com.JLotto.JLotto.Activity.FragMent_Three.Map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.JLotto.JLotto.R;
import com.JLotto.JLotto.Util.Logger;
import com.JLotto.JLotto.Util.MyToast;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class MapNaver extends FragmentActivity implements OnMapReadyCallback {
    private final String clientId = "y0189tgx11"; // 클라이언트 아이디값
    private final String clientSecret = "NK87OTfxcF1JlVUt6acqMimoSKV5toNq5Y8v75IR"; // 시크릿값

    private Double lat, lng;
    private String[] store_name = new String[2];
    private Intent it;
    private int tag;

    public Marker[] markers = new Marker[5];
    public Marker marker;
    // 내위치 주변 tag1일 경우
    private String[] name = new String[5];                  //판매 매장 이름
    private String[] addr_road = new String[5];             //도로명 주소
    private String[] addr_jibun = new String[5];            //지번 주소
    private String[] phone = new String[5];                 //매장번호
    private Double[] latitude = new Double[5];                     //위도
    private Double[] longtitude = new Double[5];                     //경도
    private String[] distance = new String[5];              //현재위치와의 거리 (단위 m)

    private String first_name;
    private String first_addr_road;
    private String first_addr_jibun;            //지번 주소
    private String first_phone;                 //매장번호
    private Double first_latitude;                     //위도
    private Double first_longtitude;                     //경도
    private String first_distance;              //현재위치와의 거리 (단위 m)

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
        Logger.d("지도", "Map 액티비티 생성");             //맵 생명주기 (생성시)

        it = getIntent();
        tag = it.getIntExtra("TAG", 0);
        if(tag == 2){
            store_name = it.getStringArrayExtra("store");
        }

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
        //it = getIntent();
        //setMap(it.getIntExtra("TAG", 0), naverMap);
        setMap(tag, naverMap);
    }

    // TAG 1 - 현재위치 주변 매장찾기
    // TAG 2 - 1등 당첨매장 위치 찾기
    public void setMap(int tag, final NaverMap naverMap) {
        // 현재위치 주변 매장찾기 인경우
        if (tag == 1) {
            Logger.d("지도", "SetMap - tag1 걸림");
            // 디바이스 위치를 얻어올 LocationManager 호출
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            //searchLotto("로또", naverMap);

            MapTask mtask = new MapTask(naverMap, 1);
            mtask.execute("로또");

            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Double latitude = location.getLatitude();
                    Double longtitude = location.getLongitude();
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
            Logger.d("지도","startLocationService() 실행");
            /*
             startLocationService(naverMap);
            naverMap.setLocationTrackingMode(LocationTrackingMode.Face);            //위치추적 설정
            naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {      //위치 추적 리스너
                @Override
                public void onLocationChange(@NonNull Location location) {
                    naverMap.moveCamera(CameraUpdate.scrollTo(new LatLng(location.getLatitude(), location.getLongitude())));
                    Logger.d("지도", "위치추적변경이벤트 x - " + location.getLatitude() + ", y - " + location.getLongitude());
                }
            });
            locationOverlay = naverMap.getLocationOverlay();
            locationOverlay.setPosition(new LatLng(lat, lng));
            locationOverlay.setBearing(90);
            locationOverlay.setVisible(true);
             */

        } else if (tag == 2) {                  //1등 매장위치 보기인 경우
            Logger.d("지도", "SetMap - tag2 걸림");

            List<Address> list = null;
            try {
                Geocoder geocoder = new Geocoder(getApplicationContext());
                list = geocoder.getFromLocationName
                        (store_name[1], // 지역 주소
                                10); // 읽을 개수
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
            }
            if (list != null) {
                if (list.size() == 0) {
                    MyToast.makeText(MapNaver.this, "해당 주소없음");
                    finish();
                } else {
                    Address addr = list.get(0);
                    lat = addr.getLatitude();
                    lng = addr.getLongitude();
                }
            }

            MapTask mtask = new MapTask(naverMap, 2);
            mtask.execute(new String[]{store_name[0], String.valueOf(lng), String.valueOf(lat)});
        }
    }

    public void startLocationService(final NaverMap naverMap) {
        Logger.d("지도","startLocationService() 진입");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Logger.d("지도","getLastKnowLocation 실행");
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);     //마지막 위치(제일최근)얻어와 저장
        if(location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        lat = location.getLatitude();
        lng = location.getLongitude();
        Logger.d("지도", "디바이스 위치 - " + lat + ", " + lng);

        long minTime = 5000;                // 최소 시간(5초)
        float minDistance = 0;              // 최소 거리
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, mLocationListener);
    }

    class MapTask extends AsyncTask<String, Void, Boolean> {
        NaverMap taskMap;
        int type;
        public MapTask(NaverMap naverMap, int type){
            this.taskMap = naverMap;
            this.type = type;
        }

        @Override
        protected Boolean doInBackground(String... searchText) {
            Logger.d("지도","MapTask doinBackground 실행");
            String text = null; // searchObject = 로또
            try {
                text = URLEncoder.encode(searchText[0], "UTF-8");
                Logger.d("지도", searchText[0]);
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}

                String locationstr = "";

                if(searchText[0].compareTo("로또") == 0){
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    if(location != null) {
                        locationstr = location.getLongitude() + "," + location.getLatitude();
                    } else {
                        //37.576063, 126.976845
                        locationstr = "126.976845, 37.576063";
                        Logger.d("맵", "마지막 위치정보 없음");
                    }
                } else if(type == 2) {
                    locationstr = searchText[1] + "," + searchText[2];
                }

                URL url = new URL(String.format(
                        "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=%s&coordinate=%s",
                        text, locationstr
                ));

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

                Logger.d("지도"," \n\tsearchResult - "+searchResult.toString());
                String data = searchResult.toString();
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("places");

                if(type == 1){
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
                    Logger.d("지도", Arrays.toString(name));

                } else if(type == 2){
                    JSONObject lottoinfo = jsonArray.getJSONObject(0);
                    first_name = lottoinfo.getString("name");
                    first_addr_road = lottoinfo.getString("road_address");     //도로명주소
                    first_addr_jibun = lottoinfo.getString("jibun_address");   //지번주소
                    first_phone = lottoinfo.getString("phone_number");         //전화번호
                    first_latitude = lottoinfo.getDouble("y");                        // y - latitude
                    first_longtitude = lottoinfo.getDouble("x");                        // x - longtitude
                    first_distance = lottoinfo.getString("distance");         //검색 중심 좌표와의 거리
                    Logger.d("지도", first_name);
                }

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
            Logger.d("지도","AsyncTask - onPostExecute진입");
            if(type==1){
                for(int i=0; i<name.length; i++) {
                    Logger.d("지도", name[i] + ", " + addr_road[i] + ", " + addr_jibun[i] + ", " + phone[i] + ", " + latitude[i] + ", " + longtitude[i] + ", " + distance[i]);
                    markers[i] = new Marker();
                    markers[i].setPosition(new LatLng(latitude[i],longtitude[i]));

                    markers[i].setCaptionText(name[i]);             //주캡션 - 마커 밑 텍스트
                    markers[i].setCaptionColor(Color.parseColor("#66cdaa"));

                    markers[i].setSubCaptionText(addr_road[i]);     //suboCaption 보조캡션 - 주캡션없으면 표시 안됨
                    markers[i].setSubCaptionColor(Color.GRAY);
                    markers[i].setSubCaptionTextSize(8);

                    markers[i].setTag(i);           //클릭이벤트 처리때 구분자용 tag
                    markers[i].setOnClickListener(new Overlay.OnClickListener() {
                        @Override
                        public boolean onClick(@NonNull Overlay overlay) {
                            int tag = (int)overlay.getTag();
                            //번호 길이가 2이상이면 다이얼하기
                            if(phone[tag].length()>2){
                                String tel = "tel:" + phone[tag];
                                Intent it = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
                                startActivity(it);
                            } else{
                                MyToast.makeText(getApplicationContext(), "번호 미등록 매장");
                            }
                            return false;
                        }
                    });
                    markers[i].setMap(mMap);

                }
                for(int i=0; i<5; i++) {
                    Logger.d("지도", "Markers "+i+ " - " +markers[i].getCaptionText() + ", " + markers[i].getPosition());
                }
                startLocationService(mMap);
                mMap.setLocationTrackingMode(LocationTrackingMode.Face);            //위치추적 설정
                mMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {      //위치 추적 리스너
                    @Override
                    public void onLocationChange(@NonNull Location location) {
                        mMap.moveCamera(CameraUpdate.scrollTo(new LatLng(location.getLatitude(), location.getLongitude())));
                        Logger.d("지도", "위치추적변경이벤트 x - " + location.getLatitude() + ", y - " + location.getLongitude());
                    }
                });
                locationOverlay = mMap.getLocationOverlay();
                locationOverlay.setPosition(new LatLng(lat, lng));
                locationOverlay.setBearing(90);
                locationOverlay.setVisible(true);
                //testmarker(mMap);

                mMap.moveCamera(CameraUpdate.scrollTo(new LatLng(lat, lng)));

            } else if(type == 2){
                Logger.d("지도", String.format(new StringBuilder()
                            .append(" \n\t매장명 - %s")
                            .append(" \n\t도로명 주소 - %s")
                            .append(" \n\t지번 주소 - %s")
                            .append(" \n\t전화번호 - %s")
                            .append(" \n\t좌표 - Lattitude(%f) / Longtitude(%f)")
                            .append(" \n\t거리 Distance - %s")
                                .toString(),
                        first_name, first_addr_road, first_addr_jibun, first_phone, first_latitude, first_longtitude, first_distance
                ));

                marker = new Marker();
                // 좌표 Lattitude, Longtitude 중 하나라도 얻어오지 못했다면 else 실행
                if((first_latitude != null) || (first_longtitude != null)) {
                    marker.setPosition(new LatLng(first_latitude, first_longtitude));
                    marker.setCaptionText(first_name);             //주캡션 - 마커 밑 텍스트
                    marker.setSubCaptionText(first_addr_road);     //suboCaption 보조캡션 - 주캡션없으면 표시 안됨
                } else {
                    marker.setPosition(new LatLng(lat,lng));
                    marker.setCaptionText(store_name[0]);
                    marker.setSubCaptionText(store_name[1]);
                }

                marker.setCaptionColor(Color.parseColor("#66cdaa"));
                marker.setSubCaptionColor(Color.GRAY);
                marker.setSubCaptionTextSize(8);

                marker.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {
                        //번호 길이가 2이상이면 다이얼하기
                        if(first_phone != null){
                            String tel = "tel:" + first_phone;
                            Intent it = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
                            startActivity(it);
                        } else{
                            MyToast.makeText(MapNaver.this, "번호 미등록 매장");
                        }
                        return false;
                    }
                });
                marker.setMap(mMap);
                mMap.moveCamera(CameraUpdate.scrollTo(marker.getPosition()));
            }
            if(type == 1){
                for(int i=0; i<5; i++) {
                    Logger.d("지도", "Markers "+i+ " - " +markers[i].getCaptionText() + ", " + markers[i].getPosition());
                }
            } else if(type == 2){
                Logger.d("지도", "Marker - " +marker.getCaptionText() + ", " + marker.getPosition());
            }

        }
    }
    /**
     * Destroy all fragments.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("지도", "Map 소멸");         // 맵 생명주기 (소멸시)

        //request리스너 작동중이면 중지시킴
        if(mLocationListener!=null)
            locationManager.removeUpdates(mLocationListener);
    }
}