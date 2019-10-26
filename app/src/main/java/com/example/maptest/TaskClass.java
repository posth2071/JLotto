package com.example.maptest;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class TaskClass extends AsyncTask<String, Void, String[]> {
    public TaskClass() {
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String... num) {
        String store = new String();
        String text = new String();
        String[] result = new String[9];
        Document doc1;
        try {
            if (num[0] == "") {
                //1등 당첨장소 파싱
                doc1 = Jsoup.connect("https://www.dhlottery.co.kr/store.do?method=topStore&pageGubun=L645").get();
                Element ele1 = doc1.select("div.group_content tbody").first(); //1등 당첨장소만 얻기위해 first함수설정
                Elements ele2 = ele1.select("tr");          //얻은 html태그중 tr로 나누기 (나눠진 총인덱스가 1등당첨자수)
                for (Element ele : ele2) {
                    Elements ele3 = ele.select("td");           // 5개 항목으로 나누어짐 (번호, 상호명, 구분, 소재지, 위치보기)
                    store += ele3.get(1).text() + "," + ele3.get(3).text() + "\n";   // 상호명, 소재지만 구하기
                }

                MainActivity.store = store.split("\\n");                            //메인액티비티의 str변수에 값저장

                //마지막회차 당첨번호 파싱
                doc1 = Jsoup.connect("https://dhlottery.co.kr/gameResult.do?method=byWin&wiselog=C_A_1_2").get();

            } else {
                // 검색회차 파싱
                doc1 = Jsoup.connect("https://www.nlotto.co.kr/gameResult.do?method=byWin&drwNo=" + num[0]).get();  // 검색회차 파싱

            }
            Elements contents1 = doc1.select("div.win_result h4");
            text = contents1.text();                                     // 회차정보 파싱
            contents1 = doc1.select("div.num.win p");
            text += " " + contents1.text();                                     //
            contents1 = doc1.select("div.num.bonus p");
            text += " " + contents1.text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        result = text.split(" ");
        return result;
    }
        /*
        try {
            Document doc1;
            doc1 = Jsoup.connect("https://www.dhlottery.co.kr/store.do?method=topStore&pageGubun=L645").get();
            //Element contents1 = doc1.select("div.group_content").first();
            Element contents1 = doc1.select("div.group_content tbody").first(); //1등 당첨장소만 얻기위해 first함수설정
            //countnum = contents1.size();
            //for(Element ele : contents1){
              //  countnum += 1;
            //}
            //str1 = String.valueOf(countnum);
            //str1 = contents1.get(24).text();   //2등까지 모두파싱됨
            Elements ele = contents1.select("tr");          //얻은 html태그중 tr로 나누기 (나눠진 총인덱스가 1등당첨자수)
            for (Element test : ele){
                Elements ele2 = test.select("td");           // 5개 항목으로 나누어짐 (번호, 상호명, 구분, 소재지, 위치보기) 원하는건 번호,상호명,소재지
                //str1 = str1 + ele2.get(0).text() +" " + ele2.get(1).text() +" " + ele2.get(3).text() +"\n";
                //main.mainstr[countnum] = ele2.get(0).text() +"," + ele2.get(1).text() +"," + ele2.get(3).text();
                //str[countnum] = ele2.get(0).text() +"," + ele2.get(1).text() +"," + ele2.get(3).text();
                str = str + ele2.get(1).text() + "," + ele2.get(3).text() + "\n";
                // str1 = ele2.get(1).text() +" ";
                // str1 = ele2.get(3).text() +"\n";
            }
            //Elements ele2 = ele.get(0).select("td");        //
            //str1 = String.valueOf(ele2.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
        //return str1;

        /*
        try {
            Document doc1;
            if(num[0] == ""){                   //전달받은 num이 ""이면 마지막회차 파싱
                doc1 = Jsoup.connect("https://dhlottery.co.kr/gameResult.do?method=byWin&wiselog=C_A_1_2").get();
            } else {                            // num이 검색원하는 회차이면 그회차 검색
                doc1 = Jsoup.connect("https://www.nlotto.co.kr/gameResult.do?method=byWin&drwNo=" + num[0]).get();
            }
            Elements contents1 = doc1.select("div.win_result h4");
            String str1 = contents1.text();
            contents1 = doc1.select("div.num.win p");
            String str2 = contents1.text();
            contents1 = doc1.select("div.num.bonus p");
            String str3 = contents1.text();
            String str4 = str1 + " " + str2 + " " + str3;
            return str4;
        } catch (IOException e) {
            e.printStackTrace();
        }

         */



    @Override
    protected void onPostExecute(String[] s) {
        super.onPostExecute(s);
    }
}