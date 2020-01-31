package com.example.maptest.Activity.FragMent_One;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.maptest.DataBase.DBOpenHelper;
import com.example.maptest.DataBase.DBinfo;
import com.example.maptest.Activity.MainActivity;
import com.example.maptest.R;
import com.example.maptest.AsyncTask.testset;

import java.util.ArrayList;
import java.util.Iterator;

public class FragOneTwo  extends Fragment implements View.OnClickListener  {
    private GridView gridTop, gridBottom;
    private ImageView bt_DBStore;              //DB 저장버튼

    private ArrayList<DBinfo> dBinfos;
    private DBOpenHelper dbOpenHelper;

    private ArrayList<testset> listTop = new ArrayList<>();
    private ArrayList<testset> listBottom = new ArrayList<>();

    private Resources res;
    private NumAdapter numAdapterTop, numAdapterBottom;

    private int listindex = 0;
    private int lastturn;
    //생성자 함수
    public FragOneTwo (){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag_onetwo, container, false);

        res = getResources();
        //상단,하단 그리드뷰 초기화
        ClearTop();
        ClearBottom();

        listindex=0;
        lastturn = MainActivity.lastLottoinfo.getTurn();    //최신회차 정보가져오기

        //상단그리드뷰 설정 "top"
        gridTop = view.findViewById(R.id.gridTop);
        numAdapterTop = new NumAdapter("top");
        gridTop.setAdapter(numAdapterTop);

        //하단그리드뷰 설정 "bottom"
        gridBottom = view.findViewById(R.id.gridBottom);
        numAdapterBottom = new NumAdapter("bottom");
        gridBottom.setAdapter(numAdapterBottom);

        //DB저장버튼 설정
        bt_DBStore = view.findViewById(R.id.bt_DBsotre);
        bt_DBStore.setOnClickListener(this);

        dBinfos = new ArrayList<>();
        dbOpenHelper = new DBOpenHelper(view.getContext().getApplicationContext());
        ArrayList<DBinfo> logTest = dbOpenHelper.selectAllDB();

        return view;
    }
    //클릭이벤트 처리리스너
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_DBsotre:
                if(listindex ==6){
                    // 체크한 숫자 개수가 6개인지 확인
                    dBinfos.clear();        // dBinfos리스트 비우기
                    Iterator<testset> it = listTop.iterator();
                    ArrayList<String> numSet_List = new ArrayList<>();     //임시로 번호 담을 리스트

                    while(it.hasNext()){
                        numSet_List.add(String.valueOf(it.next().getNumber()));
                    }

                    String numset = numSet_List.toString()
                            .replace("[","")
                            .replace("]","")
                            .replace(" ","");

                    // 내부SQLite DB 저장
                    //dbOpenHelper.insertDB(lastturn,numset);   //최신회차+1 (다음주회차로 설정)
                    dbOpenHelper.insertDB(lastturn+1,numset);   //최신회차+1 (다음주회차로 설정)

                    // 상단,하단 Grid뷰, listindex 초기화
                    ClearBottom();
                    ClearTop();
                    listindex=0;
                } else {
                    // listindex가 7이 아닌 경우 (번호가 모두 채워지지 않은 경우)
                    Log.d("FragOneTwo", String.format("번호 개수부족, '총 6개 - 현재 %d'", listindex));
                    //Toast.makeText(view.getContext().getApplicationContext(), "번호를 고르세요", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    // 상단 Grid뷰 초기화
    private void ClearTop(){
        listTop.clear();
        for(int i=0; i<6; i++){
            listTop.add(new testset(0, MainActivity.num_null,0));
        }
        if(numAdapterTop != null)
            //numAdapter가 생성되어있는경우
            numAdapterTop.notifyDataSetChanged();
    }

    // 하단Grid뷰 초기화
    private void ClearBottom(){
        listBottom.clear();
        for(int i=1; i<46; i++){
            listBottom.add(new testset(i,MainActivity.num_ID[i-1],0));
        }
        if(numAdapterBottom != null)
            //numAdapter가 생성되어있는 경우
            numAdapterBottom.notifyDataSetChanged();
    }


    // GridView연결, NumAdapter클래스
    public class NumAdapter extends BaseAdapter {
        LayoutInflater inflater;
        // Grid구분 (Top,Bottom)
        String gridindex;

        // 생성자 함수
        public NumAdapter(String gridindex) {
            super();
            this.gridindex = gridindex;
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if(gridindex.compareTo("top")==0){          //상단 그리드뷰인경우 ListTop리스트
                return listTop.size();
            } else if(gridindex.compareTo("bottom")==0){       //하단 그리드뷰인경우 Listbottom리스트
                return listBottom.size();
            } else
                return 1;
            //return imageViewList.size();          //아이템 보여주는 숫자랑 관계있음
        }

        //해당 위치 아이템반환
        @Override
        public Object getItem(int position) {
            if(gridindex.compareTo("top")==0){
                return listTop.get(position);
            } else if(gridindex.compareTo("bottom")==0){
                return listBottom.get(position);
            } else
                return null;
        }

        //해당위치 아이템아이디반환
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            NumViewHolder viewHolder;

            //기존에 convertView가 만들어졌는지
            if(convertView == null){
                convertView = inflater.inflate(R.layout.griditem, viewGroup, false);
                viewHolder = new NumViewHolder();
                viewHolder.numImage = convertView.findViewById(R.id.imageview);
                convertView.setTag(viewHolder);     //viewHolder객체 저장
            } else {        //기존에 존재하는 경우
                viewHolder = (NumViewHolder) convertView.getTag(); //getTag로 viewHolder 갖고오기
            }

            //하단 gridView인경우
            if(gridindex.compareTo("bottom") == 0) {
                viewHolder.numImage.setImageResource(listBottom.get(position).getImgid());
                viewHolder.numImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        testset bottom = listBottom.get(position);
                        //클릭한 번호의 Tag검사 - 아직안눌린 경우, Tag==0
                        if (bottom.getTag() == 0) {
                            if (listindex != 6) {       //고를수 있는 번호 최대 6개까지
                                bottom.setTag(1);       //Tag 1로 수정
                                bottom.setImgid(MainActivity.num_null);
                                numAdapterBottom.notifyDataSetChanged();    //Adapter에 연결된 List내용 수정된 것 반영하기

                                testset top = listTop.get(listindex);       //선택된 인덱스에 맞는 ImageView 가져오기
                                top.setTag(1);                              // 선택구분하기위해 TAG 설정
                                top.setImgid(MainActivity.num_ID[position]);              //해당 imageView 이미지 변경 - 고른 숫자로
                                top.setNumber(position + 1);
                                listindex += 1;                             //골라진 숫자갯수 증가 (최대 6개 검사하기위해)

                                Log.d("FragOneTwo", String.format("%d번 설정", (position+1)));
                                numAdapterTop.notifyDataSetChanged();       //Adapter에 연결된 List내용 수정 후 반영
                            } else {
                                //listindex가 7인경우 - 숫자 모두골라진 상태
                                //Toast.makeText(getContext().getApplicationContext(), "모두입력됨", Toast.LENGTH_SHORT).show();
                                Log.d("FragOneTwo", "입력개수 초과");
                            }
                        } else if (bottom.getTag() == 1) {
                            // 클릭한 숫자가 이미골랐던 번호인경우 (Tag==1)
                            //Toast.makeText(getContext().getApplicationContext(), "이미눌림", Toast.LENGTH_SHORT).show();
                            Log.d("FragOneTwo", String.format("이미눌린 번호 %d ", bottom.getNumber()));
                        }
                    }
                });
                //상단 Grid뷰인경우
            } else if(gridindex.compareTo("top")==0){
                viewHolder.numImage.setImageResource(listTop.get(position).getImgid());
                viewHolder.numImage.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {    //골라진 번호 제거하기
                        testset top = listTop.get(position);
                        //listindex가 0이 아닌경우 (번호가 체크된 경우) - Tag가 1인경우(번호가 있는경우)
                        if((listindex !=0) && (top.getTag() == 1)){
                                int number = (listTop.get(position)).getNumber(); //선택한 View - getNumber로 번호숫자 얻기
                                listTop.remove(position);                         // 해당index List값 제거
                                //remove로 제거되어서 index들이 하나씩 줄어듬 -> 마지막번호 생성(1로 세팅)
                                listTop.add(5, new testset(0,MainActivity.num_null,0));
                                numAdapterTop.notifyDataSetChanged();               // list값 수정 반영하기

                                testset test = listBottom.get(number-1);          //getNumber로 얻은 번호숫자에 해당하는 View얻기 (배열, -1필요)
                                test.setTag(0);                                   //해당 이미지뷰 Tag=0설정, 체크해제
                                test.setImgid(MainActivity.num_ID[number-1]);                   //해당번호에 해당하는 imgID설정
                                numAdapterBottom.notifyDataSetChanged();          //List반영하기

                                listindex -= 1;                                   // listindex 하나 줄이기
                                //Toast.makeText(getContext().getApplicationContext(),number+"번 해제",Toast.LENGTH_SHORT).show();
                                Log.d("FragOneTwo", String.format("%d번 해제", number));
                        }
                    }
                });
            }
            return convertView;
        }
    }

    // GridView에 사용되는 NumAdapter의 뷰홀더
    private class NumViewHolder {
        public ImageView numImage;
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