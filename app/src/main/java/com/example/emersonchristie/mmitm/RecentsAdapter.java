package com.example.emersonchristie.mmitm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecentsAdapter extends ArrayAdapter<RecentTrip> {
    private ArrayList<RecentTrip> recents;
    Context context;
    public RecentsAdapter(Context context, ArrayList<RecentTrip> recents) {
        super(context, 0, recents);
        this.recents = recents;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RecentTrip rt = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recents_item, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.recipient);
        TextView place_name = (TextView) convertView.findViewById(R.id.location_name);
        TextView trip_date = (TextView) convertView.findViewById(R.id.trip_date);
        // Populate the data into the template view using the data object
        name.setText(rt.getName());
        place_name.setText(rt.getPlace_name());
        trip_date.setText(rt.getDate());
        // Return the completed view to render on screen
        return convertView;
    }
}
