package com.example.fulltank;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OptionFragment extends Fragment {

    public String ZipData;
    private Button btnUserGPS, btnUseZipcode;
    private EditText UserZipcode;
    private View view;

    public OptionFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard

        Log.d("OPTION VIEW", "opt frag clicked");


        view = inflater.inflate(R.layout.fragment_home, null);
        SetUpUI();
        return view;
    }

    private void SetUpUI(){

        UserZipcode =view.findViewById(R.id.zipcodeUserInput);


        btnUserGPS = view.findViewById(R.id.btnUserGPSLocation);
        btnUserGPS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                UserZipcode.setText("");
                ((MainActivity) getActivity()).ChangeUserLocation("");
            }
        });


        btnUseZipcode = view.findViewById(R.id.btnZipcode);
        btnUseZipcode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String zip = String.valueOf(UserZipcode.getText());
                Log.d("OptionFrag", "new location " + zip);
                ((MainActivity) getActivity()).ChangeUserLocation(zip);
            }
        });
    }
}
