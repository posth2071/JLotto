package com.example.maptest;


import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragOne extends Fragment implements View.OnClickListener {
    private Button bt_random, bt_store;
    private ImageView[] frag1_Numiv = new ImageView[6];
    private ImageView iv_pack;

    private int[] numbers = new int[6]; //개별 숫자이미지뷰 id
    private int[] imgid = new int[45];  //1~45숫자 이미지 id
    private int[] packid = new int[6];  //1~6 pack 이미지 id
    private List<Integer> numberlist = new ArrayList<>();

    private StartTask[] startTasks = new StartTask[6];
    private StartTask packTask;
    private StopTask stopTask;
    private Timer startTimer;

    private DBOpenHelper dbOpenHelper;
    private int lastturn;
    //생성자
    public FragOne() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_one, container, false);

        Resources res = getResources();
        for(int i=0; i<6; i++){
            int viewId = res.getIdentifier("frag1_num"+(i+1),"id",getActivity().getPackageName());
            frag1_Numiv[i] = view.findViewById(viewId);
        }

        iv_pack = view.findViewById(R.id.iv_pack);

        bt_store = view.findViewById(R.id.bt_store);
        bt_store.setOnClickListener(this);

        bt_random = view.findViewById(R.id.bt_random);
        bt_random.setOnClickListener(this);

        dbOpenHelper = new DBOpenHelper(view.getContext().getApplicationContext());
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_random:
                Log.d("fragone", "bt_random 눌림");
                random();                      //랜덤 번호뽑기
                btState(false);                //시작,저장 버튼 눌리지않게
                startTimer = new Timer();
                for(int i=0; i<6; i++){
                    startTasks[i] = new StartTask(frag1_Numiv[i],1);
                    startTimer.schedule(startTasks[i],0,50);
                }
                packTask = new StartTask(iv_pack, 2);
                startTimer.schedule(packTask,0,100);
                stopTask = new StopTask();
                startTimer.schedule(stopTask,1000,800);
               break;
            // 저장 누른경우 (내부SQLite DB)
            case R.id.bt_store:
                if(numberlist.size()==6){          //numberlist가 채워져있는지
                    //Arrays.sort(numbers);
                    int paircount = 0;             //짝수 조사
                    for(int i=0; i<numberlist.size(); i++){
                        if((numberlist.get(i)%2)==0)
                            paircount +=1;
                    }
                    int hallcount = 6-paircount;   //홀수 계산
/*
                    String[] storeSet = new String[2];
                    storeSet[0] = numberlist.toString()
                            .replace("[","").replace("]","").replace(" ","");

                    storeSet[1] = hallcount + ":" + paircount;
                    Log.d("랜덤", storeSet[0]);
                    Log.d("랜덤", storeSet[1]);
 */
                    String numset = numberlist.toString()
                            .replace("[","")
                            .replace("]","")
                            .replace(" ","");;
                    //DB저장하기 Storeset[0] 숫자정보7개, Storeset[1] 홀짝비율정보
                    if(dbOpenHelper.insertDB(884,numset)==1){   //최신회차+1 (다음주회차로 설정)
                        Log.d("데이터베이스","DB저장 성공");
                    } else {
                        Log.d("데이터베이스", "저장실패 - 중복");
                    }
                    numberlist.clear();     //리스트 초기화
                    imgReset();             //이미지뷰 초기화
                } else {        // list가 비어있는경우
                    Toast.makeText(getContext(), "번호 없음",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("frag1","onActivityCreated 연결된 액티비티 onCreate() 완료 후 호출");
        //메인액티비티 숫자(1~45)이미지 아이디, pack(1~6)이미지 아이디 갖고오기
        imgid = MainActivity.imgId;
        packid = MainActivity.packid;
        //마지막회차 얻어오기
        //lastturn = MainActivity.lastLottoinfo.getTurn();
    }

    //이미지뷰7개 초기화 함수
    private void imgReset(){
        for(int i=0; i<6; i++){
            frag1_Numiv[i].setImageResource(imgid[0]);
        }
    }

    //랜덤번호 추출 함수
    public void random() {
        Log.d("frag1", "\n" + "\trandom() 실행");
        numberlist.clear();             //저장할 리스트비우기
        HashSet<Integer> exceptCheck = new HashSet<>();
        HashSet<Integer> lottonums = new HashSet<>();
        //고정수 설정된게 있다면 해쉬에 추가
        if(MainActivity.fixedNums.size()!=0){
            lottonums.addAll(MainActivity.fixedNums);
            Log.d("frag1", "\n" + "\t고정수 fixedNums - \n"+MainActivity.fixedNums.toString());
        }
        //제외수 설정된게 있다면 해쉬에 추가
        if(MainActivity.exceptNums.size()!=0){
            exceptCheck.addAll(MainActivity.exceptNums);
            Log.d("frag1", "\n" + "\t제외수 exceptCheck - \n"+exceptCheck.toString());
        }
        while(lottonums.size()<6) {
            int random = (int)(Math.random()*45)+1;
            if(exceptCheck.add(random)){ //랜덤값이 추가된다면 중복없음
                Log.d("frag1", "\n" + "\t랜덤값 제외수 통과, random - "+random);
                lottonums.add(random);
                }
        }
        numberlist.addAll(lottonums);   //numberlist에 옮기기
        Log.d("frag1", "\n" + "\t랜덤 추출번호 최종 - "+numberlist.toString());
    }

    //버튼(번호뽑기, 저장) 상태 조작 함수
    public void btState(boolean state){
        bt_random.setClickable(state);
        bt_store.setClickable(state);
        Log.d("frag1", "\n" + "\t번호추출, 저장 버튼 세팅 - "+state);
    }


    //StartTask클래스
    public class StartTask extends TimerTask {
        //type = 1(개별 공), 2(Pack)
        private int type;
        private int index;
        private ImageView imageView;
        public StartTask(ImageView imageView, int type){
            this.imageView = imageView;
            this.type = type;
            switch (type){
                case 1:     //type 1이면 개별 공
                    index = (int)(Math.random()*45)+1;  //임의의 숫자로 돌리기시작
                    break;
                case 2:     //type 2이면 pack이미지뷰
                    index = 0;
                    break;
            }
        }
        @Override
        public void run() {
            switch (type){
                case 1:     //type1 개별공이면
                    index = (index+1) %45;  // 1씩 늘려가며 이미지전환
                    imageView.setImageResource(imgid[index]);
                    break;
                case 2:     //type2 pack이면
                    index = (index+1) %5;   // 1씩 늘려가며 이미지전환
                    iv_pack.setImageResource(packid[index]);
                    break;
            }
        }
    }

    //StopTask 클래스
    public class StopTask extends TimerTask{
        int num = 0;
        @Override
        public void run() {
            if(num<6){
                startTasks[num].cancel();       //첫번째 공부터 하나씩 멈추기
                frag1_Numiv[num].setImageResource(imgid[numberlist.get(num)-1]);
                num++;
            } else {    //모두 멈췄으면 버튼 세팅
                //버튼 누를수 있게
                btState(true);
                stopTask.cancel();
            }
            if(num==6) {
                packTask.cancel();
                iv_pack.setImageResource(packid[0]);
            }
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
