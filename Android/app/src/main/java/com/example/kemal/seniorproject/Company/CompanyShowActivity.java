package com.example.kemal.seniorproject.Company;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
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
import com.cocosw.bottomsheet.BottomSheet;
import com.example.kemal.seniorproject.Model.Company;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.User.PostAddActivity;
import com.example.kemal.seniorproject.User.UserShowActivity;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CompanyShowActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String companyId, myId;

    private ImageView company_image;

    private String COMPANY_INFO_PROFILE, follow_text, COMPANY_FOLLOW, COMPANY_JOIN_URL;
    private TextView company_status, following_count, post_count, company_menu_info;


    private ImageButton followbtn, company_menu_start_btn, company_popup_menu;
    Boolean followControl;

    private CompanyPostFragment companyPostFragment;
    private CompanyFollowerFragment companyFollowerFragment;

    Bundle bundle;
    private TabLayout tabLayout;

    private String TAG = "CompanyShowActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_show);


        toolbar = findViewById(R.id.showUserToolbar);
        tabLayout = findViewById(R.id.company_tab_menu);
        initTab();

        Bundle bundle = getIntent().getExtras();

        companyId = bundle.getString("id");

        SessionManager.init(CompanyShowActivity.this);
        myId = SessionManager.myId;


        ServerAdress.init(CompanyShowActivity.this);
        COMPANY_INFO_PROFILE = ServerAdress.host + "company_info.php";
        COMPANY_FOLLOW = ServerAdress.host + "company_follow.php";
        COMPANY_JOIN_URL = ServerAdress.host + "company_join.php";

        following_count = findViewById(R.id.following_count);
        post_count = findViewById(R.id.post_count);
        followbtn = findViewById(R.id.followbtn);
        company_status = findViewById(R.id.company_status);
        company_image = findViewById(R.id.profile_show_image);

        company_popup_menu = findViewById(R.id.company_popup_menu);


        Map<String, String> params = new HashMap<>();

        params.put("id", companyId);
        params.put("myId", myId);
        params.put("type", "typeone");

        MyVolley volley = new MyVolley();

        volley.volley(CompanyShowActivity.this, COMPANY_INFO_PROFILE, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadData(jsonObject);
            }
        });

        followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (followControl) {
                    follow_text = "true";
                    followControl = !followControl;
                    followbtn.setImageResource(R.drawable.ic_unfollow);

                } else {
                    follow_text = "false";
                    followControl = !followControl;
                    followbtn.setImageResource(R.drawable.ic_follow);
                }

                Map<String, String> params = new HashMap<>();
                params.put("id", companyId);
                params.put("myId", myId);
                params.put("type", follow_text);


                MyVolley volley = new MyVolley();

                volley.volley(CompanyShowActivity.this, COMPANY_FOLLOW, params, TAG, new MyVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {

                    }
                });
            }
        });


        following_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                companyFollowerFragment = new CompanyFollowerFragment();


                Bundle bundle = new Bundle();
                bundle.putString("companyId", companyId);
                bundle.putString("type", "typethree");
                companyFollowerFragment.setArguments(bundle);
                setFragment(companyFollowerFragment);
            }
        });


        companyPostFragment = new CompanyPostFragment();


        bundle = new Bundle();
        bundle.putString("companyId", companyId);
        companyPostFragment.setArguments(bundle);
        setFragment(companyPostFragment);

        company_popup_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomMenu();
            }
        });

    }


    private void loadData(JSONObject jsonObject) {

        try {
            Boolean success = jsonObject.getBoolean("success");

            if (success) {

                String image_url = ServerAdress.company_images + jsonObject.getString("image_url");
                String name = jsonObject.getString("name");
                String status = jsonObject.getString("status");
                int flwCount = jsonObject.getInt("followingCount");
                int pstCount = jsonObject.getInt("postCount");
                Glide.with(CompanyShowActivity.this).load(image_url).into(company_image);


                following_count.setText(String.valueOf(flwCount));
                post_count.setText(String.valueOf(pstCount));
                company_status.setText(status);


                followControl = jsonObject.getBoolean("followControl");

                if (followControl)
                    followbtn.setImageResource(R.drawable.ic_follow);
                else
                    followbtn.setImageResource(R.drawable.ic_unfollow);


                toolbar.setTitle(name);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        tabLayout.getTabAt(0).select();
    }

    private void openBottomMenu() {
        SessionManager.init(CompanyShowActivity.this);
        String myCompanyId = SessionManager.companyId;
        if (myCompanyId.equals("")) {
            new BottomSheet.Builder(CompanyShowActivity.this).sheet(R.menu.company_popup_menu).listener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case R.id.company_join:
                            join_company();
                            break;
                    }
                }
            }).show();
        }
    }

    private void join_company() {

        Map<String, String> params = new HashMap<>();
        params.put("userId", myId);
        params.put("companyId", companyId);

        MyVolley volley = new MyVolley();

        volley.volley(CompanyShowActivity.this, COMPANY_JOIN_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        String message = jsonObject.getString("message");

                        Toast.makeText(CompanyShowActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void initTab() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_group));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_comment));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_info));

        int black = ContextCompat.getColor(CompanyShowActivity.this, R.color.black);
        int grey = ContextCompat.getColor(CompanyShowActivity.this, R.color.grey);

        tabLayout.getTabAt(0).getIcon().setColorFilter(black, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(grey, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(grey, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(grey, PorterDuff.Mode.SRC_IN);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Bundle bundle;
                int tabIconColor = ContextCompat.getColor(CompanyShowActivity.this, R.color.black);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                switch (tab.getPosition()) {
                    case 0:
                        bundle = new Bundle();
                        bundle.putString("companyId", companyId);
                        companyPostFragment.setArguments(bundle);
                        setFragment(companyPostFragment);
                        break;
                    case 1:
                        companyFollowerFragment = new CompanyFollowerFragment();
                        bundle = new Bundle();
                        bundle.putString("companyId", companyId);
                        bundle.putString("type", "typefour");
                        companyFollowerFragment.setArguments(bundle);
                        setFragment(companyFollowerFragment);
                        break;
                    case 2:
                        Intent intent = new Intent(CompanyShowActivity.this, CompanyCommentActivity.class);
                        intent.putExtra("id", companyId);
                        startActivity(intent);

                        break;
                    case 3:
                        Intent info = new Intent(CompanyShowActivity.this, CompanyInfoActivity.class);
                        info.putExtra("id", companyId);
                        startActivity(info);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(CompanyShowActivity.this, R.color.grey);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.company_fragment, fragment);
        fragmentTransaction.commit();
    }
}
