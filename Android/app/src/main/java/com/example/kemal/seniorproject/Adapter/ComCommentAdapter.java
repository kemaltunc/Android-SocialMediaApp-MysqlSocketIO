package com.example.kemal.seniorproject.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kemal.seniorproject.Model.ComComments;
import com.example.kemal.seniorproject.Model.Company;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyGlide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComCommentAdapter extends RecyclerView.Adapter<ComCommentAdapter.Holder> {

    private List<ComComments> commentsDetail;
    ClickListener clickListener;
    public Context context;


    public ComCommentAdapter(List<ComComments> commentsDetail, ClickListener clickListener) {
        this.commentsDetail = commentsDetail;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ComCommentAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.com_comment_list, parent, false);

        final ComCommentAdapter.Holder v_holder = new ComCommentAdapter.Holder(v);
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

        TextView name, content;
        CircleImageView image;
        RatingBar ratingBar;


        public Holder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.com_username);
            image = itemView.findViewById(R.id.com_comment_img);
            content = itemView.findViewById(R.id.com_comment_content);
            ratingBar = itemView.findViewById(R.id.ratingBarComment);


        }
    }

    @Override
    public void onBindViewHolder(@NonNull ComCommentAdapter.Holder holder, int position) {
        final ComComments comComments = commentsDetail.get(position);

        holder.name.setText(comComments.getUser());
        holder.content.setText(comComments.getContent());
        MyGlide.image(context, comComments.getImage(), holder.image);
        holder.ratingBar.setRating((comComments.getRating()));

        if (comComments.getType().equals("post")) {
            holder.ratingBar.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return commentsDetail.size();
    }


}
