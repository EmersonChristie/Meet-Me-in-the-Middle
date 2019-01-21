package com.example.emersonchristie.mmitm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

import android.util.Log;
import android.view.Menu;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class MessageActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    Button sendBtn;
    EditText txtPhoneNo;
    EditText txtMessage;
    EditText txtName;
    String phoneNo;
    String message;
    String name;
    String lat;
    String lng;
    String placeID;
    String place_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        sendBtn = (Button) findViewById(R.id.btnSendSMS);
        txtPhoneNo = findViewById(R.id.editText);
        txtMessage = findViewById(R.id.editText2);
        txtName = findViewById(R.id.editText3);

        name = getIntent().getStringExtra("EXTRA_NAME");
        phoneNo = getIntent().getStringExtra("EXTRA_NUM");
        lat = getIntent().getStringExtra("EXTRA_LAT");
        lng = getIntent().getStringExtra("EXTRA_LNG");
        placeID = getIntent().getStringExtra("EXTRA_PID");
        place_name = getIntent().getStringExtra("EXTRA_PLACE_NAME");

        if(lat == "BUNDLE NULL" && lng == "BUNDLE NULL" && placeID == "BUNDLE NULL"){
            System.out.println("BUNDLE NULL, NOT WRITING TO FIREBASE");
        }else {
            writeNewRecentTrip(lat, lng, placeID, name, phoneNo, place_name);
        }

        txtName.setText(name);
        txtPhoneNo.setText(phoneNo);




        txtMessage.setText("Hello " + name + ", Meet Me In the Middle!" + "\n" +
                "https://www.google.com/maps/search/?api=1&query="+lat+","+lng+"&query_place_id="+placeID);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMSMessage();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    protected void sendSMSMessage() {
        phoneNo = txtPhoneNo.getText().toString();
        message = txtMessage.getText().toString();
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(getApplicationContext(), "Invite Sent", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enable SMS permissions", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }


    private void writeNewRecentTrip(String lat, String lng, String pid, String name, String number, String place_name){
        DatabaseReference myDatabase;
        String user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        myDatabase = FirebaseDatabase.getInstance().getReference();
        String key = FirebaseDatabase.getInstance().getReference().push().getKey();

        String username = user_email.split("@")[0];

        Date currentTime = Calendar.getInstance().getTime();

        RecentTrip rt = new RecentTrip(currentTime.toString(), lat, lng, pid, place_name, name, number);
        myDatabase.child("recents").child(username).child(key).setValue(rt);

    }
}
