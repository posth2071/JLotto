package com.example.maptest;

import java.util.ArrayList;

public class Expand_TypeOne {
    private String rank;            // 등수
    private String totalMoney;      // 총금액
    private String people;          // 당첨자수
    private String persnalMoney;    // 개인금액
    private String norm = "테스트";

    public Expand_TypeOne(){}

    public Expand_TypeOne(String rank, String totalMoney, String people, String persnalMoney, String norm){
        this.rank = rank;
        this.totalMoney =totalMoney;
        this.people = people;
        this.persnalMoney = persnalMoney;
        this.norm = norm;
    }
    public Expand_TypeOne(ArrayList<String> subInfo){
        rank = subInfo.get(0);
        totalMoney = subInfo.get(1);
        people = subInfo.get(2);
        persnalMoney = subInfo.get(3);
    }

    public String[] getTotalInfo(){
        String[] totalInfo = new String[]{rank, totalMoney, people, persnalMoney, norm};
        return totalInfo;
    }
}
