package com.example.maptest.AsyncTask;

public class testset {
    private int number;
    private int imgid;
    private int tag = 0;

    public testset(int number, int imgid, int tag){
        this.number = number;
        this.imgid  =imgid;
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }
    public void setTag(int tag){
        this.tag = tag;
    }

    public int getImgid(){
        return imgid;
    }
    public void setImgid(int imgid){
        this.imgid = imgid;
    }

    public int getNumber(){
        return number;
    }
    public void setNumber(int number){
        this.number = number;
    }
}
