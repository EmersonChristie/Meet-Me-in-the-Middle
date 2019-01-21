package com.example.emersonchristie.mmitm;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


import java.util.ArrayList;

public class PlacesFragment extends Fragment {

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    List<StoreModel> storeModels;
    ApiInterface apiService;

    String latLngString;
    Double lat, lng;
    LatLng midLatLng;
    LatLng senderLatLng;
    LatLng receiverLatLng;

    RecyclerView recyclerView;
    EditText editText;
    Button restaurant, bar, lodging, cafe;
    Button previousButtonPressed;
    List<PlacesPOJO.CustomA> results;
    List<MarkersModel> markersL;

    View view;

    private GeoDataClient mGeoDataClient;
    public Bitmap bitmap;

    //added this to adapt class for fragment
    private static final String TAG = "PlacesFragment";

    private ProgressBar spinner;

    public PlacesFragment() {
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

        //view inflater
        View v = inflater.inflate(R.layout.fragment_places, container, false);

        view = v;

        //init geo data client
        mGeoDataClient = Places.getGeoDataClient(getActivity());

        //start tut
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);

        }


        //Get arguments passed from NewFragment
        Bundle bundleArgs = getArguments();
        if (bundleArgs != null) {

            senderLatLng = bundleArgs.getParcelable("LAT_LNG_SENDER");
            receiverLatLng = bundleArgs.getParcelable("LAT_LNG_RECEIVER");
            lat = bundleArgs.getDouble("LAT");
            lng = bundleArgs.getDouble("LNG");
            midLatLng = new LatLng(lat, lng);
            latLngString = lat + "," + lng;
            Log.d(TAG, "onCreateView: bundle not null");
        }
        //public MarkersModel(LatLng markLatLng, String markTitle, String markIconType, String markSnippet, Boolean markVisible) {
        MarkersModel senderMarker = new MarkersModel(senderLatLng, "Sender", "sender", "snippet",true);
        ((MapActivity)getActivity()).dropModelMarker(senderMarker);

        MarkersModel receiverMarker = new MarkersModel(receiverLatLng, "Receiver", "receiver", "snippet",true);
        ((MapActivity)getActivity()).dropModelMarker(receiverMarker);

        MarkersModel midMarker = new MarkersModel(midLatLng, "Mid-Point", "midpoint", "snippet",true);
        ((MapActivity)getActivity()).dropModelMarker(midMarker);

        //drop markers on map
        ArrayList<LatLng> mLatLngs = new ArrayList<>();
        mLatLngs.add(senderLatLng);
        mLatLngs.add(receiverLatLng);
        mLatLngs.add(midLatLng);

        spinner = (ProgressBar)v.findViewById(R.id.progressBar1);

        //create camera update with bounds including loc one and loc two
        ((MapActivity)getActivity()).zoomBounds(mLatLngs, 175);

        apiService = APIClient.getClient().create(ApiInterface.class);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //editText = (EditText) v.findViewById(R.id.editText);
        restaurant = (Button) v.findViewById(R.id.btn_restaurant);
        bar = (Button) v.findViewById(R.id.btn_bar);
        lodging = (Button) v.findViewById(R.id.btn_hotel);
        cafe = (Button) v.findViewById(R.id.btn_cafe);

        fetchStores("restaurant");
        ((MapActivity)getActivity()).clearPlaceMarkers(senderLatLng, receiverLatLng);

        restaurant.performClick();
        restaurant.setSelected(true);
        //initialize previous button pressed to cafe
        previousButtonPressed = restaurant;

        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurant.setSelected(true);
                if(previousButtonPressed != restaurant) {
                    previousButtonPressed.setSelected(false);
                    previousButtonPressed = restaurant;
                }
                fetchStores("restaurant");
                ((MapActivity)getActivity()).clearPlaceMarkers(senderLatLng, receiverLatLng);
            }
        });

        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setSelected(true);
                if(previousButtonPressed != bar) {
                    previousButtonPressed.setSelected(false);
                    previousButtonPressed = bar;
                }
                fetchStores("bar");
                ((MapActivity)getActivity()).clearPlaceMarkers(senderLatLng, receiverLatLng);
            }
        });

        lodging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lodging.setSelected(true);
                if(previousButtonPressed != lodging) {
                    previousButtonPressed.setSelected(false);
                    previousButtonPressed = lodging;
                }
                fetchStores("lodging");
                ((MapActivity)getActivity()).clearPlaceMarkers(senderLatLng, receiverLatLng);
            }
        });

        cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cafe.setSelected(true);
                if(previousButtonPressed != cafe) {
                    previousButtonPressed.setSelected(false);
                    previousButtonPressed = cafe;
                }
                fetchStores("cafe");
                ((MapActivity)getActivity()).clearPlaceMarkers(senderLatLng, receiverLatLng);
            }
        });

        return v;
    }

    // TODO  logic for dropping markers
    private void setPlaceMarkers(PlacesPOJO.CustomA info, String type, String duration) {
        //public MarkersModel(LatLng markLatLng, String markTitle, String markIconType, String markSnippet, Boolean markVisible)
        Double tempLat = Double.parseDouble(info.geometry.locationA.lat);
        Double tempLng = Double.parseDouble(info.geometry.locationA.lng);
        LatLng tempLatLng = new LatLng(tempLat, tempLng);
        String snippet = "Meet in " + duration;
        MarkersModel markerModel = new MarkersModel(tempLatLng, info.name, type, snippet, true);
        //markersL.add(markerModel);

        //call dropModelMarker from MapActivity
        ((MapActivity)getActivity()).dropModelMarker(markerModel);

    }



    private void fetchStores(final String placeType) {

        spinner.setVisibility(View.VISIBLE);

        Call<PlacesPOJO.Root> call = apiService.doPlaces(placeType, latLngString, true, "distance", APIClient.GOOGLE_PLACE_API_KEY);
        call.enqueue(new Callback<PlacesPOJO.Root>() {
            @Override
            public void onResponse(Call<PlacesPOJO.Root> call, Response<PlacesPOJO.Root> response) {
                PlacesPOJO.Root root = response.body();


                if (response.isSuccessful()) {

                    if (root.status.equals("OK")) {

                        results = root.customA;
                        storeModels = new ArrayList<>();
                        for (int i = 0; i < results.size(); i++) {

                            if (i == 10)
                                break;
                            PlacesPOJO.CustomA info = results.get(i);


                            fetchDistance(info, placeType, senderLatLng);

                        }

                    } else {
                        Toast.makeText(getActivity(), "No matches found near you", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "fetchStores: No matches found near Midpoint");
                        spinner.setVisibility(View.GONE);

                        //clear recyclerview
                        storeModels.clear();
                        recyclerView.setAdapter(null);
                    }

                } else if (response.code() != 200) {
                    Toast.makeText(getActivity(), "Error " + response.code() + " found.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "fetchStores: Error" + response.code() + " found");
                    spinner.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<PlacesPOJO.Root> call, Throwable t) {
                // Log error here since request failed
                call.cancel();
                Log.d(TAG, "onFailure: call.cancel()");
            }
        });


    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                } else {
                    fetchLocation();
                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this.getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void fetchLocation() {

        SmartLocation.with(this.getActivity()).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        latLngString = location.getLatitude() + "," + location.getLongitude();
                        midLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                });
    }

    private void fetchDistance(final PlacesPOJO.CustomA info, final String type, LatLng startingLL) {

        String startingLLString = startingLL.latitude + "," + startingLL.longitude;

        Call<ResultDistanceMatrix> call = apiService.getDistance(APIClient.GOOGLE_PLACE_API_KEY, startingLLString, info.geometry.locationA.lat + "," + info.geometry.locationA.lng);
        call.enqueue(new Callback<ResultDistanceMatrix>() {
            @Override
            public void onResponse(Call<ResultDistanceMatrix> call, Response<ResultDistanceMatrix> response) {

                ResultDistanceMatrix resultDistance = response.body();
                if ("OK".equalsIgnoreCase(resultDistance.status)) {
                    Log.d("fetchDistance", "result distance matrix: ");

                    ResultDistanceMatrix.InfoDistanceMatrix infoDistanceMatrix = resultDistance.rows.get(0);
                    ResultDistanceMatrix.InfoDistanceMatrix.DistanceElement distanceElement = infoDistanceMatrix.elements.get(0);
                    if ("OK".equalsIgnoreCase(distanceElement.status)) {
                        ResultDistanceMatrix.InfoDistanceMatrix.ValueItem itemDuration = distanceElement.duration;
                        ResultDistanceMatrix.InfoDistanceMatrix.ValueItem itemDistance = distanceElement.distance;
                        String totalDistance = String.valueOf(itemDistance.text);
                        String totalDuration = String.valueOf(itemDuration.text);
                        //reference for store models params
                        //public StoreModel(String name, String address, String distance, String duration, Bitmap placePhoto, String placeID, Double rating)

                        //call set markers with info, place type, and duration
                        setPlaceMarkers(info, type, totalDuration);

                        //call get photos to set bitmap equal to info photo
                        getPhotos(info, totalDistance,totalDuration);
                        Log.d(TAG, "onResponse: place id = " + info.placeID);
                        Log.d(TAG, "onResponse: bitmap - " + bitmap);

                    }

                }

            }

            @Override
            public void onFailure(Call<ResultDistanceMatrix> call, Throwable t) {
                call.cancel();
            }
        });

    }

    //function to get photo bitmap from place api
    // Request photos and metadata for the specified place.
    private void getPhotos(final PlacesPOJO.CustomA info, final String totalDistance, final String totalDuration) {

        //final String placeId = "ChIJa147K9HX3IAR-lwiGIQv9i4";
        final String placeId = info.placeID;
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                final PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                //check for null photo
                if (photoMetadataBuffer.getCount() > 0) {
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);

                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getScaledPhoto(photoMetadata, 200, 200);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            bitmap = photo.getBitmap();

                            bitmap = convertBitMapToSquare(bitmap);

                            storeModels.add(new StoreModel(info.name, info.vicinity, totalDistance, totalDuration, bitmap, info.placeID, info.rating));


                            if (storeModels.size() == 10 || storeModels.size() == results.size()) {
                                final RecyclerViewAdapter adapterStores = new RecyclerViewAdapter(results, storeModels, new RecyclerViewAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(PlacesPOJO.CustomA info, StoreModel item) {
                                        //Pass Lat Lng and PlaceID of click item to contacts frag
                                        ContactsFragment mContactsFrag = new ContactsFragment();
                                        Bundle data = new Bundle();

                                        String selectedLat = info.geometry.locationA.lat;
                                        String selectedLng = info.geometry.locationA.lng;
                                        String selectedPlaceID = info.placeID;

                                        data.putString("SELECTED_LAT", selectedLat);
                                        data.putString("SELECTED_LNG", selectedLng);
                                        data.putString("SELECTED_PLACE_ID", selectedPlaceID);
                                        mContactsFrag.setArguments(data);
                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                        transaction.add(R.id.fragment_container, mContactsFrag);
                                        transaction.addToBackStack(null);
                                        transaction.commit();




                                        Toast.makeText(getContext(), info.name, Toast.LENGTH_LONG).show();
                                    }
                                });
                                recyclerView.setAdapter(adapterStores);
                                recyclerView.setNestedScrollingEnabled(false);

//
                            }

                            photoMetadataBuffer.release();

                            spinner.setVisibility(View.GONE);

                            Log.d(TAG, "onComplete: getPhotos bitmap = " + bitmap);

                        }
                    });
                }
            }
        });

    }

    public Bitmap convertBitMapToSquare (Bitmap srcBmp) {

        Bitmap dstBmp;

        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }

        return dstBmp;
    }

}