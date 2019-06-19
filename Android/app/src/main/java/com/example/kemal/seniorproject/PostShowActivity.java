package com.example.kemal.seniorproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kemal.seniorproject.Model.Post;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PostShowActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String postId, myId;


    private String SINGLE_POST_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_show);

        postId = getIntent().getStringExtra("postId");
        SessionManager.init(PostShowActivity.this);
        myId = SessionManager.myId;

        toolbar = findViewById(R.id.singlePostToolbar);
        toolbar.setTitle("Geri dön");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ServerAdress.init(PostShowActivity.this);
        SINGLE_POST_URL = ServerAdress.host + "single_post.php";


        Toast.makeText(this, postId, Toast.LENGTH_SHORT).show();

        loadData(postId, myId);


    }

    private void loadData(final String postId, final String myId) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, SINGLE_POST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");

                    if (success) {


                        Toast.makeText(PostShowActivity.this, "Başarılı", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(PostShowActivity.this, "Bir hata meydana geldi", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PostShowActivity.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PostShowActivity.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", myId);
                params.put("postId", postId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
