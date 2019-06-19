package com.example.kemal.seniorproject.Company;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.kemal.seniorproject.Adapter.PostAdapter;
import com.example.kemal.seniorproject.Model.Post;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;

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
public class CompanyPostFragment extends Fragment {

    private final List<Post> postDetail = new ArrayList<>();
    private PostAdapter adapter;
    private RecyclerView PostList;
    private LinearLayoutManager mLinearLayout;


    private String POST_LIST_URL;
    private String myId, companyId;
    private String TAG = "CompanyPostFragment";

    public CompanyPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_post, container, false);

        adapter = new PostAdapter(postDetail, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });


        SessionManager.init(getActivity());
        myId = SessionManager.myId;

        Bundle bundle = getArguments();

        companyId = bundle.getString("companyId");

        PostList = view.findViewById(R.id.recycler_view);
        mLinearLayout = new LinearLayoutManager(getContext());
        PostList.setNestedScrollingEnabled(false);
        PostList.setHasFixedSize(true);
        PostList.setLayoutManager(mLinearLayout);
        PostList.setAdapter(adapter);

        ServerAdress.init(getActivity());
        POST_LIST_URL = ServerAdress.host + "post_list.php";
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Map<String, String> params = new HashMap<>();
        params.put("id", myId);
        params.put("companyId", companyId);
        params.put("type", "company");

        MyVolley volley = new MyVolley();
        volley.volley(getContext(), POST_LIST_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                loadPost(jsonObject);
            }
        });


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
                Toast.makeText(getContext(), "basarisiz", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error " + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

}
