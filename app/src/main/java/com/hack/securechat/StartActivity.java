package com.hack.securechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hack.securechat.WelcomeScreen.MyWelcomeActivity;
import com.stephentuso.welcome.WelcomeHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class StartActivity extends AppCompatActivity {

    ProgressBar progressBar;

    SignInButton signInButton;

    GoogleSignInClient googleSignInClient;

    FirebaseAuth firebaseAuth;

    FirebaseUser firebaseUser;

    WelcomeHelper welcomeHelper;


    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){
            startActivity(new Intent(StartActivity.this,MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        progressBar = findViewById(R.id.googleSignLoader);

        signInButton = findViewById(R.id.signin);

        progressBar.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("339733029651-0ialqja1bofb47spqoe8hkpfqi8ud8uk.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(StartActivity.this,googleSignInOptions);


        welcomeHelper = new WelcomeHelper(this, MyWelcomeActivity.class);
        welcomeHelper.show(savedInstanceState);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent,100);

            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==100){
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            if(signInAccountTask.isSuccessful()){
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);

                    if(googleSignInAccount != null){
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){

                                        progressBar.setVisibility(View.VISIBLE);


                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        assert firebaseUser != null;
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                                        String userid = firebaseUser.getUid();
                                        String username = firebaseUser.getDisplayName();
                                        String imageurl;

                                        Calendar calendar = Calendar.getInstance();
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
                                        String timestamp = simpleDateFormat.format(calendar.getTime());

                                        Uri imageuri = firebaseUser.getPhotoUrl();
                                        assert imageuri != null;
                                        imageurl = imageuri.toString();
                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                if(!snapshot.exists()){
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("id", userid);
                                                    hashMap.put("username", username);
                                                    hashMap.put("imageURL",imageurl);
                                                    hashMap.put("joined_on",timestamp);
                                                    reference.updateChildren(hashMap).addOnCompleteListener(task1 -> startActivity(new Intent(StartActivity.this, SetupActivity.class)));

                                                }else {
                                                    startActivity(new Intent(StartActivity.this,SetupActivity.class));
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}