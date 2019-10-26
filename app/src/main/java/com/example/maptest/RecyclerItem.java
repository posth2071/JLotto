package com.example.maptest;

public class RecyclerItem {
    public int drawableId;
    public String storeName,storeLocation;
    public RecyclerItem(int drawableId, String storeinfo){
        this.drawableId = drawableId;
        int index = storeinfo.indexOf(",");
        this.storeName = storeinfo.substring(0,index);
        this.storeLocation = storeinfo.substring(index+1);
    }
}