package com.example.kemal.seniorproject.Search;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kemal.seniorproject.Adapter.CompanyAdapter;
import com.example.kemal.seniorproject.Adapter.UserAdapter;
import com.example.kemal.seniorproject.Company.CompanyShowActivity;
import com.example.kemal.seniorproject.Model.Company;
import com.example.kemal.seniorproject.Model.User;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.EventBus;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.User.UserShowActivity;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchCompanyFragment extends Fragment {

    private final List<Company> companyDetail = new ArrayList<>();
    private CompanyAdapter adapter;
    private RecyclerView companyList;
    private LinearLayoutManager mLinearLayout;

    String SEARCH_URL;

    public SearchCompanyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_company, container, false);

        adapter = new CompanyAdapter(companyDetail, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {


                Company company = companyDetail.get(position);

                String id = company.getCompanyId();
                String name = company.getName();
                String image_url = company.getImage_url();


                Intent intent = new Intent(getContext(), CompanyShowActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("image_url", image_url);
                startActivity(intent);

            }
        });

        companyList = view.findViewById(R.id.recycler_view);
        mLinearLayout = new LinearLayoutManager(getContext());
        companyList.setNestedScrollingEnabled(false);
        companyList.setHasFixedSize(true);
        companyList.setLayoutManager(mLinearLayout);
        companyList.setAdapter(adapter);

        ServerAdress.init(getActivity());
        SEARCH_URL = ServerAdress.host + "company_search.php";


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
                            companyDetail.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String surname = object.getString("sector");
                                String image_url = ServerAdress.company_images + object.getString("image_url");

                                companyDetail.add(new Company(id, name, surname, image_url));

                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Error ", Toast.LENGTH_SHORT).show();
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
                    params.put("type", "company");
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        } catch (Exception ex) {

        }

    }

}
