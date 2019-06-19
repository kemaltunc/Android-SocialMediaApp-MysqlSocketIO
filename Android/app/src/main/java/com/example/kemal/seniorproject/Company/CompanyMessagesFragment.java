package com.example.kemal.seniorproject.Company;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.example.kemal.seniorproject.Adapter.CompanyLastMessageAdapter;
import com.example.kemal.seniorproject.Adapter.MessageAdapter;
import com.example.kemal.seniorproject.MessageSendActivity;
import com.example.kemal.seniorproject.Model.Message;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyGlide;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyMessagesFragment extends Fragment {


    private String companyId, myCompanyImage;
    private String COMPANY_MESSAGE_URL;

    private CircleImageView lastMessageImage;
    private TextView lastMessageName, messageSenderName, messageContent, message_date;

    private String TAG = "CompanyMessagesFragment";

    private RelativeLayout r1;

    private RecyclerView rec_messageList;
    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager LinearLayout;
    private CompanyLastMessageAdapter adapter;

    public CompanyMessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_company_messages, container, false);


        SessionManager.init(getActivity());
        companyId = SessionManager.companyId;
        myCompanyImage = SessionManager.companyImage;

        lastMessageImage = view.findViewById(R.id.LastMessageImage);
        lastMessageName = view.findViewById(R.id.lastMessageName);
        messageSenderName = view.findViewById(R.id.message_senderName);
        messageContent = view.findViewById(R.id.message_content);
        message_date = view.findViewById(R.id.message_date);
        r1 = view.findViewById(R.id.r1);

        MyGlide.image(getContext(), myCompanyImage, lastMessageImage);
        lastMessageName.setText(SessionManager.companyname);

        ServerAdress.init(getActivity());
        COMPANY_MESSAGE_URL = ServerAdress.host + "last_company_message.php";

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CompanyMessageSendActivity.class);
                intent.putExtra("companyId", companyId);
                intent.putExtra("name", SessionManager.companyname);
                intent.putExtra("image", SessionManager.companyImage);
                startActivity(intent);
            }
        });

        adapter = new CompanyLastMessageAdapter(messageList, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getContext(), CompanyMessageSendActivity.class);
                intent.putExtra("companyId", messageList.get(position).getCompanyId());
                intent.putExtra("conversationId", messageList.get(position).getConversationId());
                intent.putExtra("name", messageList.get(position).getName());
                intent.putExtra("image", messageList.get(position).getUserImage());
                startActivity(intent);
            }
        });

        rec_messageList = view.findViewById(R.id.messageLastList);
        LinearLayout = new LinearLayoutManager(getContext());
        rec_messageList.setNestedScrollingEnabled(false);
        rec_messageList.setHasFixedSize(true);
        rec_messageList.setLayoutManager(LinearLayout);
        rec_messageList.setAdapter(adapter);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Map<String, String> params = new HashMap<>();
        params.put("companyId", companyId);
        params.put("type", "MyCompany");

        MyVolley volley = new MyVolley();

        volley.volley(getContext(), COMPANY_MESSAGE_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadCompanyMessages(jsonObject);
            }
        });


    }

    private void loadLastMessage(JSONObject jsonObject) {

        try {
            Boolean success = jsonObject.getBoolean("success");
            if (success) {
                messageList.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("last_message");
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);
                    String username = object.getString("username");
                    String companyId = object.getString("companyId");
                    String conversationId = object.getString("conversationId");
                    String message = object.getString("content");
                    String date = object.getString("date");


                    String companyName = object.getString("companyname");
                    String image = ServerAdress.company_images + object.getString("image");

                    messageList.add(new Message(companyId, conversationId, username, image, companyName, message, date));


                }
                Collections.sort(messageList, new Comparator<Message>() {
                    @Override
                    public int compare(Message t1, Message t2) {
                        return t2.getDate().compareTo(t1.getDate());
                    }
                });
                adapter.notifyDataSetChanged();

            }
        } catch (Exception ex) {
        }
    }


    private void loadCompanyMessages(JSONObject jsonObject) {

        try {
            Boolean success = jsonObject.getBoolean("success");

            if (success) {

                messageSenderName.setText(jsonObject.getString("name") + " " + jsonObject.getString("surname") + ":");
                messageContent.setText(jsonObject.getString("content"));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<>();
        params.put("companyId", companyId);
        params.put("type", "OtherCompany");
        MyVolley volley = new MyVolley();

        volley.volley(getContext(), COMPANY_MESSAGE_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadLastMessage(jsonObject);
            }
        });
    }
}
