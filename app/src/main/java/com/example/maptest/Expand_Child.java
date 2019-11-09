package com.example.maptest;

import java.util.ArrayList;

public class Expand_Child {
    //Type_One - 등수 Rank / 총금액 TotalMoney / 당첨자수 People / 개인금액 Personal Money / 기준 Norm
    public ArrayList<ArrayList<String>> typeOne=  null;

    //Type_Two - 회차 Trun / 로또번호 LottoNumset / 홀짝비율 Hallpair / 당첨결과 Result
    public ArrayList<DBinfo> typeTwo = null;

    //Type_Three - 당첨금지급기한
    public ArrayList<String> typethree = null;

    public Expand_Child(){}

    //
    public void setTypeOne(ArrayList<ArrayList<String>> typeOne) {
        this.typeOne = typeOne;
    }
    public ArrayList<ArrayList<String>> getTypeOne() {
        return typeOne;
    }

    //
    public void setTypeTwo(ArrayList<DBinfo> typeTwo) {
        this.typeTwo = typeTwo;
    }
    public ArrayList<DBinfo> getTypeTwo() {
        return typeTwo;
    }

    //
    public void setTypethree(ArrayList<String> typethree) {
        this.typethree = typethree;
    }
    public ArrayList<String> getTypethree() {
        return typethree;
    }
}
