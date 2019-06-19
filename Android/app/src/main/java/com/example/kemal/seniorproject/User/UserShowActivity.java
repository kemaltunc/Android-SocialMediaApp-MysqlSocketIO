package com.example.kemal.seniorproject.User;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
import com.example.kemal.seniorproject.MessageSendActivity;
import com.example.kemal.seniorproject.Model.User;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserShowActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private String id, image_url, name;
    private ImageView profile_show_image;

    private String USER_INFO_PROFILE;

    private TextView profile_status, following_count, post_count;
    private ImageButton profile_popup_menu, message_btn;

    Bundle bundle;

    private ProfilePostFragment profilePostFragment;

    private String TAG = "UserShowActivity";

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_show);

        tabLayout = findViewById(R.id.profile_tab_menu);

        initTab();

        toolbar = findViewById(R.id.showUserToolbar);

        profile_show_image = findViewById(R.id.profile_show_image);

        Bundle bundle = getIntent().getExtras();

        id = bundle.getString("id");

        ServerAdress.init(UserShowActivity.this);
        USER_INFO_PROFILE = ServerAdress.host + "user_info.php";

        profile_popup_menu = findViewById(R.id.profile_popup_menu);
        profile_status = findViewById(R.id.profile_status);
        following_count = findViewById(R.id.following_count);
        post_count = findViewById(R.id.post_count);
        message_btn = findViewById(R.id.message_btn);


        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("type", "typeone");
        MyVolley volley = new MyVolley();

        volley.volley(UserShowActivity.this, USER_INFO_PROFILE, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadData(jsonObject);
            }
        });
        bundle = new Bundle();
        bundle.putString("id", id);

        profilePostFragment = new ProfilePostFragment();
        profilePostFragment.setArguments(bundle);
        setFragment(profilePostFragment);

        message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserShowActivity.this, MessageSendActivity.class);
                intent.putExtra("userId", id);
                intent.putExtra("name", name);
                intent.putExtra("image_url", image_url);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        tabLayout.getTabAt(0).select();
    }

    private void loadData(JSONObject jsonObject) {
        try {
            Boolean success = jsonObject.getBoolean("success");
            if (success) {
                image_url = ServerAdress.profile_images + jsonObject.getString("image_url");

                Glide.with(UserShowActivity.this).load(image_url).into(profile_show_image);


                String status = jsonObject.getString("status");
                profile_status.setText(status);

                name = jsonObject.getString("name") + " " + jsonObject.getString("surname");
                initToolbar(name);

                int flwCount = jsonObject.getInt("followingCount");
                following_count.setText(String.valueOf(flwCount));

                int pstCount = jsonObject.getInt("postCount");
                post_count.setText(String.valueOf(pstCount));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(UserShowActivity.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.profile_fragment, fragment);
        fragmentTransaction.commit();
    }


    private void initToolbar(String name) {
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void initTab() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_info));

        int black = ContextCompat.getColor(UserShowActivity.this, R.color.black);
        int grey = ContextCompat.getColor(UserShowActivity.this, R.color.grey);

        tabLayout.getTabAt(0).getIcon().setColorFilter(black, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(grey, PorterDuff.Mode.SRC_IN);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Bundle bundle;
                int tabIconColor = ContextCompat.getColor(UserShowActivity.this, R.color.black);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                switch (tab.getPosition()) {
                    case 0:
                        bundle = new Bundle();
                        bundle.putString("id", id);
                        profilePostFragment = new ProfilePostFragment();
                        profilePostFragment.setArguments(bundle);
                        setFragment(profilePostFragment);
                        break;
                    case 1:
                        Intent intent = new Intent(UserShowActivity.this, ProfileInfoActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(UserShowActivity.this, R.color.grey);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

}
