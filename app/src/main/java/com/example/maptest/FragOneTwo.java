package com.example.maptest;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class FragOneTwo  extends Fragment implements View.OnClickListener  {
    GridView gridTop, gridBottom;
    Button bt_DBStore;              //DB 저장버튼

    ArrayList<DBinfo> dBinfos;
    DBOpenHelper dbOpenHelper;

    int[] imgId = new int[45];

    ArrayList<testset> listTop = new ArrayList<>();
    ArrayList<testset> listBottom = new ArrayList<>();

    Resources res;

    NumAdapter numAdapterTop, numAdapterBottom;

    int listindex = 0;

    public FragOneTwo (){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag_onetwo, container, false);
        res = getResources();
        getIdset();

        ClearTop();
        ClearBottom();
        listindex=0;

        gridTop = view.findViewById(R.id.gridTop);
        numAdapterTop = new NumAdapter("top");
        gridTop.setAdapter(numAdapterTop);

        gridBottom = view.findViewById(R.id.gridBottom);
        numAdapterBottom = new NumAdapter("bottom");
        gridBottom.setAdapter(numAdapterBottom);

        dBinfos = new ArrayList<>();
        dbOpenHelper = new DBOpenHelper(view.getContext().getApplicationContext());
        dBinfos = dbOpenHelper.selectDB(888);
        Log.d("데이터베이스", dBinfos.toString());

        bt_DBStore = view.findViewById(R.id.bt_DBsotre);
        bt_DBStore.setOnClickListener(this);
        return view;
    }
    //클릭이벤트 처리리스너
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_DBsotre:
                if(listindex ==7){
                    Toast.makeText(view.getContext().getApplicationContext(), "저장", Toast.LENGTH_SHORT).show();
                    dBinfos.clear();        // dBinfos리스트 비우기
                    Iterator<testset> it = listTop.iterator();
                    ArrayList<String> testlist = new ArrayList<>();     //임시로 번호 담을 리스트
                    while(it.hasNext()){
                        String str = String.valueOf(it.next().getNumber());
                        testlist.add(str);
                        Log.d("테스트",str);
                    }

                    // numsetSort 오름차순으로 번호 정렬-홀짝비율 계산
                    String[] strset = numsetSort(
                            testlist.toString()
                                    .replace("[","")
                                    .replace("]","")
                                    .replace(" ",""));          //앞뒤([]), 숫자사이 공백 제거
                    // 내부SQLite DB 저장

                    if(dbOpenHelper.insertDB(MainActivity.lastturn+1,strset)==1){   //최신회차+1 (다음주회차로 설정)
                        Log.d("테스트","DB저장 성공");
                    } else {
                        Log.d("테스트", "저장실패 - 중복");
                    }

                    dBinfos = dbOpenHelper.selectDB(MainActivity.lastturn);
                    Iterator<DBinfo> it1 = dBinfos.iterator();
                    while(it1.hasNext()){
                        Log.d("테스트",it1.next().getInfo());  //db저장 목록 보여주기
                    }
                    // 상단,하단 Grid뷰, listindex 초기화
                    ClearBottom();
                    ClearTop();
                    listindex=0;
                } else {        // listindex가 7이 아닌 경우
                    Toast.makeText(view.getContext().getApplicationContext(), "번호를 고르세요", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    //숫자집합 오름차순으로 정렬, 홀짝비율 계산
    private String[] numsetSort(String numset){
        String[] numberset =  numset.split(",");

        int[] changeset = new int[numberset.length];           //numberset배열길이만큼 할당
        int paircount = 0;                                     //짝수개수 보관변수
        for(int i=0; i<numberset.length; i++){
            changeset[i] = Integer.parseInt(numberset[i]);
            if(changeset[i]%2==0)                               //짝수인지 확인 나머지가 0인경우
                paircount += 1;                                      //짝수 개수파악
        }
        int hallcount = numberset.length - paircount;                       //전체숫자개수중 짝수개수 제외 나머지는 홀수

        Arrays.sort(changeset);                                //오름차순으로 정렬
        String[] str = new String[2];
        str[0] = Arrays.toString(changeset).
                replace("[","").replace("]","").replace(" ","");
        str[1] = String.valueOf(hallcount)+":"+String.valueOf(paircount);
        Log.d("나눗셈",str[1]);
        return str;         //String배열반환(0번째 정렬된 숫자정보 / 1번째 홀짝개수)
    }

    //이미지 int형 id반환함수
    private void getIdset() {
        for (int i = 1; i < 46; i++) {                //45번반복
            int stringId = res.getIdentifier("num" + i, "string", super.getActivity().getPackageName());     // name에 해당하는 값의 위치 가져옴, 2131492892반환
            String stringId2 = res.getString(stringId);
            imgId[i - 1] = res.getIdentifier(stringId2, "drawable", super.getActivity().getPackageName());
        }
    }

    // 상단 Grid뷰 초기화
    private void ClearTop(){
        listTop.clear();
        for(int i=0; i<7; i++){
            listTop.add(new testset(1, imgId[0],0));
        }
        if(numAdapterTop != null)           //numAdapter가 생성되어있는경우
            numAdapterTop.notifyDataSetChanged();
    }

    // 하단Grid뷰 초기화
    private void ClearBottom(){
        listBottom.clear();
        for(int i=1; i<46; i++){
            listBottom.add(new testset(i,imgId[i-1],0));
        }
        if(numAdapterBottom != null)        //numAdapter가 생성되어있는 경우
            numAdapterBottom.notifyDataSetChanged();
    }


    // GridView연결, NumAdapter클래스
    public class NumAdapter extends BaseAdapter {
        LayoutInflater inflater;
        String gridindex;                   // Grid구분 (Top,Bottom)
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
                            if (listindex != 7) {       //고를수 있는 번호 최대 7개까지
                                bottom.setTag(1);       //Tag 1로 수정
                                bottom.setImgid(res.getIdentifier("plus", "drawable",getActivity().getPackageName())); // plus이미지로 변경
                                numAdapterBottom.notifyDataSetChanged();    //Adapter에 연결된 List내용 수정된 것 반영하기

                                testset top = listTop.get(listindex);       //선택된 인덱스에 맞는 ImageView 가져오기
                                top.setImgid(imgId[position]);              //해당 imageView 이미지 변경 - 고른 숫자로
                                top.setNumber(position + 1);
                                listindex += 1;                             //골라진 숫자갯수 증가 (최대 7개 검사하기위해)
                                //}
                                numAdapterTop.notifyDataSetChanged();       //Adapter에 연결된 List내용 수정 후 반영
                            } else { //listindex가 7인경우 - 숫자 모두골라진 상태
                                Toast.makeText(getContext().getApplicationContext(), "모두입력됨", Toast.LENGTH_SHORT).show();
                            }
                            // 클릭한 숫자가 이미골랐던 번호인경우 (Tag==1)
                        } else if (bottom.getTag() == 1) {
                            Toast.makeText(getContext().getApplicationContext(), "이미눌림", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //상단 Grid뷰인경우
            } else if(gridindex.compareTo("top")==0){
                viewHolder.numImage.setImageResource(listTop.get(position).getImgid());
                viewHolder.numImage.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {    //골라진 번호 제거하기
                        //listindex가 0이 아닌경우 (번호가 체크된 경우)
                        if(listindex !=0){
                            int number = (listTop.get(position)).getNumber(); //선택한 View - getNumber로 번호숫자 얻기
                            listTop.remove(position);                         // 해당index List값 제거
                            //remove로 제거되어서 index들이 하나씩 줄어듬 -> 마지막번호 생성(1로 세팅)
                            listTop.add(6, new testset(1,imgId[0],0));
                            numAdapterTop.notifyDataSetChanged();               // list값 수정 반영하기

                            testset test = listBottom.get(number-1);          //getNumber로 얻은 번호숫자에 해당하는 View얻기 (배열, -1필요)
                            test.setTag(0);                                   //해당 이미지뷰 Tag=0설정, 체크해제
                            test.setImgid(imgId[number-1]);                   //해당번호에 해당하는 imgID설정
                            numAdapterBottom.notifyDataSetChanged();          //List반영하기

                            listindex -= 1;                                   // listindex 하나 줄이기
                            Toast.makeText(getContext().getApplicationContext(),number+"번 해제",Toast.LENGTH_SHORT).show();
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