package com.example.maptest;

public class DBinfo {
    int turn;
    String numberset;
    String hallfair;
    String result;

    public DBinfo(){}           //기본 생성자

    public DBinfo(int turn, String numberset, String hallfair, String result){
        this.turn = turn;
        this.numberset = numberset;
        this.hallfair = hallfair;
        this.result = result;
    }

    public int getTurn(){
        return turn;
    }

    public void setTurn(int turn){
        this.turn = turn;
    }


    public String getNumberset(){
        return numberset;
    }

    public void setNumberset(String numberset){
        this.numberset = numberset;
    }

    public String getHallfair(){
        return hallfair;
    }

    public void setHallfair(String hallfair){
        this.hallfair = hallfair;
    }

    public String getResult(){
        return result;
    }

    public void setResult(String result){
        this.result = result;
    }

    public String getInfo(){
        String set = String.format("turn - %d, numset - %s, hallpair - %s, result - %s",turn, numberset, hallfair, result);
        return  set;
    }
}
