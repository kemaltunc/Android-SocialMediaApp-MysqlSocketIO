package com.example.kemal.seniorproject.Search;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.kemal.seniorproject.Adapter.PostAdapter;
import com.example.kemal.seniorproject.Adapter.UserAdapter;
import com.example.kemal.seniorproject.Model.Post;
import com.example.kemal.seniorproject.Model.User;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.EventBus;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.User.UserShowActivity;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchProfileFragment extends Fragment {


    private final List<User> userDetail = new ArrayList<>();
    private UserAdapter adapter;
    private RecyclerView userList;
    private LinearLayoutManager mLinearLayout;

    String SEARCH_URL;



    public SearchProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search_profile, container, false);

        adapter = new UserAdapter(userDetail, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                User user = userDetail.get(position);

                String id = user.getUserId();
                String name = user.getName();
                String surname = user.getSurname();
                String image_url = user.getImage_url();



                Intent intent = new Intent(getContext(), UserShowActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("surname", surname);
                intent.putExtra("image_url", image_url);
                startActivity(intent);
            }
        });
        userList = view.findViewById(R.id.recycler_view);
        mLinearLayout = new LinearLayoutManager(getContext());
        userList.setNestedScrollingEnabled(false);
        userList.setHasFixedSize(true);
        userList.setLayoutManager(mLinearLayout);
        userList.setAdapter(adapter);

        ServerAdress.init(getActivity());
        SEARCH_URL = ServerAdress.host + "search.php";

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getBus().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getBus().unregister(this);
    }

    @Subscribe
    public void receiver(Search search) {


        if (!TextUtils.isEmpty(search.getSearch()))
            find(search.getSearch());

    }

    private void find(final String search) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, SEARCH_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean success = jsonObject.getBoolean("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("search_list");

                        if (success) {
                            userDetail.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String surname = object.getString("surname");
                                String image_url = ServerAdress.profile_images + object.getString("image_url");

                                userDetail.add(new User(id, name, surname, image_url));
                            }

                            adapter.notifyDataSetChanged();
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("search_text", search);
                    params.put("type", "user");
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        } catch (Exception ex) {

        }

    }
}
