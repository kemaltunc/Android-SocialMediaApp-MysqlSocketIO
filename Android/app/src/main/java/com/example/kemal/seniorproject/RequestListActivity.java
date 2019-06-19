package com.example.kemal.seniorproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.example.kemal.seniorproject.Adapter.PostAdapter;
import com.example.kemal.seniorproject.Adapter.RequestAdapter;
import com.example.kemal.seniorproject.Model.User;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestListActivity extends AppCompatActivity {
    private ImageButton close;
    private RecyclerView requestList;
    private LinearLayoutManager mLinearLayout;
    private RequestAdapter adapter;
    private String companyId;

    private String REQUEST_URL;
    private String TAG = "RequestListActivity";

    private final List<User> userDetail = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        close = findViewById(R.id.close_btn);
        requestList = findViewById(R.id.request_list);

        SessionManager.init(RequestListActivity.this);
        companyId = SessionManager.companyId;

        REQUEST_URL = ServerAdress.host + "company_request_list.php";

        adapter = new RequestAdapter(userDetail, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        mLinearLayout = new LinearLayoutManager(RequestListActivity.this);
        requestList.setNestedScrollingEnabled(false);
        requestList.setHasFixedSize(true);
        requestList.setLayoutManager(mLinearLayout);
        requestList.setAdapter(adapter);


        Map<String, String> params = new HashMap<>();
        params.put("companyId", companyId);

        MyVolley volley = new MyVolley();
        volley.volley(RequestListActivity.this, REQUEST_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadData(jsonObject);
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadData(JSONObject jsonObject) {

        try {
            Boolean success = jsonObject.getBoolean("success");
            if (success) {
                userDetail.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("user_list");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    String joinId = object.getString("joinId");
                    String userId = object.getString("userId");
                    String name = object.getString("name");
                    String surname = object.getString("surname");
                    String image = ServerAdress.profile_images + object.getString("image");

                    userDetail.add(new User(joinId, userId, name, surname, image, companyId));

                }

                adapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
