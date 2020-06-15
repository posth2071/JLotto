package com.JLotto.JLotto.AlarmPackage;

import android.os.AsyncTask;

import com.JLotto.JLotto.AsyncTask.LottoParsingInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Alarm_TaskClass extends AsyncTask<Void, Void, LottoParsingInfo> {

    public Alarm_TaskClass() {
        super();
    }

    @Override
    protected LottoParsingInfo doInBackground(Void... voids) {
        LottoParsingInfo lottoParsingInfo = new LottoParsingInfo();
        try {
            Document doc1 = Jsoup.connect("https://dhlottery.co.kr/gameResult.do?method=byWin&wiselog=C_A_1_2").get();
            Elements contents1 = doc1.select("div.win_result h4");  // 회차정보 파싱
            String turn = contents1.text().replace("회 당첨결과","");    //해당회차 번호저장

            contents1 = doc1.select("div.win_result p.desc");   // 추첨날짜 파싱
            String datetest = contents1.text()
                    .replace("(","")
                    .replace(")","");

            // 당첨번호 (6개) 파싱
            contents1 = doc1.select("div.num.win p");
            String infotest = contents1.text();

            // 보너스번호 파싱
            contents1 = doc1.select("div.num.bonus p");
            infotest += " " + contents1.text(); // 당첨번호 (6) + 보너스번호(1)

            // 당첨세부정보 (등수 / 해당등수 총금액 / 해당등수 당첨자수 / 개인당 금액 / 당첨기준 / 비고 -제외)
            Elements elements = doc1.select("tbody tr");
            for (Element mainele : elements){                               //Elements들을 각각 접근하는 반복문
                Elements ele3 = mainele.select("td");               //td로 다시 Elements로 나누기
                ArrayList<String> testlist = new ArrayList<>();             //임시ArrayList 생성
                for(int i=0; i<5; i++) {                                    //세부정보(비고를 제외한 나머지 추출)
                    testlist.add(ele3.get(i).text());
                }
                //subinfo에 저장
                lottoParsingInfo.subInfo.add(testlist);
            }

            lottoParsingInfo.setTurn(Integer.parseInt(turn));
            lottoParsingInfo.setDate(datetest);
            lottoParsingInfo.setLottoInfo(infotest.split(" "));

            return lottoParsingInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(LottoParsingInfo result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}