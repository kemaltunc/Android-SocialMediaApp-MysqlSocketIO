package com.example.kemal.seniorproject;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kemal.seniorproject.Adapter.AdviceAdapter;
import com.example.kemal.seniorproject.Adapter.PostAdapter;
import com.example.kemal.seniorproject.Model.Company;
import com.example.kemal.seniorproject.Model.Post;
import com.example.kemal.seniorproject.Search.SearchActivity;
import com.example.kemal.seniorproject.Settings.ClickListener;

import com.example.kemal.seniorproject.Settings.MyVolley;

import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.google.android.gms.common.util.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private final List<Post> postDetail = new ArrayList<>();

    private PostAdapter adapter;
    private AdviceAdapter adapter2;
    private RecyclerView PostList, advice_rec;
    private LinearLayoutManager mLinearLayout, mLinearLayout2;

    private String POST_LIST_URL, ADVICE_COMPANY_URL;

    private String myId;


    private String TAG = "HomeFragment";

    MyVolley volley;


    private SessionManager sessionManager;
    private final List<Company> companyDetail = new ArrayList<>();

    private TextView ed_search;
    private ImageButton select_filter;
    private String filters;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(getContext());
        adapter = new PostAdapter(postDetail, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });
        adapter2 = new AdviceAdapter(companyDetail, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        ed_search = view.findViewById(R.id.ed_search);
        select_filter = view.findViewById(R.id.select_filter);

        PostList = view.findViewById(R.id.recycler_view);
        advice_rec = view.findViewById(R.id.advice_rec);


        mLinearLayout = new LinearLayoutManager(getContext());
        PostList.setNestedScrollingEnabled(false);
        PostList.setHasFixedSize(true);
        PostList.setLayoutManager(mLinearLayout);
        PostList.setAdapter(adapter);

        mLinearLayout2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        advice_rec.setNestedScrollingEnabled(false);
        advice_rec.setHasFixedSize(true);
        advice_rec.setLayoutManager(mLinearLayout2);
        advice_rec.setAdapter(adapter2);


        SessionManager.init(getActivity());
        myId = SessionManager.myId;

        ServerAdress.init(getActivity());
        POST_LIST_URL = ServerAdress.host + "home_post_list.php";
        ADVICE_COMPANY_URL = ServerAdress.host + "company_list.php";

        loadData();
        ed_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        select_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFilters();
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        Map<String, String> params = new HashMap<>();
        params.put("id", myId);

        volley = new MyVolley();

        volley.volley(getContext(), POST_LIST_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadPost(jsonObject);
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        volley.cancel(TAG);
    }

    private void loadPost(JSONObject jsonObject) {

        try {
            Boolean success = jsonObject.getBoolean("success");
            JSONArray jsonArray = jsonObject.getJSONArray("post_list");

            if (success) {
                postDetail.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String user_image = ServerAdress.profile_images + object.getString("userImage");
                    String company_image = ServerAdress.company_images + object.getString("companyImage");
                    String image_url = ServerAdress.post_images + object.getString("image_url");

                    String company_name = object.getString("companyName");
                    String content = object.getString("content");
                    String date = object.getString("date");

                    int comment_count = object.getInt("commentCount");
                    int like_count = object.getInt("likeCount");
                    int postId = object.getInt("postId");
                    Boolean likeControl = object.getBoolean("likeControl");
                    Boolean favControl = object.getBoolean("favControl");
                    postDetail.add(new Post(postId, content, date, company_name, image_url, company_image, user_image, String.valueOf(like_count), String.valueOf(comment_count), likeControl,favControl));

                }
                Collections.sort(postDetail, new Comparator<Post>() {
                    @Override
                    public int compare(Post t1, Post t2) {
                        return t2.getDate().compareTo(t1.getDate());
                    }
                });
                adapter.notifyDataSetChanged();

            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error " + e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    private void loadData() {
        try {
            String filters = sessionManager.filters(getContext());

            String[] splitFilter = filters.split(",");

            String newFilters = "";

            for (String str : splitFilter) {
                newFilters += "'" + str + "',";

            }
            Map<String, String> params = new HashMap<>();
            params.put("data", newFilters);

            MyVolley volley = new MyVolley();
            volley.volley(getContext(), ADVICE_COMPANY_URL, params, TAG, new MyVolley.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    try {
                        Boolean control = jsonObject.getBoolean("success");
                        if (control) {
                            companyDetail.clear();
                            JSONArray jsonArray = jsonObject.getJSONArray("company_list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String sector = object.getString("sector");
                                String image = ServerAdress.company_images + object.getString("image");
                                Double point = object.getDouble("point");

                                companyDetail.add(new Company(id, name, sector, image, point));

                            }
                            Collections.sort(companyDetail, new Comparator<Company>() {
                                @Override
                                public int compare(Company t1, Company t2) {
                                    return t2.getPoint().compareTo(t1.getPoint());
                                }
                            });
                            adapter2.notifyDataSetChanged();


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (NullPointerException e) {
            View filterview = View.inflate(getContext(), R.layout.filter_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final LinearLayout linearLayout = filterview.findViewById(R.id.filter_linear);

            final String[] sectors = getResources().getStringArray(R.array.sectors);
            final CheckBox[] checkBox = new CheckBox[sectors.length];

            for (int i = 1; i < sectors.length; i++) {
                checkBox[i] = new CheckBox(getContext());
                checkBox[i].setId(i);
                checkBox[i].setText(sectors[i]);
                linearLayout.addView(checkBox[i]);

            }

            builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int pos) {
                    String sectorList = "";
                    for (int i = 1; i < sectors.length; i++) {
                        if (checkBox[i].isChecked())
                            sectorList += checkBox[i].getText().toString() + ",";
                    }
                    sessionManager.addFilter(sectorList);
                    loadData();
                }
            });

            builder.setView(filterview);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }

    private void setFilters() {
        try {
            filters = sessionManager.filters(getContext());
            View filterview = View.inflate(getContext(), R.layout.filter_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            final LinearLayout linearLayout = filterview.findViewById(R.id.filter_linear);

            final String[] sectors = getResources().getStringArray(R.array.sectors);
            final CheckBox[] checkBox = new CheckBox[sectors.length];

            String[] filterArray = filters.split(",");
            for (int i = 1; i < sectors.length; i++) {
                checkBox[i] = new CheckBox(getContext());
                checkBox[i].setId(i);
                checkBox[i].setText(sectors[i]);
                linearLayout.addView(checkBox[i]);
                if (ArrayUtils.contains(filterArray, checkBox[i].getText().toString()))
                    checkBox[i].setChecked(true);
            }


            builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int pos) {
                    String sectorList = "";
                    for (int i = 1; i < sectors.length; i++) {
                        if (checkBox[i].isChecked())
                            sectorList += checkBox[i].getText().toString() + ",";
                    }
                    sessionManager.addFilter(sectorList);
                    loadData();
                }
            });

            builder.setView(filterview);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (NullPointerException e) {

        }
    }

}
