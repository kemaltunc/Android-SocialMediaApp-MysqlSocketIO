package com.example.kemal.seniorproject.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.example.kemal.seniorproject.CommentActivity;
import com.example.kemal.seniorproject.LikeBottomSheet;
import com.example.kemal.seniorproject.Model.Post;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.CommentBottomSheet;
import com.example.kemal.seniorproject.Settings.MyGlide;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.User.ProfilePostFragment;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.hashtaghelper.HashTagHelper;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Holder> {

    private List<Post> postDetail;
    ClickListener clickListener;
    public Context context;

    public PostAdapter(RecyclerView PostList, ClickListener clickListener) {
    }

    public PostAdapter(List<Post> postDetail, ClickListener clickListener) {
        this.postDetail = postDetail;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public PostAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_list, parent, false);

        final PostAdapter.Holder v_holder = new PostAdapter.Holder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(view, v_holder.getPosition());
            }
        });
        context = parent.getContext();


        return v_holder;
    }


    public class Holder extends RecyclerView.ViewHolder {

        ImageView post_image;
        CircleImageView user_photo, company_photo;
        TextView content, like, comments, company_name, date;
        ImageButton post_like_button, post_comment_button, fav_post;


        public Holder(View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
            user_photo = itemView.findViewById(R.id.post_user_image);
            company_photo = itemView.findViewById(R.id.post_company_image);
            content = itemView.findViewById(R.id.post_detail_content);
            like = itemView.findViewById(R.id.post_detail_like);
            comments = itemView.findViewById(R.id.post_detail_comment);
            company_name = itemView.findViewById(R.id.post_company_name);
            date = itemView.findViewById(R.id.post_detail_time);
            post_like_button = itemView.findViewById(R.id.post_like_btn);
            post_comment_button = itemView.findViewById(R.id.post_comment_btn);
            fav_post = itemView.findViewById(R.id.fav_post);


        }
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.Holder holder, int position) {
        final Post post = postDetail.get(position);
        holder.company_name.setText(post.getCompanyName());

        MyGlide.image(context, post.getUser_image_url(), holder.user_photo);
        MyGlide.image(context, post.getCompany_image_url(), holder.company_photo);
        MyGlide.image(context, post.getImage_url(), holder.post_image);

        holder.content.setText(post.getContent());
        HashTagHelper mTextHashTagHelper;

        mTextHashTagHelper = HashTagHelper.Creator.create(context.getResources().getColor(R.color.colorPrimaryDark), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {
                Toast.makeText(context, hashTag, Toast.LENGTH_SHORT).show();
            }
        });

        mTextHashTagHelper.handle(holder.content);


        holder.company_name.setText(post.getCompanyName());


        holder.like.setText(post.getLike());
        holder.comments.setText(post.getComment());

        if (post.getLikeControl())
            holder.post_like_button.setImageResource(R.drawable.ic_like);
        else
            holder.post_like_button.setImageResource(R.drawable.ic_unlike);

        if (post.getFavControl())
            holder.fav_post.setImageResource(R.drawable.ic_bookmark_black_24dp);
        else
            holder.fav_post.setImageResource(R.drawable.ic_bookmark_border_black_24dp);


        try {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = dt.parse(post.getDate());
            SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM");
            holder.date.setText(dt1.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.post_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (post.getLikeControl())
                    like(String.valueOf(post.getPostId()), holder, "true", post);
                else
                    like(String.valueOf(post.getPostId()), holder, "false", post);
            }
        });

        holder.fav_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getFavControl())
                    fav(String.valueOf(post.getPostId()), holder, "true", post);
                else
                    fav(String.valueOf(post.getPostId()), holder, "false", post);


            }
        });

        holder.post_like_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LikeBottomSheet likeBottomSheet = new LikeBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(post.getPostId()));
                likeBottomSheet.setArguments(bundle);
                likeBottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), "LikeBottomSheet");
                return false;
            }
        });


        holder.post_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CommentBottomSheet commentBottomSheet = new CommentBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(post.getPostId()));
                commentBottomSheet.setArguments(bundle);
                commentBottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), "commentBottomSheet");

            }
        });

    }


    private void like(final String postId, final Holder holder, final String type, final Post post) {
        SessionManager.init((Activity) context);
        final String myId = SessionManager.myId;

        ServerAdress.init((Activity) context);
        String POST_LIKE_URL = ServerAdress.host + "post_like.php";

        Map<String, String> params = new HashMap<>();
        params.put("postId", postId);
        params.put("userId", myId);
        params.put("type", type);

        MyVolley volley = new MyVolley();

        volley.volley(context, POST_LIKE_URL, params, "POST_LIKE_URL", new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        String action = jsonObject.getString("action");
                        String post_like = jsonObject.getString("likeCount");

                        if (action.equals("like")) {
                            holder.post_like_button.setImageResource(R.drawable.ic_like);
                            post.setLikeControl(true);

                        } else {
                            holder.post_like_button.setImageResource(R.drawable.ic_unlike);
                            post.setLikeControl(false);
                        }
                        holder.like.setText(post_like);


                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void fav(final String postId, final Holder holder, final String type, final Post post) {
        SessionManager.init((Activity) context);
        final String myId = SessionManager.myId;

        ServerAdress.init((Activity) context);
        String POST_FAV_URL = ServerAdress.host + "post_fav.php";

        Map<String, String> params = new HashMap<>();
        params.put("postId", postId);
        params.put("userId", myId);
        params.put("type", type);

        MyVolley volley = new MyVolley();
        volley.volley(context, POST_FAV_URL, params, "POSTFAV", new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        String action = jsonObject.getString("action");
                        if (action.equals("fav")) {
                            holder.fav_post.setImageResource(R.drawable.ic_bookmark_black_24dp);
                            post.setFavControl(true);

                        } else {
                            holder.fav_post.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                            post.setFavControl(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return postDetail.size();
    }


}
