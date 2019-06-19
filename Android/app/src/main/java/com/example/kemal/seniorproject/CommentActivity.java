package com.example.kemal.seniorproject;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kemal.seniorproject.Adapter.ComCommentAdapter;
import com.example.kemal.seniorproject.Company.CompanyCommentActivity;
import com.example.kemal.seniorproject.Model.ComComments;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.User.PostAddActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    private ImageButton close;
    private String postId, myId;

    private TextView share;
    private EditText ed_comment;

    private String POST_COMMENT_URL;

    private ProgressDialog progressDialog;

    private final List<ComComments> commentDetail = new ArrayList<>();
    private ComCommentAdapter adapter;
    private RecyclerView commentList;
    private LinearLayoutManager mLinearLayout;

    private String TAG = "CommentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        close = findViewById(R.id.post_close);
        share = findViewById(R.id.comment_share);
        ed_comment = findViewById(R.id.ed_comment);
        commentList = findViewById(R.id.comment_ReyclerView);

        progressDialog = new ProgressDialog(this);

        postId = getIntent().getStringExtra("postId");


        /*serverAdress = new ServerAdress(CommentActivity.this);*/
        ServerAdress.init(CommentActivity.this);
        POST_COMMENT_URL = ServerAdress.host + "post_comment_add.php";
        SessionManager.init(CommentActivity.this);
        myId = SessionManager.myId;


        adapter = new ComCommentAdapter(commentDetail, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });


        mLinearLayout = new LinearLayoutManager(CommentActivity.this);
        commentList.setNestedScrollingEnabled(false);
        commentList.setHasFixedSize(true);
        commentList.setLayoutManager(mLinearLayout);
        commentList.setAdapter(adapter);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = ed_comment.getText().toString().trim();

                InsertComment(content);
            }

        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        loadData();


    }

    private void loadData() {

        Map<String, String> params = new HashMap<>();
        params.put("postId", postId);
        params.put("type", "read");

        MyVolley volley = new MyVolley();

        volley.volley(CommentActivity.this, POST_COMMENT_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("comment_list");

                    if (success) {
                        commentDetail.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String name = object.getString("name");
                            String surname = object.getString("surname");
                            String image = ServerAdress.profile_images + object.getString("userImage");
                            String content = object.getString("content");

                            commentDetail.add(new ComComments(0, content, "12.05", image, name + " " + surname, "post"));

                        }
                        Collections.sort(commentDetail, new Comparator<ComComments>() {
                            @Override
                            public int compare(ComComments t1, ComComments t2) {
                                return t2.getDate().compareTo(t1.getDate());
                            }
                        });
                        adapter.notifyDataSetChanged();


                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });


    }

    private void InsertComment(final String content) {

        progressDialog.setTitle("Lütfen bekleyin...");
        progressDialog.setMessage("Yorum yapılıyor...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("userId", myId);
        params.put("postId", postId);
        params.put("content", content);
        params.put("type", "write");


        MyVolley volley = new MyVolley();

        volley.volley(CommentActivity.this, POST_COMMENT_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        Toast.makeText(CommentActivity.this, "Yorum başarıyla gönderildi.", Toast.LENGTH_SHORT).show();
                        ed_comment.setText("");
                        loadData();


                    } else
                        Toast.makeText(CommentActivity.this, "Yorum gönderilirken bir hata oluştu.", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        progressDialog.dismiss();


    }


}
