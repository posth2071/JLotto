package com.example.maptest;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LottoParsingInfo {
    public int turn = 0;
    public String[] lottoInfo= new String[9];
    public ArrayList<ArrayList<String>> subInfo = new ArrayList<ArrayList<String>>();
    public String date = new String();
    public ArrayList<String[]> storeList = new ArrayList<>();

    public LottoParsingInfo(){ }

    public LottoParsingInfo(String[] lottoInfo, String date){
        this.lottoInfo = lottoInfo;
        this.date = date;
    }

    //Turn(회차정보)설정,얻기
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
