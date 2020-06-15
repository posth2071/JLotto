package com.JLotto.JLotto.Activity.FragMent_Three;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.JLotto.JLotto.Adapter.Recycler.RecyclerAdapter;
import com.JLotto.JLotto.Activity.MainActivity;
import com.JLotto.JLotto.R;
import com.JLotto.JLotto.Adapter.Recycler.RecyclerItem;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragThree extends Fragment  {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    TextView frag3_title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v("Fragment2","onCreateView 프래그먼트와 관련되는 뷰계층 만들어서 리턴");
        View view = inflater.inflate(R.layout.fragment_frag_three, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("Fragment3","onActivityCreated 연결된 액티비티 onCreate() 완료 후 호출");

        frag3_title = getActivity().findViewById(R.id.frag3_title);
        frag3_title.setText(String.format(
                "%d회차 1등 당첨매장 - %d개"
                , MainActivity.lastLottoinfo.getTurn()
                ,MainActivity.lastLottoinfo.getStoreList().size()
                ));
        mRecyclerView = getActivity().findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<RecyclerItem> foodInfoArrayList = new ArrayList<>();

        // 1등당첨매장 정보 ArrayList<String[]>, String배열 ->[0]매장명, [1]주소
        ArrayList<String[]> storeList = MainActivity.lastLottoinfo.getStoreList();

        // storeList길이 (1등당첨매장수)만큼 반복
        for(int i=0; i<storeList.size(); i++){
            foodInfoArrayList.add(new RecyclerItem(R.drawable.imageone,storeList.get(i)));
        }
        RecyclerAdapter myAdapter = new RecyclerAdapter(foodInfoArrayList);

        mRecyclerView.setAdapter(myAdapter);
    }
}
