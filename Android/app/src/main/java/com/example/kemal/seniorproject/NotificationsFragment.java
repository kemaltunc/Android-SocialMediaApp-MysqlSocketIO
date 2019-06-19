package com.example.kemal.seniorproject;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kemal.seniorproject.Adapter.MessageAdapter;
import com.example.kemal.seniorproject.Adapter.NotificationAdapter;
import com.example.kemal.seniorproject.Model.Message;
import com.example.kemal.seniorproject.Model.Notification;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {

    private String myId;

    private String NOTIFY_URL;


    private RecyclerView notify_list;
    private final List<Notification> notificationList = new ArrayList<>();
    private LinearLayoutManager LinearLayout;
    private NotificationAdapter adapter;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        SessionManager.init(getContext());
        myId = SessionManager.myId;
        ServerAdress.init(getActivity());
        NOTIFY_URL = ServerAdress.host + "notification.php";

        adapter = new NotificationAdapter(notificationList, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {

/*
                Intent intent = new Intent(getContext(), MessageSendActivity.class);
                intent.putExtra("userId", messageList.get(position).getOtherUserId());
                intent.putExtra("name", messageList.get(position).getName());
                intent.putExtra("image_url", messageList.get(position).getUserImage());
                startActivity(intent);*/

            }
        });

        notify_list = view.findViewById(R.id.notif_list);
        LinearLayout = new LinearLayoutManager(getContext());
        notify_list.setNestedScrollingEnabled(false);
        notify_list.setHasFixedSize(true);
        notify_list.setLayoutManager(LinearLayout);
        notify_list.setAdapter(adapter);


        Map<String, String> params = new HashMap<>();
        params.put("userId", myId);


        MyVolley volley = new MyVolley();
        volley.volley(getContext(), NOTIFY_URL, params, "NOTIFICATION", new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadNotify(jsonObject);
            }
        });


        return view;
    }

    private void loadNotify(JSONObject jsonObject) {
        try {
            Boolean success = jsonObject.getBoolean("success");
            if (success) {
                notificationList.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("notif_list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    String id = object.getString("id");
                    String content = object.getString("content");
                    String userId = object.getString("userId");
                    String date = object.getString("date");
                    String name = object.getString("name");
                    String surname = object.getString("surname");
                    String image = ServerAdress.profile_images+object.getString("image");

                    notificationList.add(new Notification(id, content, userId, date, name + " " + surname, image));

                }
                Collections.sort(notificationList, new Comparator<Notification>() {
                    @Override
                    public int compare(Notification t1, Notification t2) {
                        return t2.getDate().compareTo(t1.getDate());
                    }
                });
                adapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
