package com.example.maptest;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragTwo extends Fragment implements View.OnClickListener {
    TextView frag2_tv1 ,frag2_tv2;              //private 설정시 에러발생
    Button frag2_btsearch, frag2_btlast;
    EditText frag2_et1;
    ExpandableListView frag2_expandable;

    ArrayList<String> mGroupList = null;

    public static String str = new String();
    public String[] search = new String[9];

    public FragTwo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v("Fragment3","onCreateView 프래그먼트와 관련되는 뷰계층 만들어서 리턴");
        View view = inflater.inflate(R.layout.fragment_frag_two, container, false);

        frag2_tv1 = view.findViewById(R.id.frag2_tv1);

        frag2_et1 = view.findViewById(R.id.frag2_et1);

        frag2_btsearch = view.findViewById(R.id.frag2_btsearch);
        frag2_btsearch.setOnClickListener(this);
        frag2_btlast = view.findViewById(R.id.frag2_btlast);
        frag2_btlast.setOnClickListener(this);

        frag2_expandable = view.findViewById(R.id.frag2_Expandable);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.frag2_btsearch:           // 검색버튼 누른경우
                dialogshow();                   // 검색 대화상자 띄우기
                break;
            case R.id.frag2_btlast:

                break;
        }
    }
    public void dialogshow(){
        DialogClass dialog = new DialogClass(getActivity());        // 대화상자 클래스 객체 생성 (getActivity로 프래그먼트가 올라와있는 액티비티 가져오기)
        dialog.setDialogListener(new MyDialogListener() {           // 리스너 인터페이스 등록
            @Override
            public void onPositiveClicked(String num) {             // 재정의
                TestClass testclass = new TestClass();
                MainActivity.searchLottoInfo = testclass.parsing(num);
            }
            @Override
            public void onNegativeClicked() {
            }
        });
        dialog.show();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Fragment2","onAttach 프래그먼트가 액티비티와 연결시 호출");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Fragment2","onCreate 프래그먼트 초기화시 호출");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("Fragment2","onActivityCreated 연결된 액티비티 onCreate() 완료 후 호출");
        String str = Arrays.toString(MainActivity.lastLottoinfo.getLottoInfo())
                .replace("[","")
                .replace("]","");
        frag2_tv1.setText(str);
        //for(int i=0; i<MainActivity.lastSet.length; i++){
          //  frag2_tv1.append(MainActivity.lastSet[i]+" ");
        //}
        //for(int i=0; i<MainActivity.store.length; i++){
          //  //frag2_tv2.append(MainActivity.store[i]+"\n");
        //}
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Fragment2","onStart 사용자에게 프래그먼트 보일 때");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Fragment2","onResume 사용자와 상호작용 가능");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("Fragment2","onPause 연결된 액티비티가 onPause()되어 사용자와 상호작용 중지시 호출");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Fragment2","onStop 연결 액티비티가 onStop()되어 화면에서 더이상 보이지x / 프래그먼트 기능 중지시");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v("Fragment2","onDestroyView 프래그먼트 관련 뷰 리소스 해재할 수 있도록 호출");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("Fragment2","onDestroy 프래그먼트 상태를 마지막으로 정리할 수 있도록 호출");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Fragment2","onDetach 액티비티와 연결 끊기 바로전 호출");
    }
}
