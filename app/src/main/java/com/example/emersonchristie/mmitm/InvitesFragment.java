package com.example.emersonchristie.mmitm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class InvitesFragment extends Fragment {

    ArrayList<UserData> userData;
    ListView listView;
    private static SettingsAdapter adapter;
    GoogleSignInClient mGoogleSignInClient;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_invites, container, false);

        listView = (ListView) v.findViewById(R.id.inviteList);

        userData = new ArrayList<>();

        userData.add(new UserData("Sign Out", "", "", ""));


        adapter = new SettingsAdapter(userData, getActivity());

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                UserData user = userData.get(position);

                Toast.makeText(getContext(), user.getLocation() + "\n" + user.getInvited() + "\n" + user.getDate(), Toast.LENGTH_LONG).show();
                signOut();


            }
        });


        return v;

    }
    private void signOut(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("874329084335-j278acflqbei24nqmnim9km73lu8p6kb.apps.googleusercontent.com")
                .requestEmail()
                .build();
        Log.d("SIGNOUT","In Signout");

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mGoogleSignInClient.signOut();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, 1);
        Log.d("SIGNOUT","In Signout");
    }
}
