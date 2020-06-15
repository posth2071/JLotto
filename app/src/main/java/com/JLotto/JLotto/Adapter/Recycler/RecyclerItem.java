package com.JLotto.JLotto.Adapter.Recycler;

public class RecyclerItem {
    public int drawableId;
    public String storeName,storeLocation;

    //리사이클러뷰 아이템 생성자함수
    public RecyclerItem(int drawableId, String[] storeinfo){
        this.drawableId = drawableId;
        this.storeName = storeinfo[0];          //매장명
        this.storeLocation = storeinfo[1];      //주소
    }
}