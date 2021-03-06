package com.JLotto.JLotto.Activity.FragMent_One;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.JLotto.JLotto.DataBase.DBOpenHelper;
import com.JLotto.JLotto.Activity.MainActivity;
import com.JLotto.JLotto.R;
import com.JLotto.JLotto.Util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
    private int[] rotation_num_img = new int[45];  //1~45숫자 이미지 id
    private int[] num_img= new int[45];  //1~45숫자 이미지 id
    private int[] packid = new int[6];  //1~6 pack 이미지 id
    private List<Integer> numberlist = new ArrayList<>();

    private StartTask[] startTasks = new StartTask[6];
    private StartTask packTask;
    private StopTask stopTask;
    private Timer startTimer;

    private DBOpenHelper dbOpenHelper;
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

        dbOpenHelper = new DBOpenHelper(view.getContext());
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_random:
                Logger.d("FragOne", "추첨버튼 클릭");
                btState(false);                //시작,저장 버튼 눌리지않게
                random();                      //랜덤 번호뽑기
                startTimer = new Timer();
                for(int i=0; i<6; i++){
                    startTasks[i] = new StartTask(frag1_Numiv[i],1);
                    startTimer.schedule(startTasks[i],0,50);
                }
                packTask = new StartTask(iv_pack, 2);
                startTimer.schedule(packTask,0,60);
                stopTask = new StopTask();
                startTimer.schedule(stopTask,500,500);
               break;
            // 저장 누른경우 (내부SQLite DB)
            case R.id.bt_store:
                Logger.d("FragOne", "저장버튼 클릭");
                if(numberlist.size()==6){          //numberlist가 채워져있는지
                    String numset = numberlist.toString()
                            .replace("[","")
                            .replace("]","")
                            .replaceAll(" ","");
                    //DB저장하기 Storeset[0] 숫자정보7개, Storeset[1] 홀짝비율정보
                    dbOpenHelper.insertDB(MainActivity.lastLottoinfo.getTurn()+1,numset);

                    numberlist.clear();     //리스트 초기화
                    imgReset();             //이미지뷰 초기화
                } else {        // list가 비어있는경우
                    Logger.d("FragOne", "저장 실패, 추첨 번호 미존재");
                }
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("FragOne","onActivityCreated 연결된 액티비티 onCreate() 완료 후 호출");
        //메인액티비티 숫자(1~45)이미지 아이디, pack(1~6)이미지 아이디 갖고오기
        rotation_num_img = MainActivity.rotation_num_ID;
        num_img = MainActivity.num_ID;
        packid = MainActivity.pack_ID;
    }

    //이미지뷰7개 초기화 함수
    private void imgReset(){
        for(int i=0; i<6; i++){
            frag1_Numiv[i].setImageResource(MainActivity.num_null);
        }
    }

    //랜덤번호 추출 함수
    public void random() {
        Logger.d("FragOne", "random() 함수실행");
        numberlist.clear();             //저장할 리스트비우기
        HashSet<Integer> exceptCheck = new HashSet<>();
        HashSet<Integer> lottonums = new HashSet<>();
        //고정수 설정된게 있다면 해쉬에 추가
        if(MainActivity.fixedNums.size()!=0){
            lottonums.addAll(MainActivity.fixedNums);
            Logger.d("FragOne", String.format("고정수 존재 '%s'",MainActivity.fixedNums.toString()));
        }
        //제외수 설정된게 있다면 해쉬에 추가
        if(MainActivity.exceptNums.size()!=0){
            exceptCheck.addAll(MainActivity.exceptNums);
            Logger.d("FragOne", String.format("제외수 존재 '%s'",MainActivity.exceptNums.toString()));
        }
        // 추첨번호 개수가 6개될때까지 반복해서 랜덤숫자 뽑기
        while(lottonums.size()<6) {
            int random = (int)(Math.random()*45)+1;
            if(exceptCheck.add(random)){ //랜덤값이 추가된다면 중복없음
                Logger.d("FragOne", String.format("제외수 통과, 번호 %d", random));
                // Hash 사용으로 중복일 경우 추가x
                lottonums.add(random);
                }
        }
        numberlist.addAll(lottonums);   //numberlist에 옮기기
        Logger.d("FragOne", String.format("추첨 번호 '%s',", numberlist.toString()));
    }

    //버튼(번호뽑기, 저장) 상태 조작 함수
    public void btState(boolean state){
        bt_random.setClickable(state);
        bt_store.setClickable(state);
        Logger.d("FragOne", String.format("번호 추첨 중, 버튼세팅 '%b'",state));
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
                    imageView.setImageResource(rotation_num_img[index]);
                    break;
                case 2:     //type2 pack이면
                    index = (index+1) %8;   // 1씩 늘려가며 이미지전환
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
                frag1_Numiv[num].setImageResource(num_img[numberlist.get(num)-1]);
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
}
