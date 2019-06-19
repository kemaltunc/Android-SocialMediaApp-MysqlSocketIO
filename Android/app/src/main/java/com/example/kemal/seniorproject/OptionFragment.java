package com.example.kemal.seniorproject;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kemal.seniorproject.Settings.NotificationService;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.Settings.SocketIO;


/**
 * A simple {@link Fragment} subclass.
 */
public class OptionFragment extends Fragment {


    private TextView optionExit, tv_join;

    private SessionManager sessionManager;

    public OptionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_option, container, false);

        optionExit = view.findViewById(R.id.option_exit);
        tv_join = view.findViewById(R.id.tv_join);


        sessionManager = new SessionManager(getContext());


        tv_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RequestListActivity.class);
                startActivity(intent);
            }
        });

        optionExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocketIO.disconnect();
                NotificationService.userId = "";
                getActivity().stopService(new Intent(getActivity(), NotificationService.class));
                sessionManager.logout();

            }
        });

        return view;
    }

}
