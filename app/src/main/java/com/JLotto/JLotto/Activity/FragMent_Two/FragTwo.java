package com.JLotto.JLotto.Activity.FragMent_Two;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.JLotto.JLotto.DataBase.DBOpenHelper;
import com.JLotto.JLotto.Activity.Dialog.DialogClass;
import com.JLotto.JLotto.Adapter.Expandable.Expand_Child;
import com.JLotto.JLotto.Adapter.Expandable.ExpandableAdapter;
import com.JLotto.JLotto.AsyncTask.LottoParsingInfo;
import com.JLotto.JLotto.Activity.MainActivity;
import com.JLotto.JLotto.Activity.Dialog.MyDialogListener;
import com.JLotto.JLotto.Activity.FragMent_Two.QRCord.QRCodeActivity;
import com.JLotto.JLotto.R;
import com.JLotto.JLotto.AsyncTask.TestClass;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragTwo extends Fragment {
    public static final String TURN_TEXT = "%d회 당첨결과";
    private ImageView[] frag2_numiv = new ImageView[7];
    private ImageView frag2_group_iv, frag2_share, frag2_QR;
    private TextView frag2_turn, frag2_date;
    private LottoParsingInfo parsingInfo;
    private View view;
    private Context context;

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
        view = inflater.inflate(R.layout.fragment_frag_two, container, false);

        frag2_share = view.findViewById(R.id.frag2_sharebt);
        frag2_share.setColorFilter(Color.parseColor("#006C93"));
        frag2_share.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        frag2_QR = view.findViewById(R.id.frag2_QRbt);
        frag2_QR.setColorFilter(Color.parseColor("#006C93"));
        frag2_QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                read_QRcode();
            }
        });
        frag2_group_iv = view.findViewById(R.id.frag2_expand_group_iv);

        frag2_turn = view.findViewById(R.id.frag2_turn);
        frag2_date = view.findViewById(R.id.frag2_date);

        frag2_expandable = view.findViewById(R.id.frag2_Expandable);
        frag2_expandable.setGroupIndicator(null);
        Resources res = getResources();
        for(int i=0; i<7; i++){
            int viewId = res.getIdentifier("frag2_num"+(i+1),"id",context.getPackageName());
            frag2_numiv[i] = view.findViewById(viewId);         //이미지뷰 7개 연결
        }
        return view;
    }

    // onActivityCreated - 연결된 액티비티 onCreate() 완료 후 호출
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.v("Fragment2","onActivityCreated 연결된 액티비티 onCreate() 완료 후 호출");

        //1~45숫자 이미지ID 갖고오기
        parsingInfo = MainActivity.lastLottoinfo;   //제일마지막회차 정보 클래스 얻어오기
        Log.d("확인", String.format(TURN_TEXT, parsingInfo.getTurn()));

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
        dbOpenHelper = new DBOpenHelper(context);
        mChildList.setTypeTwo(dbOpenHelper.selectDB(MainActivity.searchLottoInfo.turn));

        //Child타입3 세팅
        ArrayList<String> typeThree = new ArrayList<>();
        typeThree.add("지급개시일로부터 1년 (휴일인 경우 익영업일)");
        mChildList.setTypethree(typeThree);
        Log.d("테스트", "mChildList.typethree size = "+mChildList.typethree.size() +"\n"+mChildList.typethree.get(0));

        //Adapter 생성 -> GroupList, ChildList
        frag2_exAdapter = new ExpandableAdapter(context, mGroupList, mChildList);
        frag2_expandable.setAdapter(frag2_exAdapter);       //확장리스트뷰에 Adapter 연결
        //테스트용 DB전체 호출 - 로그확인용
        dbOpenHelper.selectAllDB();

    }

    public void dialogshow(int type){
        dialog = new DialogClass(getActivity(), type);        // 대화상자 클래스 객체 생성 (getActivity로 프래그먼트가 올라와있는 액티비티 가져오기)
        dialog.setDialogListener(new MyDialogListener() {           // 리스너 인터페이스 등록
            @Override
            public void onPositiveClicked(String num) {             // 재정의
                // 입력한 회차정보가 공백이 아니거나 추첨된 회차인 경우
                if(num.compareTo("") != 0) {
                    dialog.dismiss();

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
            Log.d("테스트0615", numset[i]);
            int num = Integer.parseInt(numset[i]);
            Log.d("확인", "Lottoinfo index ["+i+"] - "+numset[i] + ", num - "+num);
            frag2_numiv[i].setImageResource(MainActivity.num_ID[num-1]);
        }
    }

    private void read_QRcode(){
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setCaptureActivity(QRCodeActivity.class);
        integrator.setBeepEnabled(false); // 바코드 인식시 소리 '삐'
        integrator.setOrientationLocked(false); //가로모드 설정
        integrator.initiateScan();
    }


    // 화면캡쳐 함수
    private void captureImage(){
        Log.d("캡쳐", "captureImage 실행");
        // 권한 체크
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("에러","권한체크 if문들어옴");
            // 사용자 권한 요청
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/J'Lotto";  // 저장폴더 경로
        View layout = getActivity().getWindow().getDecorView().getRootView();                             // 캡쳐할영역(프레임레이아웃)

        File folder = new File(path);
        if(!folder.exists()){       // 저장소 내에 Dust폴더가 있는지
            folder.mkdirs();        // 없으면 생성
            Toast.makeText(context, "폴더가 생성되었습니다.", Toast.LENGTH_SHORT).show();
        }

        // 캡쳐파일 이름 ( Dust-연도-월일-시분초.jpeg )
        String filename = "/J'Lotto-" + new SimpleDateFormat("yyyy-MMdd-HHmmss").format(new Date()) +".jpeg";
        File file = new File(path + filename);

        layout.buildDrawingCache();
        Bitmap captureview = layout.getDrawingCache();

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(file);
            captureview.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
            Toast.makeText(context, "갤러리 저장", Toast.LENGTH_SHORT).show();

            fos.flush();
            fos.close();
            layout.destroyDrawingCache();

            // 이미지 SNS전송
            Uri imageUri = FileProvider.getUriForFile(context, "com.JLotto.JLotto.fileprovider", file);
            sendSNS(imageUri);

        } catch (FileNotFoundException e) {
            Log.d("에러","Frag2-Capture \n\tFileNotFoundException Error \n\t"+e.toString());
        } catch (IOException e) {
            Log.d("에러","Frag2-Capture \n\tIOException Error \n\t"+e.toString());
        }
    }

    // 화면 SNS공유
    public void sendSNS(Uri imageUri){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.putExtra(Intent.EXTRA_TEXT, "");     // TEXT 없애면 인스타그램은 전송안됨,
        getActivity().startActivity(Intent.createChooser(intent, "send"));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
        Log.v("Fragment2","onAttach 프래그먼트가 액티비티와 연결시 호출");
    }
}
