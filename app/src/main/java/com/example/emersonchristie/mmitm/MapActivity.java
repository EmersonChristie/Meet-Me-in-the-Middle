package com.example.emersonchristie.mmitm;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        //try for styling map with json
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        if (mLocationPermissionsGranted) {
            sendCurAdd = getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            //make sure extras are not null
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                receiveData();
            }

        }

        //settings for map
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 10;

    //vars from NewFragment
    private AutoCompleteTextView locOne, locTwo;

    //map vars
    private Boolean mLocationPermissionsGranted = false;
    public GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    String curAddress;
    String sendCurAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getLocationPermission();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //set initial view to new trip fragment
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new NewFragment()).commit();
        }
    }

    public String getCurrAddress () {
        return curAddress;
    }
    //drop markers function
    public Marker dropMarkers(ArrayList<LatLng> L) {
        Marker mMarker = null;

        for(int i = 0; i < L.size(); i++) {
            mMarker = mMap.addMarker(new MarkerOptions().position(L.get(i)));
        }
        return mMarker;
    }

    public void clearPlaceMarkers(LatLng sender, LatLng receiver) {
        mMap.clear();
        MarkersModel senderMarker = new MarkersModel(sender, "Sender", "sender", "snippet",true);
        dropModelMarker(senderMarker);

        MarkersModel receiverMarker = new MarkersModel(receiver, "Receiver", "receiver", "snippet",true);
        dropModelMarker(receiverMarker);
    }

    //drop markers function
    public void dropModelMarker(MarkersModel mMarker) {
        Drawable mDrawable = null;
        BitmapDescriptor markerIcon = null;
        Marker temp;
        if (mMarker.markIconType == "cafe") {
            mDrawable = getResources().getDrawable(R.drawable.ic_cafe_marker);
            markerIcon = getMarkerIconFromDrawable(mDrawable); }
        else if(mMarker.markIconType == "bar") {
            mDrawable = getResources().getDrawable(R.drawable.ic_bar_marker);
            markerIcon = getMarkerIconFromDrawable(mDrawable); }
        else if(mMarker.markIconType == "lodging") {
            mDrawable = getResources().getDrawable(R.drawable.ic_hotel_marker);
            markerIcon = getMarkerIconFromDrawable(mDrawable);        }
        else if(mMarker.markIconType == "restaurant") {
            mDrawable = getResources().getDrawable(R.drawable.ic_restaurant_marker);
            markerIcon = getMarkerIconFromDrawable(mDrawable);        }
        else if(mMarker.markIconType == "sender") {
            mDrawable = getResources().getDrawable(R.drawable.ic_sender_marker);
            markerIcon = getMarkerIconFromDrawable(mDrawable);        }
        else if(mMarker.markIconType == "receiver") {
            mDrawable = getResources().getDrawable(R.drawable.ic_receiver_marker);
            markerIcon = getMarkerIconFromDrawable(mDrawable);        }
        else if(mMarker.markIconType == "midpoint") {
            mDrawable = getResources().getDrawable(R.drawable.ic_mid_marker);
            markerIcon = getMarkerIconFromDrawable(mDrawable);        }
        else {
            mDrawable = getResources().getDrawable(R.drawable.ic_cafe_marker);
            markerIcon = getMarkerIconFromDrawable(mDrawable);        }
        if(mMarker.markLatLng != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(mMarker.markLatLng)
                    .title(mMarker.markTitle)
                    .snippet(mMarker.markSnippet)
                    .icon(markerIcon));
        }

    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //onResume Method check which fragment intent is sent from
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void receiveData() {
        //RECEIVE DATA VIA INTENT
        Intent i = getIntent();

        Geocoder geocoder = new Geocoder(this);

        if(this.getIntent() != null && mMap != null)
        {
            LatLng locationOne = i.getParcelableExtra("LOC_ONE");
            LatLng locationTwo = i.getParcelableExtra("LOC_TWO");
            LatLng midLatLng;

            Marker markOne, markTwo, markMid;

            String addressOne = i.getStringExtra("ADDRESS_ONE");
            String addressTwo = i.getStringExtra("ADDRESS_TWO");
            //Toast.makeText(this, "LocOne = " + locationOne + ", LocTwo = " + locationTwo, Toast.LENGTH_LONG).show();
            if(locationOne != null && locationTwo != null) {

                Log.d(TAG, "receiveData: calling moveCamera");

                markOne = mMap.addMarker(new MarkerOptions().position(locationOne)
                        .title(addressOne));
                markTwo = mMap.addMarker(new MarkerOptions().position(locationTwo)
                        .title(addressTwo));
                

                midLatLng = getMidLatLng(locationOne, locationTwo);
                Log.d(TAG, "receiveData: setting mid latlng");
                markMid = mMap.addMarker(new MarkerOptions().position(midLatLng)
                        .title("Mid Point"));

            }
        }
    }

    //zooms camera to included latlng bounds
    public void zoomBounds (ArrayList<LatLng> L, int padding) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //use if implement more than two locations in the furture
        for (LatLng mLatLng : L) {
            builder.include(mLatLng);
        }

        LatLngBounds bounds = builder.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);
    }


    private String getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        curAddress = "";
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            Double mlatitude = currentLocation.getLatitude();
                            Double mlongitude = currentLocation.getLongitude();
                            Address currentAddress = getAddressFromLocation(MapActivity.this ,mlatitude, mlongitude);
                            String add = currentAddress.toString();
                            curAddress = add;

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
        return curAddress;
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
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

    //method that returns a latlng from address
    public Address getAddressFromLocation(Context context, Double mlat, Double mlng) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        Address location = null;

        try {
            // May throw an IOException
            address = coder.getFromLocation(mlat, mlng, 5);
            if (address == null) {
                return null;
            }

            location = address.get(0);

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return location;
    }

    // onclick listener for navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    android.support.v4.app.Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_new:
                            selectedFragment = new NewFragment();
                            break;
                        case R.id.nav_recent:
                            selectedFragment = new RecentFragment();
                            break;
                        case R.id.nav_contacts:
                            selectedFragment = new ContactsFragment();
                            break;
                        case R.id.nav_invites:
                            selectedFragment = new InvitesFragment();
                            break;

                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    // boolean to select item
                    return true;
                }
            };

}

