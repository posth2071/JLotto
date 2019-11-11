package com.example.maptest;


import android.content.Context;
import android.media.Image;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragOne extends Fragment implements View.OnClickListener {
    private Button bt_random, bt_change, bt_store;
    private ImageView[] iv_num = new ImageView[7];
    private ImageView iv_pack;

    private int[] numbers = new int[7]; //개별 숫자이미지뷰 id
    private int[] imgid = new int[45];  //1~45숫자 이미지 id
    private int[] packid = new int[6];  //1~6 pack 이미지 id

    private StartTask[] startTasks = new StartTask[7];
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

        iv_num[0] = (ImageView)view.findViewById(R.id.iv_num1);
        iv_num[1] = (ImageView)view.findViewById(R.id.iv_num2);
        iv_num[2] = (ImageView)view.findViewById(R.id.iv_num3);
        iv_num[3] = (ImageView)view.findViewById(R.id.iv_num4);
        iv_num[4] = (ImageView)view.findViewById(R.id.iv_num5);
        iv_num[5] = (ImageView)view.findViewById(R.id.iv_num6);
        iv_num[6] = (ImageView)view.findViewById(R.id.iv_num7);

        iv_pack = view.findViewById(R.id.iv_pack);

        bt_random = view.findViewById(R.id.bt_random);
        bt_random.setOnClickListener(this);

        bt_store = view.findViewById(R.id.bt_store);
        bt_store.setOnClickListener(this);

        bt_change = view.findViewById(R.id.bt_change);
        bt_change.setOnClickListener(this);

        dbOpenHelper = new DBOpenHelper(view.getContext().getApplicationContext());
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_random:
                random();

                btState(false);                //시작,저장 버튼 눌리지않게
                startTimer = new Timer();
                for(int i=0; i<7; i++){
                    startTasks[i] = new StartTask(iv_num[i],1);
                    startTimer.schedule(startTasks[i],0,50);
                }
                packTask = new StartTask(iv_pack, 2);
                startTimer.schedule(packTask,0,100);
                stopTask = new StopTask();
                startTimer.schedule(stopTask,1000,800);
               break;
            case R.id.bt_store:
                // 내부SQLite DB 저장
                Arrays.sort(numbers);
                int paircount = 0;
                for(int i=0; i<numbers.length; i++){
                    if((numbers[i]%2)==0)
                        paircount +=1;
                }
                int hallcount = 7-paircount;
                String[] storeSet = new String[2];
                storeSet[0] = Arrays.toString(numbers)
                        .replace("[","").replace("]","").replace(" ","");
                storeSet[1] = hallcount + ":" + paircount;
                Log.d("랜덤", storeSet[0]);
                Log.d("랜덤", storeSet[1]);

                //DB저장하기 Storeset[0] 숫자정보7개, Storeset[1] 홀짝비율정보
                if(dbOpenHelper.insertDB(lastturn+1,storeSet)==1){   //최신회차+1 (다음주회차로 설정)
                    Log.d("데이터베이스","DB저장 성공");
                } else {
                    Log.d("데이터베이스", "저장실패 - 중복");
                }

                break;
            case R.id.bt_change:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment, MainActivity.frag11).addToBackStack(null).commit();
                break;
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
        //메인액티비티 숫자(1~45)이미지 아이디, pack(1~6)이미지 아이디 갖고오기
        imgid = MainActivity.imgId;
        packid = MainActivity.packid;
        //마지막회차 얻어오기
        lastturn = MainActivity.lastLottoinfo.getTurn();
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

    //StartTask
    public class StartTask extends TimerTask {
        // type = 1(개별 공), 2(Pack)
        private int type;
        private int index;
        private ImageView imageView;
        public StartTask(ImageView imageView, int type){
            this.imageView = imageView;
            this.type = type;
            switch (type){
                case 1:
                    index = (int)(Math.random()*45)+1;
                    break;
                case 2:
                    index = 0;
                    break;
            }
        }
        @Override
        public void run() {
            switch (type){
                case 1:
                    index = (index+1) %45;
                    imageView.setImageResource(imgid[index]);
                    break;
                case 2:
                    index = (index+1) %5;
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
            if(num<7){
                startTasks[num].cancel();
                iv_num[num].setImageResource(imgid[numbers[num]-1]);
                num++;
            } else {
                //버튼 누를수 있게
                btState(true);
                stopTask.cancel();
            }
            if(num==7) {
                packTask.cancel();
                iv_pack.setImageResource(packid[0]);
            }
        }
    }

    public void random() {
        for (int i = 0; i < 7; i++) {
            numbers[i] = (int) (Math.random() * 45) + 1;
            for (int j = 0; j < i; j++) {
                if (numbers[i] == numbers[j])
                    random();
            }
        }
        Log.d("테스트", Arrays.toString(numbers));
    }

    public void btState(boolean state){
        bt_random.setClickable(state);
        bt_store.setClickable(state);
    }
}
