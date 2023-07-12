package com.hack.securechat;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class CodeAndDecodeFragment extends Fragment {


    CardView lock,unlock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_code_and_decode, container, false);


        lock = view.findViewById(R.id.code_card);
        unlock = view.findViewById(R.id.decode_card);



        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),DecodeActivity.class));
                //Toast.makeText(getContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}