package com.example.kemal.seniorproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.kemal.seniorproject.Adapter.MessageAdapter;
import com.example.kemal.seniorproject.Model.Message;
import com.example.kemal.seniorproject.Model.User;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.GallerySettings;
import com.example.kemal.seniorproject.Settings.KeyboardUtils;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.NotificationService;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.Settings.SocketIO;
import com.example.kemal.seniorproject.User.UserShowActivity;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageSendActivity extends AppCompatActivity {


    private Uri uri;
    private static int PICK_IMAGE_REQUEST = 1;

    private Bitmap bitmap;

    private String myId, otherId, name, image_url;
    public String conversationId;

    private String PRIVATE_MESSAGE_URL;


    private Socket mSocket;
    private EditText ed_message;
    private ImageButton msg_back, btn_file;
    private FloatingActionButton btn_message;
    private CircleImageView otherUserCircle;
    private Toolbar toolbar;

    private TextView otherUserName;

    private RecyclerView rec_messageList;

    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager LinearLayout;
    private MessageAdapter adapter;

    private LinearLayout messageLinear;


    private String TAG = "MessagesSendActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_send);


        toolbar = findViewById(R.id.sendMessage_toolbar);
        setSupportActionBar(toolbar);

        ed_message = findViewById(R.id.ed_message);
        btn_message = findViewById(R.id.btn_message);
        messageLinear = findViewById(R.id.message_linear);
        btn_file = findViewById(R.id.btn_file);


        msg_back = findViewById(R.id.msg_back);


        otherUserCircle = findViewById(R.id.msgImage);
        otherUserName = findViewById(R.id.msgName);

        ServerAdress.init(MessageSendActivity.this);
        SessionManager.init(MessageSendActivity.this);

        myId = SessionManager.myId;
        name = SessionManager.name + " " + SessionManager.surname;
        image_url = SessionManager.userImage;


        otherId = getIntent().getStringExtra("userId");
        String otherName = getIntent().getStringExtra("name");
        String otherImage = getIntent().getStringExtra("image_url");


        Glide.with(MessageSendActivity.this).load(otherImage).into(otherUserCircle);
        otherUserName.setText(otherName);


        PRIVATE_MESSAGE_URL = ServerAdress.host + "private_message.php";


        NotificationService.userId = otherId;
        mSocket = SocketIO.getConnection(this, myId, "listen in the foreground...");
        mSocket.on("PRIVATE MESSAGE", onNewMessage);


        adapter = new MessageAdapter(messageList, "private_message", new ClickListener() {
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


        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = ed_message.getText().toString();

                if (TextUtils.isEmpty(message)) {
                    return;
                }

                ed_message.setText("");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String millisInString = dateFormat.format(new Date());

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", name);
                    jsonObject.put("userImage", image_url);
                    jsonObject.put("message", message);
                    jsonObject.put("senderId", myId);
                    jsonObject.put("receiverId", otherId);
                    jsonObject.put("conversationId", conversationId);
                    jsonObject.put("date", millisInString);
                    jsonObject.put("messageType", "text");

                    mSocket.emit("PRIVATE MESSAGE", jsonObject);

                    messageList.add(new Message(myId, message, getDate(), "text"));
                    rec_messageList.scrollToPosition(messageList.size() - 1);
                    adapter.notifyDataSetChanged();
                    writeMessage(message, "text");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final BottomSheetBuilder bottomSheetBuilder = new BottomSheetBuilder(MessageSendActivity.this);
                bottomSheetBuilder.setMode(BottomSheetBuilder.MODE_LIST);
                bottomSheetBuilder.setMenu(R.menu.message_select_file);

                bottomSheetBuilder.setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.message_image_file:
                                selectPhoto();
                                break;
                            case R.id.message_video_file:


                                break;
                            case R.id.message_audio_file:


                                break;
                        }
                    }
                });
                BottomSheetMenuDialog dialog = bottomSheetBuilder.createDialog();
                dialog.show();
            }
        });

        msg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationService.userId = "";
                Intent intent = new Intent(MessageSendActivity.this, MainActivity.class);
                intent.putExtra("fragment_name", "Message");
                intent.putExtra("page", 0);
                startActivity(intent);
                finish();

            }
        });

        messageLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageSendActivity.this, UserShowActivity.class);
                intent.putExtra("id", otherId);
                startActivity(intent);
            }
        });

        loadMessages();

    }

    private void selectPhoto() {
        try {
            if (ActivityCompat.checkSelfPermission(MessageSendActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(MessageSendActivity.this);
            } else {
                ActivityCompat.requestPermissions(MessageSendActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.private_message_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            uri = GallerySettings.activityResult(requestCode, resultCode, data, MessageSendActivity.this, getCacheDir());


            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                writeMessage(getStringImage(bitmap), "image");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public String getStringImage(Bitmap bitmap) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            byte[] imageByteArray = byteArrayOutputStream.toByteArray();
            String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

            return encodedImage;
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPhoto();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(MessageSendActivity.this, MainActivity.class);
            intent.putExtra("fragment_name", "Message");
            intent.putExtra("page", 0);
            startActivity(intent);
            return true;
        } else {

            return super.onKeyDown(keyCode, event);
        }
    }

    private void writeMessage(final String message, final String messageType) {

        Map<String, String> params = new HashMap<>();
        params.put("myId", myId);
        params.put("otherId", otherId);
        params.put("conversationId", conversationId);
        params.put("message", message);
        params.put("status", "true");
        params.put("type", "write");
        params.put("messageType", messageType);

        MyVolley volley = new MyVolley();

        volley.volley(MessageSendActivity.this, PRIVATE_MESSAGE_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        String type = jsonObject.getString("messageType");

                        if (type.equals("image")) {
                            JSONObject msg = new JSONObject();
                            try {
                                Date now = new Date();
                                String date = String.valueOf(now);
                                msg.put("name", name);
                                msg.put("message", message);
                                msg.put("senderId", myId);
                                msg.put("receiverId", otherId);
                                msg.put("conversationId", conversationId);
                                msg.put("date", date);
                                msg.put("messageType", "image");
                                mSocket.emit("PRIVATE MESSAGE", jsonObject);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void loadMessages() {
        Map<String, String> params = new HashMap<>();
        params.put("myId", myId);
        params.put("otherId", otherId);
        params.put("type", "load");

        MyVolley volley = new MyVolley();

        volley.volley(MessageSendActivity.this, PRIVATE_MESSAGE_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Boolean count = jsonObject.getBoolean("count");
                        if (!count)
                            conversationId = UUID.randomUUID().toString();
                        else {
                            JSONArray jsonArray = jsonObject.getJSONArray("message_list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String senderId = object.getString("senderId");
                                String message = object.getString("message");
                                String date = object.getString("date");
                                String messageType = object.getString("messageType");
                                conversationId = object.getString("conversationId");
                                messageList.add(new Message(senderId, message, date, messageType));


                            }
                            rec_messageList.scrollToPosition(messageList.size() - 1);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                        String message = data.getString("message");
                        String date = data.getString("date");
                        String messageType = data.getString("messageType");

                        messageList.add(new Message(senderId, message, date, messageType));
                        rec_messageList.scrollToPosition(messageList.size() - 1);
                        adapter.notifyDataSetChanged();


                    } catch (Exception e) {

                    }
                }
            });
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        NotificationService.userId = "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageList.clear();
        NotificationService.userId = "";
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String millisInString = format.format(new Date());

        return millisInString;
    }
}
