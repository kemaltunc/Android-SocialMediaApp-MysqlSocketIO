package com.example.kemal.seniorproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kemal.seniorproject.Model.Post;
import com.example.kemal.seniorproject.Model.User;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyGlide;
import com.example.kemal.seniorproject.User.UserShowActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Holder> {

    private List<User> userDetail;
    ClickListener clickListener;
    public Context context;


    public UserAdapter(List<User> userDetail, ClickListener clickListener) {
        this.userDetail = userDetail;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public UserAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list, parent, false);

        final UserAdapter.Holder v_holder = new UserAdapter.Holder(v);
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

        TextView name;
        CircleImageView image;

        public Holder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userlist_name);
            image = itemView.findViewById(R.id.userlist_image);


        }
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.Holder holder, int position) {
        final User user = userDetail.get(position);

        holder.name.setText(user.getName() + " " + user.getSurname());
        MyGlide.image(context, user.getImage_url(), holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserShowActivity.class);
                intent.putExtra("id", user.getUserId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userDetail.size();
    }


}
