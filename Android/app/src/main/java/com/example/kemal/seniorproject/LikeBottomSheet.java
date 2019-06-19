package com.example.kemal.seniorproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.kemal.seniorproject.Adapter.UserAdapter;
import com.example.kemal.seniorproject.Model.User;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LikeBottomSheet extends BottomSheetDialogFragment {

    private ImageButton close;
    private String postId;
    private String POST_LIKE_URL;
    private RecyclerView likeList;

    private List<User> usersList = new ArrayList<>();
    private UserAdapter adapter;

    private LinearLayoutManager mLinearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.like_bottom_sheet, container, false);

        Bundle bundle = getArguments();
        postId = bundle.getString("id");

        close = view.findViewById(R.id.bottom_close);
        likeList = view.findViewById(R.id.like_list);
        ServerAdress.init(getActivity());
        POST_LIKE_URL = ServerAdress.host + "post_like_userlist.php";


        adapter = new UserAdapter(usersList, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        mLinearLayout = new LinearLayoutManager(getActivity());
        likeList.setNestedScrollingEnabled(false);
        likeList.setHasFixedSize(true);
        likeList.setLayoutManager(mLinearLayout);
        likeList.setAdapter(adapter);


        loadData();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("id", postId);
        MyVolley volley = new MyVolley();
        volley.volley(getContext(), POST_LIKE_URL, params, "LikeBottomSheet", new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("like_list");
                    if (success) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String userId = object.getString("userId");
                            String name = object.getString("name");
                            String surname = object.getString("surname");
                            String image = ServerAdress.profile_images + object.getString("image");

                            usersList.add(new User(userId, name, surname, image));

                        }

                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
