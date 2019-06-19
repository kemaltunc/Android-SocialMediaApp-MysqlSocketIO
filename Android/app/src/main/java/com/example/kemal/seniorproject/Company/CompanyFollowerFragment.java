package com.example.kemal.seniorproject.Company;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kemal.seniorproject.Adapter.UserAdapter;
import com.example.kemal.seniorproject.Model.User;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.User.UserShowActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyFollowerFragment extends Fragment {

    private String companyId;
    private String COMPANY_INFO_URL, type;


    private final List<User> userDetail = new ArrayList<>();
    private UserAdapter adapter;
    private RecyclerView userList;
    private LinearLayoutManager mLinearLayout;

    private String TAG = "CompanyFollowerFragment";

    public CompanyFollowerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_company_follower, container, false);

        Bundle bundle = getArguments();

        companyId = bundle.getString("companyId");
        type = bundle.getString("type");
        ServerAdress.init(getActivity());
        COMPANY_INFO_URL = ServerAdress.host + "company_info.php";

        Map<String, String> params = new HashMap<>();
        params.put("id", companyId);
        params.put("type", type);

        MyVolley volley = new MyVolley();

        volley.volley(getContext(), COMPANY_INFO_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadData(jsonObject);
            }
        });

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
        return view;
    }

    private void loadData(JSONObject jsonObject) {
        try {
            Boolean control = jsonObject.getBoolean("success");
            userDetail.clear();
            if (control) {
                JSONArray jsonArray = jsonObject.getJSONArray("user_list");

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
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

}
