package com.example.kemal.seniorproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kemal.seniorproject.Adapter.ComCommentAdapter;
import com.example.kemal.seniorproject.CommentActivity;
import com.example.kemal.seniorproject.Model.ComComments;
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

public class CommentBottomSheet extends BottomSheetDialogFragment {

    private ImageButton close;
    private String postId, myId, name, surname, image_url;

    private ImageButton comment_send;
    private EditText ed_comment;

    private String POST_COMMENT_URL;

    private ProgressDialog progressDialog;

    private final List<ComComments> commentDetail = new ArrayList<>();
    private ComCommentAdapter adapter;
    private RecyclerView commentList;

    private LinearLayoutManager mLinearLayout;
    private String TAG = "CommentBottomSheet";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_bottom_sheet, container, false);


        Bundle bundle = getArguments();
        postId = bundle.getString("id");

        progressDialog = new ProgressDialog(getContext());

        close = view.findViewById(R.id.bottom_close);
        commentList = view.findViewById(R.id.comment_list);
        ed_comment = view.findViewById(R.id.ed_comment);
        comment_send = view.findViewById(R.id.comment_send);

        ServerAdress.init(getActivity());
        POST_COMMENT_URL = ServerAdress.host + "post_comment_add.php";
        SessionManager.init(getActivity());
        myId = SessionManager.myId;
        name = SessionManager.name;
        surname = SessionManager.surname;
        image_url = SessionManager.userImage;




        adapter = new ComCommentAdapter(commentDetail, new ClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        mLinearLayout = new LinearLayoutManager(getActivity());
        commentList.setNestedScrollingEnabled(false);
        commentList.setHasFixedSize(true);
        commentList.setLayoutManager(mLinearLayout);
        commentList.setAdapter(adapter);

        comment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = ed_comment.getText().toString().trim();

                InsertComment(content);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        loadData();

        return view;
    }

    private void InsertComment(final String content) {
        progressDialog.setTitle("Lütfen bekleyin...");
        progressDialog.setMessage("Yorum yapılıyor...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("userId", myId);
        params.put("postId", postId);
        params.put("content", content);
        params.put("type", "write");


        MyVolley volley = new MyVolley();

        volley.volley(getContext(), POST_COMMENT_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        Toast.makeText(getContext(), "Yorum başarıyla gönderildi.", Toast.LENGTH_SHORT).show();
                        ed_comment.setText("");

                        commentDetail.add(new ComComments(0, content, "12.05", image_url, name + " " + surname, "post"));

                        Collections.sort(commentDetail, new Comparator<ComComments>() {
                            @Override
                            public int compare(ComComments t1, ComComments t2) {
                                return t2.getDate().compareTo(t1.getDate());
                            }
                        });
                        adapter.notifyDataSetChanged();
                    } else
                        Toast.makeText(getContext(), "Yorum gönderilirken bir hata oluştu.", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        progressDialog.dismiss();
    }

    private void loadData() {


        Map<String, String> params = new HashMap<>();
        params.put("postId", postId);
        params.put("type", "read");

        MyVolley volley = new MyVolley();

        volley.volley(getContext(), POST_COMMENT_URL, params, TAG, new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("comment_list");

                    if (success) {
                        commentDetail.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String name = object.getString("name");
                            String surname = object.getString("surname");
                            String image = ServerAdress.profile_images + object.getString("userImage");
                            String content = object.getString("content");

                            commentDetail.add(new ComComments(0, content, "12.05", image, name + " " + surname, "post"));

                        }
                        Collections.sort(commentDetail, new Comparator<ComComments>() {
                            @Override
                            public int compare(ComComments t1, ComComments t2) {
                                return t2.getDate().compareTo(t1.getDate());
                            }
                        });
                        adapter.notifyDataSetChanged();


                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });

    }


}