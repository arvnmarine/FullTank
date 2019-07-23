package com.example.fulltank;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class GasStationDataAdapter extends ArrayAdapter{

    private Context context;
    private GasStation gasStation;
    private ArrayList<GasStation> stationList;

    public GasStationDataAdapter(@NonNull Context context, ArrayList<GasStation> list){
        super(context, 0 , list);
        this.context = context;
        stationList = list;

        this.context=context;
        this.gasStation = gasStation;

    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.row_item,parent,false);

        GasStation currentStation = stationList.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.Name);
        TextView addr = (TextView) convertView.findViewById(R.id.address);
        TextView regPrice = (TextView) convertView.findViewById(R.id.regPrice);
        TextView midPrice = (TextView) convertView.findViewById(R.id.midPrice);
        TextView premPrice = (TextView) convertView.findViewById(R.id.premPrice);

        name.setText(currentStation.GetName());
        addr.setText(currentStation.GetAddress());


        double nPrice = currentStation.GetRegPrice();
        if (nPrice > 0.01){
        regPrice.setText(String.valueOf(nPrice)); }else{
        regPrice.setText("-");}

        nPrice = currentStation.GetMidPrice();
        if (nPrice > 0.01){
            midPrice.setText(String.valueOf(nPrice)); }else{
            midPrice.setText("-");}

        nPrice = currentStation.GetPremPrice();
        if (nPrice > 0.1){
            premPrice.setText(String.valueOf(nPrice)); }else{
            premPrice.setText("-");}



        return listItem;
    }
}
