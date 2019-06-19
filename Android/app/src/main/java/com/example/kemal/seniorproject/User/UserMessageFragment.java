package com.example.kemal.seniorproject.User;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kemal.seniorproject.Adapter.MessageAdapter;
import com.example.kemal.seniorproject.MessageSendActivity;
import com.example.kemal.seniorproject.Model.Message;
import com.example.kemal.seniorproject.Model.Post;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.Settings.SocketIO;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.security.auth.callback.Callback;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserMessageFragment extends Fragment {


    private String myId;

    private String LAST_MESSAGE_URL;


    private RecyclerView rec_messageList;
    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager LinearLayout;
    private MessageAdapter adapter;


    private Socket mSocket;


    public UserMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_message, container, false);

        SessionManager.init(getContext());
        myId = SessionManager.myId;
        ServerAdress.init(getActivity());
        LAST_MESSAGE_URL = ServerAdress.host + "last_message.php";


        mSocket = SocketIO.getConnection(getContext(), myId, "listen in the foreground...");


        mSocket.on("PRIVATE MESSAGE", onNewMessage);

        adapter = new MessageAdapter(messageList, "lastMessage", new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {


                Intent intent = new Intent(getContext(), MessageSendActivity.class);
                intent.putExtra("userId", messageList.get(position).getOtherUserId());
                intent.putExtra("name", messageList.get(position).getName());
                intent.putExtra("image_url", messageList.get(position).getUserImage());
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
        loadMessages();


    }

    private void loadMessages() {
        Map<String, String> params = new HashMap<>();
        params.put("myId", myId);

        MyVolley volley = new MyVolley();
        volley.volley(getContext(), LAST_MESSAGE_URL, params, "LASTMESSAGE", new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        messageList.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("last_message");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String otherUserId = object.getString("toUserId");
                            String senderId = object.getString("senderId");
                            String conversationId = object.getString("conversationId");

                            String message = object.getString("message");
                            String date = object.getString("date");
                            String name = object.getString("name") + " " + object.getString("surname");
                            String image = ServerAdress.profile_images + object.getString("image_url");

                            String messageType = object.getString("messageType");


                            messageList.add(new Message(senderId, conversationId, message, date, otherUserId, name, image, messageType));

                        }
                        Collections.sort(messageList, new Comparator<Message>() {
                            @Override
                            public int compare(Message t1, Message t2) {
                                return t2.getDate().compareTo(t1.getDate());
                            }
                        });
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getContext(), "Başarısız", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() != null) {


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = (JSONObject) args[0];
                        try {
                            String senderId = data.getString("senderId");
                            String receiverId = data.getString("receiverId");
                            String conversationId = data.getString("conversationId");
                            String name = data.getString("name");
                            String image = data.getString("userImage");
                            String message = data.getString("message");
                            String date = data.getString("date");
                            String messageType = data.getString("messageType");


                            for (int i = 0; i < messageList.size(); i++) {
                                if (conversationId.equals(messageList.get(i).getConversationId())) {
                                    messageList.remove(i);
                                    break;
                                }
                            }
                            String otherUserId = null;
                            if (senderId.equals(myId))
                                otherUserId = receiverId;
                            else
                                otherUserId = senderId;

                            messageList.add(new Message(senderId, conversationId, message, date, otherUserId, name, image, messageType));


                            Collections.sort(messageList, new Comparator<Message>() {
                                @Override
                                public int compare(Message t1, Message t2) {
                                    return t2.getDate().compareTo(t1.getDate());
                                }
                            });
                            adapter.notifyDataSetChanged();


                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        messageList.clear();

    }
}
