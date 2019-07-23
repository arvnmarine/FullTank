package com.example.fulltank;




import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private final int INTERNET_PERMISSION_CODE = 1;
    private SwipeRefreshLayout pullToRefresh;
    private ArrayList<GasStation> menu;
    private GasStationDataAdapter adapter;
    private View view;
    private Button btnRefresh;

    public HomeFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard




        view = inflater.inflate(R.layout.fragment_home, null);
        ListView list = (ListView) view.findViewById(R.id.gasListView);


        ArrayList<String> names = new ArrayList<>();
        names.add("Mitch");
        names.add("Blake");
        names.add("Shelly");
        names.add("Jess");
        names.add("Steve");
        names.add("Mohammed");

        ArrayAdapter adapter = new ArrayAdapter((MainActivity)getActivity(), R.layout.row_item, names);
        list.setAdapter(adapter);
        return view;
    }





}
