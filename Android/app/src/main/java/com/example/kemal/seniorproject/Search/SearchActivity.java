package com.example.kemal.seniorproject.Search;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.EventBus;
import com.example.kemal.seniorproject.Settings.TabPageAdapter;
import com.example.kemal.seniorproject.User.ProfileFragment;

import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ImageButton search_back;

    private EditText ed_search;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.account_viewpager);
        ed_search = findViewById(R.id.ed_search);


        search_back = findViewById(R.id.search_back);
        toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);


        search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        setTabMenu();

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    EventBus.getBus().post(new Search(editable.toString()));

                }
            }
        });
    }

    private void setTabMenu() {
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(viewPager);

        TabPageAdapter adapter = new TabPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new SearchProfileFragment(), "Kullanıcı ");
        adapter.addFragment(new SearchCompanyFragment(), "Şirket ");
        viewPager.setAdapter(adapter);


    }
}
