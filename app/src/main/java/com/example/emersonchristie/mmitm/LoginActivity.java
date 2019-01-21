package com.example.emersonchristie.mmitm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("asd", "in oncreate");

        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestIdToken("874329084335-j278acflqbei24nqmnim9km73lu8p6kb.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // SignInButton signInButton = findViewById(R.id.loginBtn);
        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("asd", "in onclick");
                signIn();
            }
        });
    }

    private void signIn() {
        Log.d("in signIn()", "in signIn()");

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Intent intent = new Intent(this, MainActivity.class);
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(LoginActivity.this, "logged in", Toast.LENGTH_SHORT);
            // startActivity(new Intent(this, MainActivity.class));
            finish();
            startActivityForResult(intent, 1);
            //startActivity(new Intent(this, MainActivity.class));
        }

        if(user == null){
            Toast.makeText(LoginActivity.this,"NULL", Toast.LENGTH_SHORT);

        }
        //FirebaseAuth.getInstance().signOut();

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("fawg", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ONCMPLTE", "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "User signed in.", Toast.LENGTH_SHORT);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("auth fail", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT);

                            // ...
                        }
                    }


                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("in", "onactivityresult");

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                Log.d("success", "sign in done");

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                launchHome();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d("fail", "sign in failed");
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                // ...
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(LoginActivity.this,"already logged in", Toast.LENGTH_SHORT);
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        if(user == null){
            Toast.makeText(LoginActivity.this,"NULL", Toast.LENGTH_SHORT);
        }
    }


    private void launchHome() {
        // signIn();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}