package com.example.emersonchristie.mmitm;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import android.provider.ContactsContract;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class ContactsFragment extends Fragment {
    ArrayList<Contact> contacts;
    ListView listview;
    Button addBtn;
    String selectedLat;
    String selectedLng;
    String selectedPlaceID;
    String selectedPlaceName;

    public ContactsFragment(){}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contacts = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        listview = v.findViewById(R.id.clist);
        addBtn = v.findViewById(R.id.addBtn);


        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            contacts.add(new Contact(name, phoneNumber));
        }
        ContactsAdapter adapter = new ContactsAdapter(getActivity(), contacts);
        listview.setAdapter(adapter);


        phones.close();


        Bundle bundleArgs = getArguments();
        if (bundleArgs != null) {
            selectedLat = bundleArgs.getString("SELECTED_LAT");
            selectedLng = bundleArgs.getString("SELECTED_LNG");
            selectedPlaceID = bundleArgs.getString("SELECTED_PLACE_ID");
            selectedPlaceName = bundleArgs.getString("SELECTED_PLACE_NAME");
        }else{
            selectedLat = "LAT NULL";
            selectedLng = "LNG NULL";
            selectedPlaceID = "PID NULL";
            selectedPlaceName = "PLACE NAME NULL";
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contact contact = contacts.get(position);

                Intent intent = new Intent(getActivity(), MessageActivity.class);
                intent.putExtra("EXTRA_NAME", contact.getName());
                intent.putExtra("EXTRA_NUM", contact.getPhoneNum());
                intent.putExtra("EXTRA_LAT", selectedLat);
                intent.putExtra("EXTRA_LNG", selectedLng);
                intent.putExtra("EXTRA_PID", selectedPlaceID);
                intent.putExtra("EXTRA_PLACE_NAME", selectedPlaceName);

                startActivityForResult(intent, 1);
            }

        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateNew();
            }
        });
        return v;
    }

    public void navigateNew(){
        Intent intent = new Intent(getActivity(),  NewContactActivity.class);
        startActivityForResult(intent, 10001);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == NewContactActivity.RESULT_OK)) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }

}