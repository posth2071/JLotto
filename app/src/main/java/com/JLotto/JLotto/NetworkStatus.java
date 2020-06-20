package com.JLotto.JLotto;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.JLotto.JLotto.Activity.Dialog.DialogClass;
import com.JLotto.JLotto.Activity.FragMent_Two.QRCord.CustomDialog;
import com.JLotto.JLotto.Activity.Loading_Activity;
import com.JLotto.JLotto.Activity.FragMent_Three.Map.MapNaver;
import com.JLotto.JLotto.Util.Logger;
import com.JLotto.JLotto.Util.MyToast;

import java.io.IOException;
import java.util.List;

public class NetworkStatus {
    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int TYPE_MOBILE = 1;
    public static final int TYPE_WIFI = 2;

    private static int METHOD_TYPE_LOADING = 1;
    private static int METHOD_TYPE_SEARCH = 2;
    private static int METHOD_TYPE_MAP_MENU = 3;
    private static int METHOD_TYPE_MAP_FIRST_STORE = 4;

    // 네트워크 연결상태를 얻기 위한 메소드
    public static int getConnectivity_Status(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // 네트워크 연결상태 얻어오기 - 연결안되었다면 null 반환
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // 연결된 상태라면 (null이 아니라면)
        if(networkInfo != null) {
            // 연결된 네트워크 종류 얻기
            int type = networkInfo.getType();
            // 모바일 네트워크에 연결된 상태라면 - int 1 반환
            if (type == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
                // 와이파이에 연결된 상태라면 - int 2 반환
            } else if (type == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }
        }
        // 연결이 되지않았다면 - int 0 반환
        return TYPE_NOT_CONNECTED;
    }

    public static void Check_NetworkStatus(final Context context, final int network_type, final String[] store){
         if (getConnectivity_Status(context) == 0) {
            Logger.d("다이얼로그", "DialogClass 생성자 함수 실행");
            new DialogClass(context, 3, network_type, store).show();
            Logger.d("다이얼로그", "DialogClass.show()");
        } else {
            // 인터넷 상태가 연결되어 있는 경우
            switch (network_type){
                case 1:         // Loading_Activity 화면일때
                    ActivityCompat.finishAffinity(Loading_Activity.activity);
                    break;
                case 2:         // 회차검색 화면일때
                    //MainActivity.frag2.dialogshow(1);
                    DialogClass.dialogListener.onPositiveClicked(store[0]);
                    break;
                case 3:
                    Intent it = new Intent(context, MapNaver.class);  //MapNaver액티비티 띄울목적
                    it.putExtra("TAG", 1);          //TAG값 전달 (int형)
                    context.startActivity(it);
                    break;
                case 4:
                    List<Address> list = null;
                    try {
                        Geocoder geocoder = new Geocoder(context);
                        list = geocoder.getFromLocationName
                                (store[1], // 지역 주소
                                        10); // 읽을 개수
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                    }

                    if (list != null) {
                        if (list.size() == 0) {
                            MyToast.makeText(context, "해당 주소없음");
                        } else {
                            // 해당되는 주소로 인텐트 날리기
                            Address addr = list.get(0);

                            Intent intent = new Intent(context, MapNaver.class);  //MapNaver액티비티 띄울목적
                            intent.putExtra("TAG", 2);          //TAG값 전달 (int형)
                            intent.putExtra("lat",addr.getLatitude());           //위도 저장);     //위도 전달
                            intent.putExtra("lng",addr.getLongitude());          //경도 저장);     //경도 전달
                            intent.putExtra("store", store); //String배열 매점정보 전달(매장명,주소)

                            // 눌린 view의 부모 프래그먼트의 부모 메인액티비티로 네이버맵 띄우기
                            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    }


                    Intent intent = new Intent(context, MapNaver.class);  //MapNaver액티비티 띄울목적
                    intent.putExtra("TAG", 2);          //TAG값 전달 (int형)
                    intent.putExtra("store", store); //String배열 매점정보 전달(매장명,주소)

                    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    break;

                case 5:
                    String qr_result = store[0];
                    Logger.d("바코드", qr_result);
                    // 동행복권 QR코드인지 검사
                    if(qr_result.contains("dhlottery.co.kr")){
                        CustomDialog customDialog = new CustomDialog(context, qr_result);
                    } else {
                        // 동행복권 QR코드 아닌경우
                        MyToast.makeText(context, "QR코드 오류");
                    }
                    break;
            }

        }
    }
}