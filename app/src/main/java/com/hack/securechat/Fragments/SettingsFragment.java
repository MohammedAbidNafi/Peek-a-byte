package com.hack.securechat.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hack.securechat.Adapter.UserAdapter;
import com.hack.securechat.MainActivity;
import com.hack.securechat.Model.User;
import com.hack.securechat.R;
import com.hack.securechat.StartActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {


    CardView logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        logout = view.findViewById(R.id.Logout_card);


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });



        return view;
    }


}