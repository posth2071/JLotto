package com.example.maptest.QRCord;

public class ListItem{
    private String type = "";
    private String result = "";
    private String numberSet = "";

    public ListItem(String[] text){
        type = text[0];
        result = text[1];
        numberSet = text[2];
    }
    public ListItem(){}

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setResult(String result) {
        this.result = result;
    }
    public String getResult() {
        return result;
    }

    public void setNumberSet(String numberSet) {
        this.numberSet = numberSet;
    }
    public String getNumberSet() {
        return numberSet;
    }
}