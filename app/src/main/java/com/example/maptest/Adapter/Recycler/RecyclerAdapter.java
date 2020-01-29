package com.example.maptest.Adapter.Recycler;

import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.maptest.NetworkStatus;
import com.example.maptest.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPicture;
        TextView tv_storeName, tv_storeLocation;
        Geocoder geocoder;
        Double lat, lng;
        MyViewHolder(View view) {
            super(view);
            ivPicture = view.findViewById(R.id.iv_picture);
            tv_storeName= view.findViewById(R.id.tv_storename);
            tv_storeLocation = view.findViewById(R.id.tv_storelocation);
            geocoder = new Geocoder(view.getContext());

            //클릭 이벤트 처리 리스너 등록
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(), tv_storeLocation.getText(),Toast.LENGTH_SHORT).show();
                    String[] store_info = new String[]{tv_storeName.getText().toString(), tv_storeLocation.getText().toString()};
                    NetworkStatus.Check_NetworkStatus(view.getContext(), 4, store_info);
                    /*
                    List<Address> list = null;
                    String str = tv_storeLocation.getText().toString();         //클릭한 매장명 제외, 주소값 얻기
                    try {
                        list = geocoder.getFromLocationName
                                (str, // 지역 이름
                                        10); // 읽을 개수
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                    }

                    if (list != null) {
                        if (list.size() == 0) {
                            Toast.makeText(view.getContext(), "해당 주소없음", Toast.LENGTH_SHORT).show();
                        } else {
                            // 해당되는 주소로 인텐트 날리기
                            Address addr = list.get(0);

                            lat = addr.getLatitude();           //위도 저장
                            lng = addr.getLongitude();          //경도 저장
                            String[] storeinfo = new String[]{tv_storeName.getText().toString(),tv_storeLocation.getText().toString()};

                            Intent it = new Intent(view.getContext().getApplicationContext(), MapNaver.class);  //MapNaver액티비티 띄울목적
                            it.putExtra("TAG", 2);          //TAG값 전달 (int형)
                            it.putExtra("lat",lat);     //위도 전달
                            it.putExtra("lng",lng);     //경도 전달
                            it.putExtra("store",storeinfo); //String배열 매점정보 전달(매장명,주소)

                            // 눌린 view의 부모 프래그먼트의 부모 메인액티비티로 네이버맵 띄우기
                            view.getContext().getApplicationContext().startActivity(it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


                            //에러발생
                            //Calling startActivity() from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
                            //서비스는 태스크가 없기에 액티비티 시작에 new Task 플래그 설정 필요
                            //it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        }
                    }
                    */
                }
            });
        }
    }


    private ArrayList<RecyclerItem> foodInfoArrayList;

    public RecyclerAdapter(ArrayList<RecyclerItem> foodInfoArrayList) {
        this.foodInfoArrayList = foodInfoArrayList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;

        //myViewHolder.ivPicture.setImageResource(foodInfoArrayList.get(position).drawableId);
        myViewHolder.tv_storeName.setText(foodInfoArrayList.get(position).storeName);
        myViewHolder.tv_storeLocation.setText(foodInfoArrayList.get(position).storeLocation);
    }

    @Override
    public int getItemCount() {
        return foodInfoArrayList.size();
    }
}