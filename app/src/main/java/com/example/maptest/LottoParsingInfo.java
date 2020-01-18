package com.example.maptest;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LottoParsingInfo {
    public int turn = 0;                                                //회차정보
    public String date = new String();                                  //추첨날짜
    public String[] lottoInfo= new String[9];                           //로또번호
    public ArrayList<ArrayList<String>> subInfo = new ArrayList<>();    //보조정보(1~5등 당첨정보) (등수 / 해당등수 총금액 / 해당등수 당첨자수 / 개인당 금액 / 당첨기준 / 비고 -제외)
    public ArrayList<String[]> storeList = new ArrayList<>();           //1등 당첨매장정보

    //생성자 함수
    public LottoParsingInfo(){ }
    //생성자 함수
    public LottoParsingInfo(String[] lottoInfo, String date){
        this.lottoInfo = lottoInfo;
        this.date = date;
    }

    //Turn(회차정보) 설정,얻기
    public void setTurn(int turn){
        this.turn = turn;
    }
    public int getTurn(){
        return this.turn;
    }

    //로또번호 설정,얻기
    public void setLottoInfo(String[] lottoinfo){
        this.lottoInfo = lottoinfo;
    }
    public String[] getLottoInfo(){
        return this.lottoInfo;
    }

    //Date(추첨날짜) 설정 / 얻기
    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return date;
    }


    //SubInfo(1~5등 당첨정보) 설정 / 얻기
    public void setSubInfo(ArrayList<ArrayList<String>> subInfo){
        this.subInfo = subInfo;
    }
    public ArrayList<ArrayList<String>> getSubInfo(){
        return this.subInfo;
    }

    //StoreList(당첨매장정보) 설정 / 얻기
    public void setStoreList(ArrayList<String[]> storeList){
        this.storeList = storeList;
    }
    public ArrayList<String[]> getStoreList(){
        return this.storeList;
    }
}
