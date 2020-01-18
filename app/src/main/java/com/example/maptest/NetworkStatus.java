package com.example.maptest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class NetworkStatus {
    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int TYPE_MOBILE = 1;
    public static final int TYPE_WIFI = 2;

    private static int METHOD_TYPE_LOADING = 1;
    private static int METHOD_TYPE_SEARCH = 2;
    private static int METHOD_TYPE_MAP = 3;

    public void NetworkStatus(){}

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

    public static void Check_NetworkStatus(final Context context, final int type){
        if (getConnectivity_Status(context) == 0) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("인터넷 에러");
            builder.setMessage("인터넷 연결 없음");
            builder.setCancelable(false);

            builder.setPositiveButton("재시도", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Check_NetworkStatus(context, type);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (type){
                        case 1: // Loading 화면에서 취소일 경우 전부 종료
                            ActivityCompat.finishAffinity(MainActivity.activity);
                            System.exit(0);
                            break;
                        case 2:
                            break;

                        case 3:
                            break;
                    }
                }
            });
            Log.d("네트워크", "builder 세팅 완료");
            builder.show();
            Log.d("네트워크", "builder 띄우기");
        } else {
            // 인터넷 상태가 연결되어 있는 경우
            switch (type){
                case 1:         // Loading 화면일때
                    ActivityCompat.finishAffinity(Loading.activity);
                    break;
                case 2:         // 회차검색 화면일때
                    MainActivity.frag2.dialogshow(1);
                    break;
                case 3:
                    break;
            }
        }

    }
}