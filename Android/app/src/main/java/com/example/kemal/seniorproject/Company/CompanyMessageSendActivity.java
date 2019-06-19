package com.example.kemal.seniorproject.Company;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kemal.seniorproject.Adapter.CompanyMessageAdapter;
import com.example.kemal.seniorproject.Adapter.MessageAdapter;
import com.example.kemal.seniorproject.Model.Message;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.KeyboardUtils;
import com.example.kemal.seniorproject.Settings.MyGlide;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.Settings.SocketIO;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyMessageSendActivity extends AppCompatActivity {

    private String companyId, myId, name, image, cName, cProfile, conversationId;
    private Socket mSocket;

    private EditText ed_message;
    private FloatingActionButton btn_message;

    private RecyclerView rec_messageList;

    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager LinearLayout;
    private CompanyMessageAdapter adapter;

    private android.widget.LinearLayout messageLinear;

    private Toolbar toolbar;
    private TextView companyName;
    CircleImageView circleImageView;

    private String COMPANY_MESSAGE_URL;
    private String OTHER_COMPANY_MESSAGE_URL;

    private String TAG = "CompanyMessageSendActivit";

    public Boolean kontrol = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_message_send);

        ed_message = findViewById(R.id.ed_message);
        btn_message = findViewById(R.id.btn_message);

        toolbar = findViewById(R.id.sendMessage_toolbar);
        circleImageView = findViewById(R.id.msgImage);

        companyId = getIntent().getStringExtra("companyId");
        cName = getIntent().getStringExtra("name");
        cProfile = getIntent().getStringExtra("image");

        companyName = findViewById(R.id.msgName);

        SessionManager.init(CompanyMessageSendActivity.this);
        COMPANY_MESSAGE_URL = ServerAdress.host + "company_message.php";
        OTHER_COMPANY_MESSAGE_URL = ServerAdress.host + "otherCompany_message.php";

        myId = SessionManager.myId;
        name = SessionManager.name + " " + SessionManager.surname;
        image = SessionManager.userImage;


        companyName.setText(cName);
        MyGlide.image(CompanyMessageSendActivity.this, cProfile, circleImageView);


        initSocket();
        initRecylerView();

        if (SessionManager.companyId.equals(companyId))
            loadMessage();
        else
            loadOtherMessage();


        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = ed_message.getText().toString();
                if (TextUtils.isEmpty(message))
                    return;
                sendMessage(message);

            }
        });


    }

    private void loadOtherMessage() {
        conversationId = getIntent().getStringExtra("conversationId");

        Map<String, String> params = new HashMap<>();
        params.put("conversationId", conversationId);
        params.put("type", "load");
        kontrol = true;

        MyVolley volley = new MyVolley();

        volley.volley(CompanyMessageSendActivity.this, OTHER_COMPANY_MESSAGE_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonObject.getJSONArray("message_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String senderId = object.getString("senderId");
                            String content = object.getString("content");
                            String date = object.getString("date");

                            String name = object.getString("name") + " " + object.getString("surname");


                            String image = ServerAdress.profile_images + object.getString("image");

                            messageList.add(new Message(senderId, image, name, content, date));


                        }
                        rec_messageList.scrollToPosition(messageList.size() - 1);
                        adapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void sendMessage(String message) {
        ed_message.setText("");


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image", image);
            jsonObject.put("senderId", myId);
            jsonObject.put("name", name);
            jsonObject.put("message", message);
            if (!SessionManager.companyId.equals(companyId))
                jsonObject.put("companyId", getIntent().getStringExtra("conversationId"));
            else
                jsonObject.put("companyId", companyId);


            mSocket.emit("COMPANY MESSAGE CHAT", jsonObject);

            messageList.add(new Message(myId, "", "", message, getDate()));
            rec_messageList.scrollToPosition(messageList.size() - 1);
            adapter.notifyDataSetChanged();

            saveMessage(myId, companyId, message);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveMessage(String myId, String companyId, String message) {

        Map<String, String> params = new HashMap<>();
        params.put("senderId", myId);
        params.put("message", message);
        params.put("companyId", companyId);
        params.put("type", "write");

        if (!SessionManager.companyId.equals(companyId)) {
            COMPANY_MESSAGE_URL = OTHER_COMPANY_MESSAGE_URL;
            params.put("conversationId", getIntent().getStringExtra("conversationId"));
        }

        MyVolley volley = new MyVolley();
        volley.volley(CompanyMessageSendActivity.this, COMPANY_MESSAGE_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

            }
        });

    }


    Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {

                        String senderId = data.getString("senderId");
                        String image = data.getString("image");
                        String name = data.getString("name");
                        String message = data.getString("message");


                        messageList.add(new Message(senderId, image, name, message, getDate()));
                        rec_messageList.scrollToPosition(messageList.size() - 1);
                        adapter.notifyDataSetChanged();


                    } catch (Exception e) {

                    }
                }
            });
        }
    };

    private void initSocket() {
        mSocket = SocketIO.getConnection(CompanyMessageSendActivity.this, myId, "listen in the foreground...");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", myId);


            if (!SessionManager.companyId.equals(companyId))
                jsonObject.put("companyId", getIntent().getStringExtra("conversationId"));
            else
                jsonObject.put("companyId", companyId);


            mSocket.emit("COMPANY MESSAGE", jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        mSocket.on("COMPANY MESSAGE CHAT", onNewMessage);
    }

    private void initRecylerView() {
        adapter = new CompanyMessageAdapter(messageList, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        rec_messageList = findViewById(R.id.message_ReyclerView);
        LinearLayout = new LinearLayoutManager(this);
        rec_messageList.setNestedScrollingEnabled(false);
        rec_messageList.setHasFixedSize(true);
        rec_messageList.setLayoutManager(LinearLayout);
        rec_messageList.setAdapter(adapter);

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                rec_messageList.scrollToPosition(messageList.size() - 1);
            }
        });

    }

    private void loadMessage() {

        Map<String, String> params = new HashMap<>();
        params.put("companyId", companyId);
        params.put("type", "load");


        MyVolley volley = new MyVolley();

        volley.volley(CompanyMessageSendActivity.this, COMPANY_MESSAGE_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonObject.getJSONArray("message_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String senderId = object.getString("senderId");
                            String userImage = ServerAdress.profile_images + object.getString("image");
                            String username = object.getString("name") + " " + object.getString("surname");
                            String message = object.getString("content");
                            String date = object.getString("date");

                            messageList.add(new Message(senderId, userImage, username, message, date));

                        }
                        rec_messageList.scrollToPosition(messageList.size() - 1);
                        adapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String millisInString = format.format(new Date());

        return millisInString;
    }
}
