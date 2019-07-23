package com.example.fulltank;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import android.content.Intent;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;




public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private TextView mTextMessage;
    private Fragment mFragment, mHomeFrag, mOptionFrag, mAboutFrag;
    private Coordination mLocation;
    private final int LOCATION_PERMISSION_CODE = 1;
    private final int INTERNET_PERMISSION_CODE = 2;
    private String mZipCode;
    private ArrayList<GasStation> GasStationList;
    private boolean isForgingData;


    public ArrayList<GasStation> getGasStationList(){
        return GasStationList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView  = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        isForgingData = false;
        mHomeFrag = new HomeFragment();
        mOptionFrag = new OptionFragment();
        mAboutFrag = new AboutFragment();
        mZipCode= "";

        requestLocationPermission();

        mLocation = GetGPSLocation();

        requestInternetPermission();
        loadFragment(mHomeFrag);

        Thread thread = new Thread() {
            @Override
            public void run() {


                ForgeData();
            }
        };
        thread.start();



    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {

            case R.id.navigation_home:

                Log.d(Constants.TAG, "Passing data " + mZipCode);
                fragment = mHomeFrag;
                break;

            case R.id.navigation_option:

                fragment = mOptionFrag;
                break;

            case R.id.navigation_about:
                fragment = mAboutFrag;

                break;
        }

        return loadFragment(fragment);
    }


    private void requestLocationPermission() {


        int permission = PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PermissionChecker.PERMISSION_GRANTED) {
            // good to go
            Log.d(Constants.TAG, "You had FINE_LOCATION permission");
            GetGPSLocation();
        } else {
            Log.d(Constants.TAG, "You need FINE_LOCATION permission");
            // permission not granted, you decide what to do
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }



    }

    private void requestInternetPermission() {
        int permission = PermissionChecker.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (permission == PermissionChecker.PERMISSION_GRANTED) {
            // good to go
            Log.d(Constants.TAG, "You had INTERNET permission");

            return;
        } else {
            Log.d(Constants.TAG, "You need INTERNET permission");

            try {
                while (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED){
                    //wait for GPS Location permission first
                    sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Errors detected, closing app...", Toast.LENGTH_SHORT).show();
                closeNow(); //exit app
            }


            Thread thread = new Thread() {
                @Override
                public void run() {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
                }
            };

            thread.start();


            // permission not granted, you decide what to do

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(Constants.TAG, String.valueOf(requestCode));


        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Permission DENIED, closing app...", Toast.LENGTH_SHORT).show();
            closeNow(); //exit if user does not grant permission
        } else {
            Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
        }
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE:

                GetGPSLocation();

                break;

            case INTERNET_PERMISSION_CODE:
                mFragment = new HomeFragment();
                loadFragment(mFragment);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void closeNow() {
        Log.d(Constants.TAG, "Program is exiting!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            finishAffinity();
        }

        else
        {
            finish();
        }
    }


    private Coordination GetGPSLocation(){
        //Get GPS location
        double latti, longi;
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            //wait for user to grant permission
            Toast.makeText(MainActivity.this, "Errors detected, closing app...", Toast.LENGTH_SHORT).show();
            closeNow(); //exit app
            return null;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            }

        }


        Coordination coord = null;
        if (location != null){
            latti = location.getLatitude();
            longi = location.getLongitude();
            coord = new Coordination(latti,longi);


            String lattitude = String.valueOf(latti);
            String longitude = String.valueOf(longi);
            Log.d(Constants.TAG,"Your current location is"+ "\n" + "Lattitude = " + lattitude
                    + "\n" + "Longitude = " + longitude);
        } else {
            Log.d(Constants.TAG,"Unable to get location from device");
        }


        return coord;

    }



    public void ChangeUserLocation(String loc){


        if (mZipCode.equals(loc))
            return;
        Log.d("MAIN ACTIVITY", "new location " + loc);
        mZipCode = loc;

        Toast.makeText(MainActivity.this, "Location updated!", Toast.LENGTH_SHORT).show();


        Thread thread = new Thread() {
            @Override
            public void run() {
                if (mZipCode.equals("")){
                    mLocation = GetGPSLocation();
                } else {
                    //TO-DO: get Location of zipcode using GoogleMap API
                    mLocation = Zip2Loc(mZipCode);
                    Log.d("Zip2Loc", mLocation.GetLatAsString() + " " + mLocation.GetLongAsString());
                }

                //Forged data
                ForgeData();
            }
        };

        thread.start();



    }

    public boolean isForgingData(){
        return isForgingData;
    }

    public void ForgeData(){
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.INTERNET) != PermissionChecker.PERMISSION_GRANTED) {
            // good to go
            Log.d(Constants.TAG, "You don't have INTERNET permission to forge data");

            return;
        }
        if (isForgingData)
            return;


        isForgingData = true;


        //TO-DO: get gas station data using GoolgeMap API
        String response = HttpGet(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        + mLocation.GetLatAsString() + "," + mLocation.GetLongAsString()
                        + "&radius=7000&type=gasoline&keyword=gasoline"
                        + "&key=" + Constants.API_Key);
        //Log.d("GoogleMaps API", response);

        try {

            JSONObject rootObj = new JSONObject(response);


            //Log.d("Location value ", obj.getString("location"));


            JSONArray resultObj = rootObj.getJSONArray("results");
            int n = resultObj.length();
            GasStationList = new ArrayList<GasStation>();
            for (int i=0; i<n;i++){
                JSONObject obj = resultObj.getJSONObject(i);
                boolean hasCarWash = false;
                if (obj.has("types")) {
                    JSONArray types = obj.getJSONArray("types");
                    int m = types.length();

                    for (int j = 0; j < m; j++) {
                        if (types.get(j).toString().equals("car_wash")) {
                            hasCarWash = true;
                            break;
                        }
                    }
                }


                boolean isOpenNow = false;
                if (obj.has("opening_hours")){
                    JSONObject opening_hours = obj.getJSONObject("opening_hours");
                    if (opening_hours.has("open_now")){
                        isOpenNow = opening_hours.getBoolean("open_now");

                    }
                }

                GasStationList.add(
                    new GasStation(obj.getString("name"),
                                    obj.getString("vicinity"),
                                    isOpenNow,
                                    hasCarWash)
                );


            }

        } catch (Throwable tx) {
            Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
            isForgingData = false;
            return;
        }

        //TO-DO: snip out gas price using HTTP request https://maps.google.com/




        int n = GasStationList.size();
        for (int i=0;i<n;i++){
            String tmp = GasStationList.get(i).GetAddress().replace(" ","+");
            response = HttpGet(
                    "https://www.google.com/maps/place/" + tmp);
            Log.d("GoogleMaps API", String.valueOf(response.length()));
            Log.d("GoogleMaps API", String.valueOf(response.indexOf("[\\\"$")));

            //find 3 gas prices with starting key [\"$

            String debugStr ="";
            int m = response.length();
            int countTo3 = 0;
            for (int j=0;j<m-4;j++){
                if (response.charAt(j) != '[')
                    continue;
                if (response.charAt(j+1) != '\\')
                    continue;
                if (response.charAt(j+2) != '\"')
                    continue;
                if (response.charAt(j+3) != '$')
                    continue;


                int k = j+4;

                boolean validNum = false;
                StringBuilder stringBuilder = new StringBuilder();
                while ((k < m) && ((response.charAt(k) >= '0') && (response.charAt(k) <= '9'))){
                    stringBuilder.append(response.charAt(k));
                    k++;
                }

                if (k==m)
                    continue;
                if (response.charAt(k) != '.')
                    continue;
                stringBuilder.append('.');
                k++;



                if (k==m)
                    continue;
                while ((k < m) && ((response.charAt(k) >= '0') && (response.charAt(k) <= '9'))){
                    stringBuilder.append(response.charAt(k));
                    k++;
                    validNum = true;
                }



                if (validNum){
                    String finalString = stringBuilder.toString();
                    Log.d("GoogleMaps API", finalString);
                    debugStr += (" " + finalString);
                    double parsedPrice = Double.parseDouble(finalString);
                    if (countTo3 ==0){

                        GasStationList.get(i).SetRegPrice(parsedPrice);
                    } else if (countTo3 ==1) {
                        if (parsedPrice >= GasStationList.get(i).GetRegPrice())
                        GasStationList.get(i).SetMidPrice(parsedPrice);
                    } else if (countTo3 ==2){
                        if (parsedPrice >= GasStationList.get(i).GetMidPrice())
                            GasStationList.get(i).SetPremPrice(parsedPrice);
                    }

                    countTo3++;
                } else
                    continue;

                if (countTo3 ==3) { //currently support REG price only, switch to 1-2-3 to support number of prices reg-mid-prem
                    Log.d("Done pricing", debugStr);
                    break;
                }

            }


        }

        isForgingData = false;

    }

    private Coordination Zip2Loc(String zipcode){
        Coordination Loc = null;
        String response = HttpGet("https://maps.googleapis.com/maps/api/geocode/json?address=zipcode%20" + zipcode + "&key=" + Constants.API_Key);


        try {

            JSONObject rootObj = new JSONObject(response);


            //Log.d("Location value ", obj.getString("location"));


            JSONArray resultObj = rootObj.getJSONArray("results");
            JSONObject firstObjInResults = resultObj.getJSONObject(0);
            JSONObject geoObj = firstObjInResults.getJSONObject("geometry");
            JSONObject locObj = geoObj.getJSONObject("location");
            double Longitude = locObj.getDouble("lng");
            double Lattitude = locObj.getDouble("lat");
            Loc = new Coordination(Lattitude, Longitude);
        } catch (Throwable tx) {
            Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
        }
        return Loc;
    }




    private String HttpGet(String url){
        Log.d("HttpGet", url);


        try {

            HttpRequestFactory requestFactory
                    = new NetHttpTransport().createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(
                    new GenericUrl(url));


            HttpResponse response = request.execute();
            String responseString = response.parseAsString();
            Log.d("HttpGet", responseString);
            return responseString;
        }
        catch (IOException e) {
            Log.d("HttpGet","Failed to request");
            return null;
        }


    }

}


