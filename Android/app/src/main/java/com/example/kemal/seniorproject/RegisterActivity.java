package com.example.kemal.seniorproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.kemal.seniorproject.Settings.GallerySettings;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.example.kemal.seniorproject.Settings.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;


    private TextView register_select_photo;
    private EditText register_name, register_surname, register_email, register_password, register_cpassword;
    private Button register_button;
    private ImageView circleImageView;


    private String REGISTER_URL;


    private Uri uri;
    private static int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;

    SessionManager sessionManager;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);


        progressDialog = new ProgressDialog(this);


        ServerAdress.init(RegisterActivity.this);
        REGISTER_URL = ServerAdress.host + "register.php";


        toolbar = findViewById(R.id.register_toolbar);
        register_name = findViewById(R.id.register_name);
        register_surname = findViewById(R.id.register_surname);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_cpassword = findViewById(R.id.register_cpassword);
        register_button = findViewById(R.id.btn_register);
        register_select_photo = findViewById(R.id.register_select_photo);
        circleImageView = findViewById(R.id.register_imageview);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = register_name.getText().toString().trim();
                String surname = register_surname.getText().toString().trim();
                String email = register_email.getText().toString().trim();
                String password = register_password.getText().toString().trim();
                String cpassword = register_cpassword.getText().toString().trim();
                String imageBitmap = getStringImage(bitmap);

                RegisterAccount(name, surname, email, password, cpassword, imageBitmap);


            }
        });

        register_select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectPhoto();
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });

        setToolbar();


    }

    private void selectPhoto() {
        try {
            if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(RegisterActivity.this);
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void RegisterAccount(final String name, final String surname, final String email, final String password, String cpassword, final String imageBitmap) {


        if (name.equals("")) {
            register_name.setError("Ad alanı boş geçilemez");
            return;
        }
        if (surname.equals("")) {
            register_surname.setError("Soyad alanı boş geçilemez");
            return;
        }
        if (email.equals("")) {
            register_email.setError("Email alanı boş geçilemez");
            return;
        }
        if (password.equals("")) {
            register_password.setError("Şifre alanı boş geçilemez");
            return;
        }
        if (cpassword.equals("")) {
            register_cpassword.setError("Şifre alanı boş geçilemez");
            return;
        }
        if (!password.equals(cpassword)) {
            register_cpassword.setError("Girilen şifreler uyuşmak zorunda");
            return;
        }
        if (imageBitmap.equals("")) {
            Toast.makeText(this, "Profil resmi seçmek zorundasınız", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setTitle("Lütfen bekleyin...");
        progressDialog.setMessage("Hesabınız oluşturuluyor...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("surname", surname);
        params.put("email", email);
        params.put("password", password);
        params.put("photo", imageBitmap);

        MyVolley myVolley = new MyVolley();
        myVolley.volley(RegisterActivity.this, REGISTER_URL, params, "RegisterActivity", new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean control = jsonObject.getBoolean("control");
                    if (control)
                        Toast.makeText(RegisterActivity.this, "Bu mail adresi zaten alınmış", Toast.LENGTH_SHORT).show();
                    else {
                        Boolean success = jsonObject.getBoolean("success");
                        if (success) {
                            int id = jsonObject.getInt("myId");
                            sessionManager.createSession(String.valueOf(id), email, name, surname, "", "", "", "");
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Kayıt olusturulurken bir hata olustu " + e.toString(), Toast.LENGTH_SHORT).show();

                }
            }
        });

        progressDialog.dismiss();


    }

    private void setToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Kayıt ol");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            uri = GallerySettings.activityResult(requestCode, resultCode, data, RegisterActivity.this, getCacheDir());


            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                circleImageView.setImageBitmap(bitmap);
                register_select_photo.setVisibility(View.INVISIBLE);
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
