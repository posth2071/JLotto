package com.example.maptest;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragOne extends Fragment implements View.OnClickListener {
    Button bt_random, bt_change;
    TextView tv_result;
    ImageView[] iv_num = new ImageView[7];

    private int[] numbers = new int[7];
    private int num = 0;

    //생성자
    public FragOne() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_one, container, false);

        iv_num[0] = (ImageView)view.findViewById(R.id.iv_num1);
        iv_num[1] = (ImageView)view.findViewById(R.id.iv_num2);
        iv_num[2] = (ImageView)view.findViewById(R.id.iv_num3);
        iv_num[3] = (ImageView)view.findViewById(R.id.iv_num4);
        iv_num[4] = (ImageView)view.findViewById(R.id.iv_num5);
        iv_num[5] = (ImageView)view.findViewById(R.id.iv_num6);
        iv_num[6] = (ImageView)view.findViewById(R.id.iv_num7);

        tv_result = view.findViewById(R.id.tv_result);
        bt_random = view.findViewById(R.id.bt_random);
        bt_random.setOnClickListener(this);

        bt_change = view.findViewById(R.id.bt_change);
        bt_change.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_random:
                start();                        // 랜덤번호 뽑기 메소드 실행
               break;
            case R.id.bt_change:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment, MainActivity.frag11).addToBackStack(null).commit();
                break;
        }
    }
    public void start(){
        tv_result.setText("");                 // TextView 초기화
        for(int i=0; i<7; i++){
            random();
            if(num==6)
                tv_result.append(numbers[num]+"");
            else
                tv_result.append(numbers[num]+" + ");
            num++;
        }
        num = 0;
    }

    public void random(){
        numbers[num] = (int)(Math.random()*45)+1;
        for(int j=0; j<num; j++){
            if(numbers[num]== numbers[j])
                random();
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Fragment1","onAttach 프래그먼트가 액티비티와 연결시 호출");
        //((MainActivity)context).pushOnBackKeyPressedListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Fragment1","onCreate 프래그먼트 초기화시 호출");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("Fragment1","onActivityCreated 연결된 액티비티 onCreate() 완료 후 호출");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Fragment1","onStart 사용자에게 프래그먼트 보일 때");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Fragment1","onResume 사용자와 상호작용 가능");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("Fragment1","onPause 연결된 액티비티가 onPause()되어 사용자와 상호작용 중지시 호출");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Fragment1","onStop 연결 액티비티가 onStop()되어 화면에서 더이상 보이지x / 프래그먼트 기능 중지시");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v("Fragment1","onDestroyView 프래그먼트 관련 뷰 리소스 해재할 수 있도록 호출");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("Fragment1","onDestroy 프래그먼트 상태를 마지막으로 정리할 수 있도록 호출");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Fragment1","onDetach 액티비티와 연결 끊기 바로전 호출");
    }
}
