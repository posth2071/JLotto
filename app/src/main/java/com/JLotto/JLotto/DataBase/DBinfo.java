package com.JLotto.JLotto.DataBase;

public class DBinfo {
    int turn;
    String numset;

    public DBinfo(){}           //기본 생성자

    public DBinfo(int turn, String numberset){
        this.turn = turn;
        this.numset = numberset;
    }

    public int getTurn(){
        return turn;
    }

    public void setTurn(int turn){
        this.turn = turn;
    }

    public String getNumset() {
        return numset;
    }

    public void setNumset(String numset) {
        this.numset = numset;
    }

    public String getInfo(){
        String set = String.format("turn - %d, numset - %s",turn, numset);
        return  set;
    }

}
