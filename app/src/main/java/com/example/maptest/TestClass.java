package com.example.maptest;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class TestClass {
    //private String[] test;
    LottoParsingInfo lottoParsingInfo;
    // 생성자함수
    public TestClass() { }

    // Parsing함수
    public LottoParsingInfo parsing(String num) {

        //TaskClass 객체 생성
        TaskClass task = new TaskClass();
        try {
            if (task.getStatus() == AsyncTask.Status.RUNNING)
                task.cancel(true);
        } catch (Exception e) {
        }
        task.execute(num);
        try {
            //task 끝나면 결과값받기 끝날때까지 대기
            lottoParsingInfo = task.get();
            //test = task.get();
            //task 결과받고 task객체 소멸
            if (task.getStatus() == AsyncTask.Status.RUNNING)
                task.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lottoParsingInfo;
    }

    public class TaskClass extends AsyncTask<String, Void, LottoParsingInfo> {
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
        protected LottoParsingInfo doInBackground(String... num) {
            String store = new String();
            String text = new String();

            LottoParsingInfo resultParsingInfo = new LottoParsingInfo();

            Document doc1;
            try {
                if (num[0] == "") {           //""인경우 제일최신회차 파싱
                    //1등 당첨장소 파싱
                    doc1 = Jsoup.connect("https://www.dhlottery.co.kr/store.do?method=topStore&pageGubun=L645").get();
                    Element ele1 = doc1.select("div.group_content tbody").first(); //1등 당첨장소만 얻기위해 first함수설정
                    Elements ele2 = ele1.select("tr");          //얻은 html태그중 tr로 나누기 (나눠진 총인덱스가 1등당첨자수)
                    for (Element ele : ele2) {
                        // 5개 항목 (번호, 상호명, 구분, 소재지, 위치보기)
                        Elements ele3 = ele.select("td");
                        // 상호명, 소재지만 구하기 (인덱스 1,3)
                        String[] str = new String[]{
                                ele3.get(1).text(),     //인덱스[0], 상호명
                                ele3.get(3).text()};    //인덱스[1], 주소
                        //storeList에 추가
                        resultParsingInfo.storeList.add(str);
                    }

                    //마지막회차 당첨번호 파싱
                    doc1 = Jsoup.connect("https://dhlottery.co.kr/gameResult.do?method=byWin&wiselog=C_A_1_2").get();
                } else {    //num[0]==""이 아닌경우 (회차입력한 경우)
                    // 검색회차 파싱
                    doc1 = Jsoup.connect("https://www.nlotto.co.kr/gameResult.do?method=byWin&drwNo=" + num[0]).get();  // 검색회차 파싱
                }
                Elements contents1 = doc1.select("div.win_result h4");  // 회차정보 파싱
                //해당회차 번호저장
                resultParsingInfo.setTurn(Integer.parseInt(
                        contents1.text().replace("회 당첨결과","")));

                // 추첨날짜 파싱
                contents1 = doc1.select("div.win_result p.desc");
                //String datetest = contents1.text()
                //      .replace("(","")
                //    .replace(")","");
                //추첨날짜 저장
                resultParsingInfo.setDate(contents1.text()
                        .replace("(","")
                        .replace(")",""));
                // 당첨번호 (6개) 파싱
                contents1 = doc1.select("div.num.win p");
                String infotest = contents1.text();

                // 보너스번호 파싱
                contents1 = doc1.select("div.num.bonus p");
                infotest += " " + contents1.text();
                resultParsingInfo.lottoInfo = infotest.split(" ");      //로또번호 (보너스포함) split함수이용 배열로 저장

                // 당첨세부정보 (등수 / 해당등수 총금액 / 해당등수 당첨자수 / 개인당 금액 / 비고)
                Elements elements = doc1.select("tbody tr");
                for (Element mainele : elements){                               //Elements들을 각각 접근하는 반복문
                    Elements ele3 = mainele.select("td");               //td로 다시 Elements로 나누기
                    ArrayList<String> testlist = new ArrayList<>();             //임시ArrayList 생성
                    for(int i=0; i<4; i++) {                                    //세부정보(비고를 제외한 나머지 추출)
                        testlist.add(ele3.get(i).text());
                    }
                    //subinfo에 저장
                    resultParsingInfo.subInfo.add(testlist);
                }

                //로그출력 (resultParsingInfo 데이터)
                lottoInfoLog(resultParsingInfo);

            } catch (IOException e) {
                e.printStackTrace();
            };
            return resultParsingInfo;
        }

        @Override
        protected void onPostExecute(LottoParsingInfo resultParsingInfo) {
            super.onPostExecute(resultParsingInfo);
        }


        // Log출력 메소드
        public void lottoInfoLog(LottoParsingInfo resultParsingInfo){
            String str = "\n회차 => "+resultParsingInfo.turn
                    +"\n추첨날짜 => "+resultParsingInfo.date
                    +"\n당첨번호 => "+Arrays.toString(resultParsingInfo.lottoInfo);

            String str1 = "";
            for(int i=0; i<resultParsingInfo.subInfo.size(); i++) {
                str1 += "\nsubinfo index [" + i + "] \n \t\t\t => ";
                Iterator<String> it = resultParsingInfo.subInfo.get(i).iterator();
                while(it.hasNext()){
                    str1 += it.next() + "\t\t";
                }
            }
            str += str1;

            String str2 = "";
            for(int i=0; i<resultParsingInfo.storeList.size(); i++){
                str2 += "\nstorList index ["+i+"] \n \t\t\t => "
                        +resultParsingInfo.storeList.get(i)[0]
                        + "\t\t"
                        +resultParsingInfo.storeList.get(i)[1];
            }
            str += str2;
            Log.d("LottoParsingInfo", "\n "+str);
        }
    }
}