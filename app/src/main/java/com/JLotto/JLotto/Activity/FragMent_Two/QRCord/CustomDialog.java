package com.JLotto.JLotto.Activity.FragMent_Two.QRCord;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.JLotto.JLotto.Activity.MainActivity;
import com.JLotto.JLotto.DataBase.DBOpenHelper;
import com.JLotto.JLotto.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class CustomDialog implements View.OnClickListener {

    public static TextView QR_Dialog_Turn, QR_Dialog_Date, QR_Dialog_ResultNum, QR_Dialog_ResultInfo;
    private ListView QR_Dialog_MyNumList;
    private TextView QR_Dialog_Ok, QR_Dialog_Cancel;
    private ImageView[] QR_Dialog_numset = new ImageView[7];
    private LinearLayout QR_Dialog_numLayout;

    private Context context;
    private Dialog dlg;

    private ListViewAdapter listViewAdapter;
    private ArrayList<Boolean> check_state = new ArrayList<>();

    private int turn;

    public CustomDialog(Context context, String url){
        this.context = context;
        dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.setContentView(R.layout.dialog_qrcord);

        QR_Dialog_numLayout = dlg.findViewById(R.id.QR_Dialog_numLayout);

        QR_Dialog_Turn = dlg.findViewById(R.id.QR_Dialog_Turn);
        QR_Dialog_Date = dlg.findViewById(R.id.QR_Dialog_Date);
        // QR_Dialog_ResultNum = dlg.findViewById(R.id.QR_Dialog_ResultNum);
        QR_Dialog_ResultInfo = dlg.findViewById(R.id.QR_Dialog_ResultInfo);

        QR_Dialog_MyNumList = dlg.findViewById(R.id.QR_Dialog_MyNumList);

        QR_Dialog_Ok = dlg.findViewById(R.id.QR_Dialog_ok);
        QR_Dialog_Cancel = dlg.findViewById(R.id.QR_Dialog_cancel);

        Resources res = context.getResources();
        for(int i=0; i < QR_Dialog_numset.length; i++){
            int id = res.getIdentifier("QR_Dialog_num"+(i+1), "id", context.getPackageName());
            QR_Dialog_numset[i] = dlg.findViewById(id);
        }

        Parser parser = new Parser();
        parser.execute(url);
    }

    public void callDialog(final ArrayList<ListItem> items){
        dlg.show();

        listViewAdapter = new ListViewAdapter(items);
        QR_Dialog_MyNumList.setAdapter(listViewAdapter);

        QR_Dialog_Ok.setOnClickListener(this);
        QR_Dialog_Cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.QR_Dialog_ok:
                StringBuilder builder = new StringBuilder();
                for(int i=0; i<check_state.size(); i++){
                    if(check_state.get(i) == true){
                        builder.append(listViewAdapter.getItem(i).getNumberSet().replace(" ",","));
                        builder.append("\n");
                    }
                }
                // 체크한 숫자가 있다면 if문 성립
                if(builder.length()>1){
                    builder.deleteCharAt(builder.length()-1);                   // 마지막 공백 제거
                    String[] test = builder.toString().split("\n");      // 줄바꿈 단위로 나누기

                    DBOpenHelper dbOpenHelper = new DBOpenHelper(context);      // DB저장위해 DBOpenHelper 생성
                    int[] insertState = new int[test.length];                   // DB저장 성공,실패 판단위해 생성
                    // builder 초기화(비우기)
                    builder.setLength(0);
                    for(int i=0; i<test.length; i++){                           // 배열 개수만큼 DB Insert 반복
                        insertState[i] = dbOpenHelper.insertDB(turn, test[i]);                   // Insert(회차정보, 번호세트)
                        if(insertState[i]==1){
                            builder.append(test[i].replace(",", " ")+"\n");
                        }
                    }
                    // 데이터베이스에 중복없이 저장성공이 된 경우, builder크기가 1보다 큼
                    if(builder.length()>1){
                        Toast.makeText(context,"저장",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"저장 실패 (중복)",Toast.LENGTH_SHORT).show();
                    }
                }
                dlg.dismiss();
                break;

            case R.id.QR_Dialog_cancel:
                dlg.dismiss();
                break;
        }
    }

    public class ListViewAdapter extends BaseAdapter {
        private ArrayList<ListItem> listViewItemList = new ArrayList<>();

        public ListViewAdapter(ArrayList<ListItem> items){
            listViewItemList.addAll(items);
            for(int i=0; i<listViewItemList.size();i++){
                check_state.add(false);
            }
        }

        @Override
        public int getCount() {
            return listViewItemList.size();
        }

        @Override
        public ListItem getItem(int position) {
            return listViewItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.dialog_qritem, parent, false);
            }

            TextView item_type = (TextView) convertView.findViewById(R.id.item_type);
            TextView item_result = (TextView) convertView.findViewById(R.id.item_result);

            Resources res = context.getResources();
            ImageView[] listView_Num = new ImageView[6];
            for (int i=0; i<listView_Num.length; i++){
                int id = res.getIdentifier("QR_List_num"+(i+1), "id", context.getPackageName());
                listView_Num[i] = convertView.findViewById(id);
            }

            //TextView item_numberSet = (TextView) convertView.findViewById(R.id.item_numberSet);

            CheckBox item_checkbox = convertView.findViewById(R.id.item_checkbox);

            ListItem listItem = listViewItemList.get(position);
            item_type.setText(listItem.getType());
            item_result.setText(listItem.getResult());

            String[] number_set = listItem.getNumberSet().split(" ");
            for (int i=0; i<listView_Num.length; i++){
                int number = Integer.parseInt(number_set[i]);
                listView_Num[i].setImageResource(MainActivity.num_ID[number-1]);
            }
            //item_numberSet.setText(listItem.getNumberSet());


            item_checkbox.setChecked(false);
            item_checkbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        check_state.set(position, true);
                    } else {
                        check_state.set(position, false);
                    }
            }
            });
            return convertView;
        }
    }

    public class Parser extends AsyncTask<String, Void, Document> {

        public Parser() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... Strings) {
            try {
                //String str = String.format("https://m.dhlottery.co.kr/qr.do?method=winQr&v=%s", Strings[0].split("v=")[1]);
                String url = Strings[0].replace(
                        "http://m.dhlottery.co.kr/?",
                        "https://m.dhlottery.co.kr/qr.do?method=winQr&"
                );
                Log.d("바코드", "Format된 주소 \n\t"+url);
                Document doc = Jsoup.connect(url).get();
                return doc;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("바코드", "try문 밖에서 return doc");
            return null;
        }

        @Override
        protected void onPostExecute(Document doc) {
            String qr_Turn = "";
            String qr_Date = "";
            String qr_ResultNum = "";
            String qr_ResultInfo = "";

            if(doc == null){
                Toast.makeText(context, "Qr코드 오류", Toast.LENGTH_SHORT).show();
            } else {
                // 사용자가 선택한 번호들 정보 파싱
                Element ele1 = doc.select("div.list_my_number tbody").first();
                // <tr>태그, 사용자 numberSet 갯수 구분
                Elements ele2 = ele1.select("tr");
                ArrayList<ListItem> list = new ArrayList<>();

                for(Element ele : ele2){
                    Elements sub_ele = ele.select("td");

                    ListItem item = new ListItem();
                    item.setType(ele.select("th").text());         // A,B,C,D,E 타입 (숫자Set 구분)
                    item.setResult(sub_ele.get(0).text());                  // <td class="result">낙첨/당첨</td>
                    item.setNumberSet(sub_ele.get(1).text());               // <td> -> <span> 태그내 번호들 (숫자 6개)
                    list.add(item);                                         // ArrayList 추가
                }

                Element info_ele = doc.select("div.winner_number").first();

                StringBuilder info_builder = new StringBuilder();

                // 해당 회차 <span class="key_clr1">
                qr_Turn = info_ele.selectFirst("span.key_clr1").text();
                turn = Integer.parseInt(qr_Turn.replace("제","").replace("회",""));
                Log.d("바코드","QR 회차" +turn);
                QR_Dialog_Turn.setText(qr_Turn);

                // 추첨 날짜 <span class="date">
                qr_Date = info_ele.selectFirst("span.date").text();
                QR_Dialog_Date.setText(qr_Date);

                //이미 추첨된 경우 <div class="bx_winner winner> 내부 <strong class="tit"> 존재
                Element result_ele = info_ele.selectFirst("div.bx_winner.winner");
                //<strong class="tit"> 존재해서 파싱이 되었으면 null이 아님
                if(result_ele !=null){
                    QR_Dialog_numLayout.setVisibility(View.VISIBLE);

                    // <strong class="tit"> 내용 출력
                    qr_ResultNum = result_ele.selectFirst("div.list").text();
                    String[] result_Numset = qr_ResultNum.split(" ");
                    for(int i=0; i<result_Numset.length; i++){
                        int num = Integer.parseInt(result_Numset[i]);
                        QR_Dialog_numset[i].setImageResource(MainActivity.num_ID[num-1]);
                    }
                    //QR_Dialog_ResultNum.setText(qr_ResultNum);
                    //QR_Dialog_ResultNum.setVisibility(View.VISIBLE);

                    // <div class="list"> 추첨번호 출력


                    // 이미 추첨된 경우       (당첨,낙첨 결과 출력)
                    qr_ResultInfo = info_ele.selectFirst("div.bx_notice.winner span").text()
                            + "\n"
                            + info_ele.selectFirst("div.bx_notice.winner strong").text();
                } else {
                    // 아직 미추첨 경우       (미추첨 복권입니다.(20시 45분 추첨예정)
                    QR_Dialog_numLayout.setVisibility(View.GONE);
                    //QR_Dialog_ResultNum.setVisibility(View.GONE);
                    qr_ResultInfo = info_ele.selectFirst("div.bx_notice.winner strong").text()
                            + "\n"
                            + info_ele.selectFirst("div.bx_notice.winner span").text();
                }
                QR_Dialog_ResultInfo.setText(qr_ResultInfo);

                callDialog(list);
            }
            super.onPostExecute(doc);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Document doc) {
            super.onCancelled(doc);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }
}
