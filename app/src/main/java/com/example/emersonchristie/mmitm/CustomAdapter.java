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
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<UserData> implements View.OnClickListener{

    private ArrayList<UserData> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtLocation;
        TextView txtInvited;
        TextView txtDate;
        ImageView meet_type_image;
    }

    public CustomAdapter(ArrayList<UserData> data, Context context) {
        super(context, R.layout.recents_row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        UserData dataModel=(UserData) object;

        switch (v.getId())
        {
            case R.id.row:
                Toast.makeText(getContext(), "Location: " +dataModel.getLocation(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UserData dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.recents_row_item, parent, false);
            viewHolder.txtLocation = (TextView) convertView.findViewById(R.id.location);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date);
            viewHolder.txtInvited = (TextView) convertView.findViewById(R.id.invited);
            viewHolder.meet_type_image = (ImageView) convertView.findViewById(R.id.meet_type_img);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtLocation.setText(dataModel.getLocation());
        viewHolder.txtDate.setText(dataModel.getDate());
        viewHolder.txtInvited.setText(dataModel.getInvited());
        viewHolder.meet_type_image.setOnClickListener(this);
        viewHolder.meet_type_image.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
