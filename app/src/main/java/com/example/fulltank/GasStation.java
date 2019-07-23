package com.example.fulltank;

public class GasStation {
    private String name, address;
    private boolean isOpenNow, hasCarWash;
    private double regPrice, midPrice, premPrice;

    public GasStation(String _name, String _address, Boolean _isOpenNow, Boolean _hasCarWash){
        name = _name;
        address = _address;
        isOpenNow = _isOpenNow;
        hasCarWash = _hasCarWash;
        regPrice = midPrice = premPrice = 0.0;
    }

    public String GetName(){
        return name;
    }


    public double GetRegPrice(){
        return regPrice;
    }
    public double GetMidPrice(){
        return regPrice;
    }
    public double GetPremPrice(){
        return regPrice;
    }

    public double SetRegPrice(double x){
        regPrice = x;
        return regPrice;
    }

    public double SetMidPrice(double x){
        midPrice = x;
        return midPrice;
    }
    public double SetPremPrice(double x){
        premPrice = x;
        return premPrice;
    }

    public boolean isOpenNow(){
        return isOpenNow;
    }

    public boolean hasCarWash(){
        return hasCarWash;
    }

    public String GetAddress(){
        return address;
    }
}
