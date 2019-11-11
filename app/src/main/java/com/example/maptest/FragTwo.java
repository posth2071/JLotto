package com.example.maptest;


import android.app.Dialog;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragTwo extends Fragment implements View.OnClickListener {
    TextView frag2_tv1;              //private 설정시 에러발생
    Button frag2_btsearch, frag2_dbsetting;

    ExpandableListView frag2_expandable;
    ExpandableAdapter frag2_exAdapter;

    ArrayList<String> mGroupList = null;
    Expand_Child mChildList = null;

    DBOpenHelper dbOpenHelper;

    public static DialogClass dialog;
    //생성자함수
    public FragTwo() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v("Fragment3","onCreateView 프래그먼트와 관련되는 뷰계층 만들어서 리턴");
        View view = inflater.inflate(R.layout.fragment_frag_two, container, false);

        frag2_tv1 = view.findViewById(R.id.frag2_tv1);


        frag2_btsearch = view.findViewById(R.id.frag2_btsearch);
        frag2_btsearch.setOnClickListener(this);

        frag2_dbsetting = view.findViewById(R.id.frag2_dbsetting);
        frag2_dbsetting.setOnClickListener(this);

        frag2_expandable = view.findViewById(R.id.frag2_Expandable);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.frag2_btsearch:           // 검색버튼 누른경우
                dialogshow(1);                   // 검색 대화상자 띄우기
                break;
            case R.id.frag2_dbsetting:
                dialogshow(2);
                break;
        }
    }
    public void dialogshow(int type){
        dialog = new DialogClass(getActivity(), type);        // 대화상자 클래스 객체 생성 (getActivity로 프래그먼트가 올라와있는 액티비티 가져오기)
        dialog.setDialogListener(new MyDialogListener() {           // 리스너 인터페이스 등록
            @Override
            public void onPositiveClicked(String num) {             // 재정의
                if(num.compareTo("") != 0) {
                    TestClass testclass = new TestClass();
                    MainActivity.searchLottoInfo = testclass.parsing(num);
                    mChildList.setTypeTwo(dbOpenHelper.selectDB(MainActivity.searchLottoInfo.turn));
                    frag2_exAdapter.notifyDataSetChanged();
                } else {
                }
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

        mGroupList = new ArrayList<>();
        mGroupList.add("당첨정보");
        mGroupList.add("기록");
        mGroupList.add("당첨금 지급기한");
        Log.d("테스트", "mGroupList.size = "+mGroupList.size());

        mChildList = new Expand_Child();
        mChildList.setTypeOne(MainActivity.lastLottoinfo.getSubInfo());
        Log.d("테스트", "mChildList.typeOne size = "+mChildList.typeOne.size() +"\n"+mChildList.typeOne.get(0));

        dbOpenHelper = new DBOpenHelper(getView().getContext());
        Log.d("테스트", "turn" +MainActivity.searchLottoInfo.turn);
        ArrayList<DBinfo> test = dbOpenHelper.selectDB(MainActivity.searchLottoInfo.turn);
        Log.d("테스트", "test 사이즈 " +test.size());         //10반환
        mChildList.setTypeTwo(dbOpenHelper.selectDB(MainActivity.searchLottoInfo.turn));
        //10반환, 888/ 13,14,15,19,20,21,31 /5:2 / DB
        dbOpenHelper.selectAllDB();


        ArrayList<String> typeThree = new ArrayList<>();
        typeThree.add("당첨금 지급기한");
        mChildList.setTypethree(typeThree);
        Log.d("테스트", "mChildList.typethree size = "+mChildList.typethree.size() +"\n"+mChildList.typethree.get(0));

        frag2_exAdapter = new ExpandableAdapter(getContext(), mGroupList, mChildList);
        frag2_expandable.setAdapter(frag2_exAdapter);
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
