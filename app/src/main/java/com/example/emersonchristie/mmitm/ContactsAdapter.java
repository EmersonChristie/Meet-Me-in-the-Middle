package com.example.emersonchristie.mmitm;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactsAdapter extends ArrayAdapter<Contact> {
    private ArrayList<Contact> contacts;
    Context context;
    private int lastPosition = -1;

    public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
        this.contacts = contacts;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Contact contact = getItem(position);
        final View result;

        // Check if an existing view is being reused, otherwise inflate the view
        //  if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.recents_row_item, parent, false);
        result = convertView;
        //     }
        // Lookup view for data population
        TextView name = convertView.findViewById(R.id.location);
        TextView num = convertView.findViewById(R.id.invited);
        ImageView photo = convertView.findViewById(R.id.meet_type_img);
        // Populate the data into the template view using the data object
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        name.setText(contact.getName());
        num.setText(contact.getPhoneNum());
        // Return the completed view to render on screen
        return convertView;
    }
}