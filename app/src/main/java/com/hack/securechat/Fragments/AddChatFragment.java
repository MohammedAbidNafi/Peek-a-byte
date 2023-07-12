package com.hack.securechat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.hack.securechat.Model.User;
import com.hack.securechat.R;

import java.util.ArrayList;
import java.util.List;

public class AddChatFragment extends Fragment {


    RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_add_chat, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);

        mUsers = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));



        readUsers();





        return view;
    }

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    mUsers.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        User user = snapshot1.getValue(User.class);


                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }

                    }

                    userAdapter = new UserAdapter(getContext(), mUsers,  getActivity());
                    recyclerView.setAdapter(userAdapter);

                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}