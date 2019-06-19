package com.example.kemal.seniorproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.Settings.TabPageAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button btn_login;
    private TextView toRegister;
    private ProgressDialog progressDialog;

    private String LOGIN_URL;

    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        btn_login = findViewById(R.id.btn_login);
        toRegister = findViewById(R.id.to_register_tv);

        sessionManager = new SessionManager(this);


        ServerAdress.init(LoginActivity.this);
        LOGIN_URL = ServerAdress.host + "login.php";

        progressDialog = new ProgressDialog(this);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                Login(email, password);
            }
        });


        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Login(final String email, final String password) {
        if (email.equals("")) {
            mEmail.setError("Mail adresi boş geçilemez");
            return;
        }
        if (password.equals("")) {
            mPassword.setError("Şifre boş geçilemez");
            return;
        }
        progressDialog.setTitle("Lütfen bekleyin...");
        progressDialog.setMessage("Hesabınız oluşturuluyor...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean control = jsonObject.getBoolean("success");
                    if (control) {

                        Integer id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name").trim();
                        String surname = jsonObject.getString("surname").trim();
                        String userImage = ServerAdress.profile_images + jsonObject.getString("userImage").trim();
                        String companyId = jsonObject.getString("companyId").trim();
                        String companyName = jsonObject.getString("companyName").trim();
                        String companyImage = jsonObject.getString("companyImage").trim();



                        if (companyId.equals("null"))
                            sessionManager.createSession(String.valueOf(id), email, name, surname, userImage, "", "", "");
                        else
                            sessionManager.createSession(String.valueOf(id), email, name, surname, userImage, companyId, companyName, companyImage);


                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);


                    } else {
                        Toast.makeText(LoginActivity.this, "Email adresi ya da şifre yanlış...", Toast.LENGTH_SHORT).show();
                    }

                } catch (
                        JSONException e)

                {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Giriş yapılırken bir hata olustu " + e.toString(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Giriş yapılırken bir hata olustu " + error.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }




}
