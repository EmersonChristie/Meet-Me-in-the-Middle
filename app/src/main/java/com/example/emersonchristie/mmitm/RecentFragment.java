package com.example.emersonchristie.mmitm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class RecentFragment extends Fragment {

    ArrayList<RecentTrip> recent_trips_al;
    ListView listView;
    private static RecentsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //view inflater
        View v = inflater.inflate(R.layout.fragment_recent, container, false);
        listView = (ListView) v.findViewById(R.id.recentlist);
        recent_trips_al = new ArrayList<>();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("recents");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.equals(null)){
                            Log.d("RecentFragment", "datasnapshot.equals(null) is true");
                        }else {
                            recent_trips_al = retrieveTrips((Map<String, Object>) dataSnapshot.getValue());
                            adapter = new RecentsAdapter(getActivity(), recent_trips_al);

                            listView.setAdapter(adapter);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                RecentTrip rt = recent_trips_al.get(position);

                ContactsFragment mContactsFrag = new ContactsFragment();
                Bundle data = new Bundle();


                data.putString("SELECTED_LAT", rt.getLat());
                data.putString("SELECTED_LNG", rt.getLng());
                data.putString("SELECTED_PLACE_ID", rt.getPid());
                mContactsFrag.setArguments(data);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container, mContactsFrag);
                transaction.addToBackStack(null);
                transaction.commit();


            }
        });

        return v;
    }

    private ArrayList<RecentTrip> retrieveTrips(Map<String,Object> recents) {

        ArrayList<RecentTrip> recentTrips = new ArrayList<>();


        for (Map.Entry<String, Object> entry : recents.entrySet()){

            Map mapTrip = (Map) entry.getValue();
            String trip = mapTrip.toString();
            String[] split_trip = trip.split("\\{");
            for(int i = 0; i < split_trip.length; i++){
                //System.out.println("split_trip["+i+"]:");
                //System.out.println(split_trip[i]);
                String[] inner_split = split_trip[i].split("\\}");
                ArrayList<String> trip_data = new ArrayList<String>();
                for(int j = 0; j < inner_split.length; j++){
                    //System.out.println("    inner_split["+j+"]:");
                    //System.out.println("    "+inner_split[j]);
                    String[] final_split = inner_split[j].split(", ");
                    trip_data.clear();
                    for (int k = 0; k < final_split.length; k++){
                        System.out.println("        final_split["+k+"]:");
                        System.out.println("        "+final_split[k]);
                        String[] trip_al = final_split[k].split("=");
                        if(trip_al.length>1){
                            trip_data.add(trip_al[1]);
                        }
                    }

                    if(trip_data.size() > 6) {
                        //recentTrips.add(new RecentTrip(trip_data.get(0), trip_data.get(5), trip_data.get(6), trip_data.get(4), trip_data.get(2), trip_data.get(3), trip_data.get(1)));
                        recentTrips.add(new RecentTrip(trip_data.get(0), trip_data.get(6), trip_data.get(3), trip_data.get(5), trip_data.get(2), trip_data.get(4), trip_data.get(1)));

                        trip_data.clear();
                    }

                }


            }
            //recentTrips.add(trip);
        }
        System.out.println("RECENT TRIPS BELOW THIS LINE ----------------------------------");
        System.out.println(recentTrips);
        for(int i = 0; i < recentTrips.size(); i++){
            System.out.println("Recent trip #"+i);
            RecentTrip rt = (recentTrips.get(i));
            System.out.println("date: " + rt.date);
            System.out.println("lat: " + rt.lat);
            System.out.println("lng: " + rt.lng);
            System.out.println("pid: " + rt.pid);
            System.out.println("place_name: " + rt.place_name);
            System.out.println("name: " + rt.name);
            System.out.println("number: " + rt.number);


        }
        System.out.println(recentTrips.size());
        System.out.println("RECENT TRIPS ABOVE THIS LINE ----------------------------------");

        return recentTrips;
    }

}