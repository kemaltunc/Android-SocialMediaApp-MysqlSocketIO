package com.example.kemal.seniorproject.Company;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.MyGlide;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.User.ProfileInfoActivity;
import com.example.kemal.seniorproject.User.ProfilePostFragment;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyFragment extends Fragment {
    private TextView company_status, following_count, post_count;
    private ImageView company_image;
    private ImageButton company_popup_menu;

    private String companyId, image_url, company_name, myId;
    SessionManager sessionManager;
    private String COMPANY_INFO_URL;


    private CompanyPostFragment companyPostFragment;
    private CompanyFollowerFragment companyFollowerFragment;

    Bundle bundle;

    private String TAG = "CompanyFragment";

    private TabLayout tabLayout;

    public CompanyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_company, container, false);

        ServerAdress.init(getActivity());
        COMPANY_INFO_URL = ServerAdress.host + "company_info.php";


        tabLayout = view.findViewById(R.id.company_tab_menu);
        initTab();
        company_status = view.findViewById(R.id.company_status);
        following_count = view.findViewById(R.id.following_count);
        post_count = view.findViewById(R.id.post_count);
        company_image = view.findViewById(R.id.company_fragment_image);
        company_popup_menu = view.findViewById(R.id.company_popup_menu);
        sessionManager = new SessionManager(getContext());

        SessionManager.init(getActivity());
        companyId = SessionManager.companyId;
        myId = SessionManager.myId;

        companyPostFragment = new CompanyPostFragment();


        bundle = new Bundle();
        bundle.putString("companyId", companyId);
        companyPostFragment.setArguments(bundle);
        setFragment(companyPostFragment);

        Map<String, String> params = new HashMap<>();
        params.put("id", companyId);
        params.put("myId", myId);
        params.put("type", "typeone");

        MyVolley volley = new MyVolley();
        volley.volley(getContext(), COMPANY_INFO_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadData(jsonObject);
            }
        });


        following_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                companyFollowerFragment = new CompanyFollowerFragment();
                bundle = new Bundle();
                bundle.putString("companyId", companyId);
                bundle.putString("type", "typethree");
                companyFollowerFragment.setArguments(bundle);
                setFragment(companyFollowerFragment);
            }
        });

        return view;
    }


    private void loadData(JSONObject jsonObject) {
        try {
            Boolean success = jsonObject.getBoolean("success");
            if (success) {

                int id = jsonObject.getInt("id");
                company_name = jsonObject.getString("name");
                image_url = ServerAdress.company_images + jsonObject.getString("image_url");
                MyGlide.image(getContext(), image_url, company_image);
                sessionManager.addCompany(String.valueOf(id), company_name, image_url);


                post_count.setText(jsonObject.getString("postCount"));
                following_count.setText(jsonObject.getString("followingCount"));
                company_status.setText(jsonObject.getString("status"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error " + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void setFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.company_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        tabLayout.getTabAt(0).select();
    }

    private void initTab() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_group));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_comment));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_info));

        int black = ContextCompat.getColor(getContext(), R.color.black);
        int grey = ContextCompat.getColor(getContext(), R.color.grey);

        tabLayout.getTabAt(0).getIcon().setColorFilter(black, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(grey, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(grey, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(grey, PorterDuff.Mode.SRC_IN);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Bundle bundle;
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.black);
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
                        Intent intent = new Intent(getContext(), CompanyCommentActivity.class);
                        intent.putExtra("id", companyId);
                        startActivity(intent);

                        break;
                    case 3:
                        Intent info = new Intent(getContext(), CompanyInfoActivity.class);
                        info.putExtra("id", companyId);
                        startActivity(info);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.grey);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
