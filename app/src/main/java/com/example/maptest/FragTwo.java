package com.example.maptest;


import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
public class FragTwo extends Fragment {
    public static final String TURN_TEXT = "%d회 당첨결과";
    private ImageView[] frag2_numiv = new ImageView[7];
    private TextView frag2_turn, frag2_date;
    private LottoParsingInfo parsingInfo;

    ExpandableListView frag2_expandable;
    ExpandableAdapter frag2_exAdapter;

    ArrayList<String> mGroupList = null;
    Expand_Child mChildList = null;

    DBOpenHelper dbOpenHelper;

    public static DialogClass dialog;

    public FragTwo() { }    //생성자함수

    // onCreateView - 프래그먼트와 관련되는 뷰계층 만들어서 리턴
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v("Fragment3","onCreateView 프래그먼트와 관련되는 뷰계층 만들어서 리턴");
        View view = inflater.inflate(R.layout.fragment_frag_two, container, false);

        frag2_turn = view.findViewById(R.id.frag2_turn);
        frag2_date = view.findViewById(R.id.frag2_date);
        frag2_expandable = view.findViewById(R.id.frag2_Expandable);

        Resources res = getResources();
        for(int i=0; i<7; i++){
            int viewId = res.getIdentifier("frag2_num"+(i+1),"id",getContext().getPackageName());
            frag2_numiv[i] = view.findViewById(viewId);         //이미지뷰 7개 연결
        }

        return view;
    }

    // onActivityCreated - 연결된 액티비티 onCreate() 완료 후 호출
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("Fragment2","onActivityCreated 연결된 액티비티 onCreate() 완료 후 호출");
        //String str = Arrays.toString(MainActivity.lastLottoinfo.getLottoInfo())
          //      .replace("[","")
            //    .replace("]","");

        //1~45숫자 이미지ID 갖고오기
        parsingInfo = MainActivity.lastLottoinfo;   //제일마지막회차 정보 클래스 얻어오기
        Log.d("확인", String.format(TURN_TEXT, parsingInfo.getTurn()));


        /*
        //초기 제일최신회차 번호 이미지뷰 세팅
        String[] str = parsingInfo.getLottoInfo();
        for(int i=0; i<str.length; i++){
            int num = Integer.parseInt(str[i]);
            Log.d("확인", "Lottoinfo index ["+i+"] - "+str[i] + ", num - "+num);
            frag2_numiv[i].setImageResource(MainActivity.imgId[num-1]);
        }
        frag2_turn.setText(String.format(TURN_TEXT, String.valueOf(parsingInfo.getTurn())));  //회차 세팅
        frag2_date.setText(parsingInfo.getDate());  //추첨날짜 세팅
         */
        setting();

        //그룹뷰 리스트 세팅
        mGroupList = new ArrayList<>();
        mGroupList.add("당첨정보");
        mGroupList.add("기록");
        mGroupList.add("당첨금 지급기한");
        Log.d("테스트", "mGroupList.size = "+mGroupList.size());

        //자식뷰 담을 Expand_Child생성
        mChildList = new Expand_Child();

        //Child타입1 세팅
        mChildList.setTypeOne(MainActivity.lastLottoinfo.getSubInfo());
        Log.d("테스트", "mChildList.typeOne size = "+mChildList.typeOne.size() +"\n"+mChildList.typeOne.get(0));

        //Child타입2 세팅 (현재 검색된 회차에 해당하는 DB기록 불러오기)
        dbOpenHelper = new DBOpenHelper(getView().getContext());
        mChildList.setTypeTwo(dbOpenHelper.selectDB(MainActivity.searchLottoInfo.turn));

        //Child타입3 세팅
        ArrayList<String> typeThree = new ArrayList<>();
        typeThree.add("당첨금 지급기한");
        mChildList.setTypethree(typeThree);
        Log.d("테스트", "mChildList.typethree size = "+mChildList.typethree.size() +"\n"+mChildList.typethree.get(0));

        //Adapter 생성 -> GroupList, ChildList
        frag2_exAdapter = new ExpandableAdapter(getContext(), mGroupList, mChildList);
        frag2_expandable.setAdapter(frag2_exAdapter);       //확장리스트뷰에 Adapter 연결

        //테스트용 DB전체 호출 - 로그확인용
        dbOpenHelper.selectAllDB();
    }

    public void dialogshow(int type){
        dialog = new DialogClass(getActivity(), type);        // 대화상자 클래스 객체 생성 (getActivity로 프래그먼트가 올라와있는 액티비티 가져오기)
        dialog.setDialogListener(new MyDialogListener() {           // 리스너 인터페이스 등록
            @Override
            public void onPositiveClicked(String num) {             // 재정의
                if(num.compareTo("") != 0) {
                    TestClass testclass = new TestClass();
                    MainActivity.searchLottoInfo = testclass.parsing(num);
                    parsingInfo = MainActivity.searchLottoInfo;
                    setting();
                    mChildList.setTypeOne(parsingInfo.getSubInfo());
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

    public void setting(){
        String[] numset = parsingInfo.getLottoInfo();

        frag2_turn.setText(String.format(
                TURN_TEXT,              //미리 정해놓은 상수
                parsingInfo.getTurn()   //회차정보 int형
        ));                   //회차정보 세팅
        frag2_date.setText(parsingInfo.getDate());  //추첨날짜 세팅
        // 이미지뷰 7개 세팅
        for(int i=0; i<7; i++){
            int num = Integer.parseInt(numset[i]);
            Log.d("확인", "Lottoinfo index ["+i+"] - "+numset[i] + ", num - "+num);
            frag2_numiv[i].setImageResource(MainActivity.imgId[num-1]);
        }
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
