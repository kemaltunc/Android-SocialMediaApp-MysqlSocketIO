package com.example.kemal.seniorproject.Company;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kemal.seniorproject.Adapter.ComCommentAdapter;
import com.example.kemal.seniorproject.Adapter.PostAdapter;
import com.example.kemal.seniorproject.Model.ComComments;
import com.example.kemal.seniorproject.Model.Company;
import com.example.kemal.seniorproject.Model.Post;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.User.PostAddActivity;
import com.example.kemal.seniorproject.User.ProfileInfoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CompanyCommentActivity extends AppCompatActivity {

    private ImageButton closeBtn;
    private RatingBar ratingBar, ratingBarAlert;
    private TextView write_btn, comment_count;
    private EditText company_comment_ed;

    private final List<ComComments> commentDetail = new ArrayList<>();
    private ComCommentAdapter adapter;
    private RecyclerView commentList;
    private LinearLayoutManager mLinearLayout;

    private String COMPANY_COMMENT_URL, myId, companyId;

    private ProgressDialog progressDialog;

    private String TAG = "CompanyCommentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_comment);

        /* serverAdress = new ServerAdress(CompanyCommentActivity.this);*/
        ServerAdress.init(CompanyCommentActivity.this);
        COMPANY_COMMENT_URL = ServerAdress.host + "company_comment_add.php";

        SessionManager.init(CompanyCommentActivity.this);
        myId = SessionManager.myId;
        companyId = getIntent().getStringExtra("id");
        progressDialog = new ProgressDialog(CompanyCommentActivity.this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        closeBtn = findViewById(R.id.company_comment_close_btn);
        ratingBar = findViewById(R.id.ratingBar);
        write_btn = findViewById(R.id.write_btn);
        commentList = findViewById(R.id.com_comment_list);
        comment_count = findViewById(R.id.t2);


        adapter = new ComCommentAdapter(commentDetail, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });


        mLinearLayout = new LinearLayoutManager(CompanyCommentActivity.this);
        commentList.setNestedScrollingEnabled(false);
        commentList.setHasFixedSize(true);
        commentList.setLayoutManager(mLinearLayout);
        commentList.setAdapter(adapter);


        write_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeComment();

            }
        });

        loadComments();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

    }

    private void writeComment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CompanyCommentActivity.this);
        builder.setTitle("");
        builder.setMessage("");
        builder.setCancelable(false);


        View open_dialog = View.inflate(CompanyCommentActivity.this, R.layout.write_comment_layout, null);
        ratingBarAlert = open_dialog.findViewById(R.id.ratingBarAlert);
        company_comment_ed = open_dialog.findViewById(R.id.company_comment_ed);


        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final String point = Float.toString(ratingBarAlert.getRating());
                final String content = company_comment_ed.getText().toString().trim();


                Map<String, String> params = new HashMap<>();
                params.put("userId", myId);
                params.put("companyId", companyId);
                params.put("point", point);
                params.put("content", content);
                params.put("type", "write");

                MyVolley volley = new MyVolley();
                volley.volley(CompanyCommentActivity.this, COMPANY_COMMENT_URL, params, TAG, new MyVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        try {
                            Boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                loadComments();
                                Toast.makeText(CompanyCommentActivity.this, "Yorum başarıyla gönderildi", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(CompanyCommentActivity.this, "Yorum gönderilemedi", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CompanyCommentActivity.this, "Yorum gönderilemedi " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setView(open_dialog);
        AlertDialog ad = builder.create();
        ad.show();
    }

    private void loadComments() {


        Map<String, String> params = new HashMap<>();
        params.put("userId", myId);
        params.put("companyId", companyId);
        params.put("type", "read");

        MyVolley volley = new MyVolley();
        volley.volley(CompanyCommentActivity.this, COMPANY_COMMENT_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {

                    Boolean success = jsonObject.getBoolean("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("comment_list");
                    comment_count.setText(String.valueOf(jsonArray.length()) + " Yorum");
                    if (success) {

                        double top = 0.0;
                        commentDetail.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String name = object.getString("name");
                            String surname = object.getString("surname");
                            String image = ServerAdress.profile_images + object.getString("userImage");
                            String content = object.getString("content");
                            Double rating = object.getDouble("rating");

                            top += rating;
                            commentDetail.add(new ComComments(0, content, new Float(rating), "12.05", image, name + " " + surname, "company"));

                        }

                        ratingBar.setRating((float) top / jsonArray.length());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CompanyCommentActivity.this, "Hata", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });

    }

}
