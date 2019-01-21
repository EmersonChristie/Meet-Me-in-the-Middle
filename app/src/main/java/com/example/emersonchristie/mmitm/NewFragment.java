package com.example.emersonchristie.mmitm;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    //interface methods
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private static final String TAG = "NewFragment";


    private PlaceAutocompleteAdapter mPlaceAutoCompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    //widgets
    private AutoCompleteTextView locOne;
    private AutoCompleteTextView locTwo;
    private Button showPlacesBtn;

    //vars
    private PlaceDetectionClient mPlaceDetectionClient;
    private GeoDataClient mGeoDataClient;
    public Bundle mBundle;
    public String curAddress;

    public LatLng latLngOne;
    public LatLng latLngTwo;

    public NewFragment() {
        //required empty constructor;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_new, container, false);

        //frag vars
        locOne = (AutoCompleteTextView) parent.findViewById(R.id.edittxt_loc_1);
        locTwo = (AutoCompleteTextView) parent.findViewById(R.id.edittxt_loc_2);
        showPlacesBtn = (Button) parent.findViewById((R.id.btn_show_places));

        //String currentAddress = ((MapActivity)getActivity()).getCurrAddress();
        locOne.setText("1156 High St, Santa Cruz, CA 95064");

        mGeoDataClient = Places.getGeoDataClient(this.getActivity());

        mPlaceAutoCompleteAdapter = new PlaceAutocompleteAdapter(this.getActivity(), mGeoDataClient,
                LAT_LNG_BOUNDS, null);

        locOne.setAdapter(mPlaceAutoCompleteAdapter);
        locTwo.setAdapter(mPlaceAutoCompleteAdapter);

        if(curAddress != null)
            locOne.setText(curAddress);
        else
            Toast.makeText(getActivity(), "curAddress null", Toast.LENGTH_SHORT).show();

        //on click listener for get meeting point button
        showPlacesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make sure both fields are filled
                //TODO Logic to make sure Entries are valid addresses
                if(!locOne.equals("") && !locTwo.equals("") && locOne != null && locTwo != null) {
                    geoLocate();
                }else{
                    Toast.makeText(getContext(), "Invalid Entry", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    //method to geolocate an address based on edit text inputs
    private void geoLocate(){

        Log.d(TAG, "geoLocate: geoLocating");

        String locationOne = locOne.getText().toString();
        String locationTwo = locTwo.getText().toString();

        Geocoder geocoder = new Geocoder(NewFragment.this.getActivity());
        List<Address> listOne = new ArrayList<>();
        List<Address> listTwo = new ArrayList<>();
        try{
            listOne = geocoder.getFromLocationName(locationOne, 1);
            listTwo = geocoder.getFromLocationName(locationTwo, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        Intent intent = new Intent(NewFragment.this.getActivity(), MapActivity.class);

        if(listOne.size() > 0) {
            Address locOneInfo = listOne.get(0);
            String addressOne = locOneInfo.getAddressLine(0);

            Double latTwo = locOneInfo.getLatitude();
            Double lngTwo = locOneInfo.getLongitude();
            latLngOne = new LatLng(latTwo,lngTwo);

            //put address in intent
            intent.putExtra("LOC_ONE", latLngOne);
            intent.putExtra("ADDRESS_ONE", addressOne);

            //locOne.setText(addressOne);
            Log.d(TAG, "geoLocate: found a location for location one: " + addressOne);
        }



        if(listTwo.size() > 0) {
            Address locTwoInfo = listTwo.get(0);

            String addressTwo = locTwoInfo.getAddressLine(0);
            Double latTwo = locTwoInfo.getLatitude();
            Double lngTwo = locTwoInfo.getLongitude();
            latLngTwo = new LatLng(latTwo,lngTwo);

            //put address in intent
            intent.putExtra("LOC_TWO", latLngTwo);
            intent.putExtra("ADDRESS_TWO", addressTwo);
            Log.d(TAG, "geoLocate: found a location for location two: " + addressTwo);
        }

        //TODO Logic to show places fragment based on locations


        //init bundle to send to places fragment
        PlacesFragment mplaceFrag = new PlacesFragment();
        Bundle data = new Bundle();
        if (latLngOne != null && latLngTwo != null) {
            LatLng midLatLng = getMidLatLng(latLngOne, latLngTwo);

            Toast.makeText(getActivity(), midLatLng.toString(), Toast.LENGTH_SHORT).show();

            Double lat = midLatLng.latitude;
            Double lng = midLatLng.longitude;
            data.putDouble("LAT", lat);
            data.putDouble("LNG", lng);
            data.putParcelable("LAT_LNG_SENDER", latLngOne);
            data.putParcelable("LAT_LNG_RECEIVER", latLngTwo);
            mplaceFrag.setArguments(data);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, mplaceFrag);
        transaction.addToBackStack(null);
        transaction.commit();

    }
    //method that returns a latlng from address
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    //method to send data from edit texts to parent activity
    private void sendData() {

        geoLocate();

        Intent intent = new Intent(NewFragment.this.getActivity(), MapActivity.class);

        //pack data
        //intent.putExtra("SENDER_KEY", "NewFragment");
        intent.putExtra("LOC_ONE", locOne.getText().toString());
        intent.putExtra("LOC_TWO", locTwo.getText().toString());

    }

    private LatLng getMidLatLng (LatLng locOne, LatLng locTwo) {
        double latOne = locOne.latitude;
        double lngOne = locOne.longitude;
        double latTwo = locTwo.latitude;
        double lngTwo = locTwo.longitude;

        double avgLat = (latOne + latTwo) / 2;
        double avgLng = (lngOne + lngTwo) / 2;

        LatLng avgLatLng = new LatLng(avgLat, avgLng);

        return avgLatLng;
    }
}

