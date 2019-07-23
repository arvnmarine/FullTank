package com.example.fulltank;

public class Coordination {
    private double Longitude, Lattitude;
    public Coordination(double latti, double longi){
        Longitude = longi;
        Lattitude = latti;
    }


    public String GetLatAsString(){
        return String.valueOf(Lattitude);
    }

    public String GetLongAsString(){
        return String.valueOf(Longitude);
    }
}
