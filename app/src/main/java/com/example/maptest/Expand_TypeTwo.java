package com.example.maptest;

import java.util.List;

public class Expand_TypeTwo {
    private int turn;
    private String[] lottoNumSet;
    private String hallpair;
    private String result;

    //생성자 함수
    public Expand_TypeTwo(){}
    public Expand_TypeTwo(int turn, String[] lottoNumSet, String hallpair, String result){
        this.turn = turn;
        this.lottoNumSet = lottoNumSet;
        this.hallpair = hallpair;
        this.result = result;
    }

    //Turn(회차) 설정 / 반환
    public void setTurn(int turn) {
        this.turn = turn;
    }
    public int getTurn() {
        return turn;
    }

    //LottoNumSet(번호집합) 설정 / 반환
    public void setLottoNumSet(String[] lottoNumSet) {
        this.lottoNumSet = lottoNumSet;
    }
    public String[] getLottoNumSet() {
        return lottoNumSet;
    }

    //Hallpair(홀짝비율) 설정 / 반환
    public void setHallpair(String hallpair) {
        this.hallpair = hallpair;
    }
    public String getHallpair() {
        return hallpair;
    }

    //Result(결과) 설정 / 반환
    public void setResult(String result) {
        this.result = result;
    }
    public String getResult() {
        return result;
    }
}
