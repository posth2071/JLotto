package com.example.maptest.Activity.FragMent_Three;

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

import com.example.maptest.Adapter.Recycler.RecyclerAdapter;
import com.example.maptest.Activity.MainActivity;
import com.example.maptest.R;
import com.example.maptest.Adapter.Recycler.RecyclerItem;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragThree extends Fragment  {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    TextView frag3_title;
    public FragThree() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v("Fragment2","onCreateView 프래그먼트와 관련되는 뷰계층 만들어서 리턴");
        View view = inflater.inflate(R.layout.fragment_frag_three, container, false);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Fragment3","onAttach 프래그먼트가 액티비티와 연결시 호출");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Fragment3","onCreate 프래그먼트 초기화시 호출");
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

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Fragment3","onStart 사용자에게 프래그먼트 보일 때");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Fragment3","onResume 사용자와 상호작용 가능");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("Fragment3","onPause 연결된 액티비티가 onPause()되어 사용자와 상호작용 중지시 호출");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Fragment3","onStop 연결 액티비티가 onStop()되어 화면에서 더이상 보이지x / 프래그먼트 기능 중지시");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v("Fragment3","onDestroyView 프래그먼트 관련 뷰 리소스 해재할 수 있도록 호출");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("Fragment3","onDestroy 프래그먼트 상태를 마지막으로 정리할 수 있도록 호출");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Fragment3","onDetach 액티비티와 연결 끊기 바로전 호출");
    }


}
