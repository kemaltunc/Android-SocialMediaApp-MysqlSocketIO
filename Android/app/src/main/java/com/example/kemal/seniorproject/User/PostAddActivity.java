package com.example.kemal.seniorproject.User;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.kemal.seniorproject.MainActivity;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.RegisterActivity;
import com.example.kemal.seniorproject.Settings.GallerySettings;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.Settings.SocketIO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.yavski.fabspeeddial.FabSpeedDial;

public class PostAddActivity extends AppCompatActivity {


    private String POST_ADD_URL;


    private CircleImageView circleImageView;
    private ImageView imageview;
    private TextView company_name;

    private FabSpeedDial fabSpeedDial;

    private static int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private Bitmap bitmap;

    private TextView post_share;
    private EditText post_content;
    private String myId, companyId, image_url;


    private ProgressDialog progressDialog;
    private Socket mSocket;

    private ImageButton close;


    private String TAG = "PostAddActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_add);


        progressDialog = new ProgressDialog(this);
        ServerAdress.init(PostAddActivity.this);

        POST_ADD_URL = ServerAdress.host + "post_add.php";

        SessionManager.init(PostAddActivity.this);
        image_url = SessionManager.companyImage;
        myId = SessionManager.myId;
        companyId = SessionManager.companyId;

        close = findViewById(R.id.post_close);

        post_content = findViewById(R.id.post_content);

        circleImageView = findViewById(R.id.post_circleImage);
        Glide.with(PostAddActivity.this).load(image_url).into(circleImageView);

        company_name = findViewById(R.id.company_name);
        imageview = findViewById(R.id.post_select_image);
        post_share = findViewById(R.id.post_share);
        company_name.setText(SessionManager.companyname);
        fabSpeedDial = findViewById(R.id.post_show_float);

        mSocket = SocketIO.getConnection(this, myId, "listen in the foreground...");

        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {

                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.image_add:
                        selectPhoto();
                        break;
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });

        post_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = post_content.getText().toString().trim();
                String imageBitmap = getStringImage(bitmap);

                InsertPost(content, imageBitmap);

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }

    private void InsertPost(final String content, final String imageBitmap) {


        progressDialog.setTitle("Lütfen bekleyin...");
        progressDialog.setMessage("Paylaşım yapılıyor...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("userId", myId);
        params.put("companyId", companyId);
        params.put("content", content);
        params.put("photo", imageBitmap);

        MyVolley volley = new MyVolley();

        volley.volley(PostAddActivity.this, POST_ADD_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Toast.makeText(PostAddActivity.this, "Gönderi sağlandı", Toast.LENGTH_SHORT).show();
                        String userIds = jsonObject.getString("userIds");
                        int postId = jsonObject.getInt("postId");

                        JSONObject notificationObject = new JSONObject();
                        try {
                            notificationObject.put("postId", String.valueOf(postId));
                            notificationObject.put("userIds", userIds);
                            notificationObject.put("companyName", SessionManager.companyname);
                            mSocket.emit("POST NOTIFICATION", notificationObject);
                        } catch (Exception ex) {

                        }
                        onBackPressed();
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });

    }

    private void selectPhoto() {
        try {
            if (ActivityCompat.checkSelfPermission(PostAddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(PostAddActivity.this);
            } else {
                ActivityCompat.requestPermissions(PostAddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            uri = GallerySettings.activityResult(requestCode, resultCode, data, PostAddActivity.this, getCacheDir());


            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageview.setVisibility(View.VISIBLE);
                imageview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
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


}
