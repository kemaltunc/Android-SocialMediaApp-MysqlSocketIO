package com.example.kemal.seniorproject.User;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kemal.seniorproject.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileWarningFragment extends Fragment {


    public ProfileWarningFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_warning, container, false);
        return view;
    }

}
