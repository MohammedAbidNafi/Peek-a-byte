package com.hack.securechat.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hack.securechat.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompressFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compress, container, false);


        return view;
    }
}