package com.JLotto.JLotto.Adapter.Recycler;

import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.JLotto.JLotto.NetworkStatus;
import com.JLotto.JLotto.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPicture;
        TextView tv_storeName, tv_storeLocation;
        Geocoder geocoder;

        MyViewHolder(View view) {
            super(view);
            ivPicture = view.findViewById(R.id.iv_picture);
            tv_storeName = view.findViewById(R.id.tv_storename);
            tv_storeLocation = view.findViewById(R.id.tv_storelocation);
            geocoder = new Geocoder(view.getContext());

            //클릭 이벤트 처리 리스너 등록
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] store_info = new String[]{tv_storeName.getText().toString(), tv_storeLocation.getText().toString()};
                    NetworkStatus.Check_NetworkStatus(view.getContext(), 4, store_info);
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

        myViewHolder.tv_storeName.setText(foodInfoArrayList.get(position).storeName);
        myViewHolder.tv_storeLocation.setText(foodInfoArrayList.get(position).storeLocation);
    }

    @Override
    public int getItemCount() {
        return foodInfoArrayList.size();
    }


}