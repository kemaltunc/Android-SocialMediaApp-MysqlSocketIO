package com.example.kemal.seniorproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kemal.seniorproject.Model.Notification;
import com.example.kemal.seniorproject.Model.User;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyGlide;
import com.example.kemal.seniorproject.User.UserShowActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    private List<Notification> notificationList;
    ClickListener clickListener;
    public Context context;


    public NotificationAdapter(List<Notification> notificationList, ClickListener clickListener) {
        this.notificationList = notificationList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NotificationAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.last_message_list, parent, false);

        final NotificationAdapter.Holder v_holder = new NotificationAdapter.Holder(v);
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

        TextView name, content, date;
        CircleImageView image;

        public Holder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.lastMessageName);
            content = itemView.findViewById(R.id.message_user_content);
            date = itemView.findViewById(R.id.message_date);
            image = itemView.findViewById(R.id.userLastMessageImage);


        }
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.Holder holder, int position) {
        final Notification notification = notificationList.get(position);

        MyGlide.image(context, notification.getImage(), holder.image);

        holder.name.setText(notification.getName());
        holder.content.setText(notification.getContent());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date newDate = format.parse(notification.getDate());

            format = new SimpleDateFormat("dd/MM");
            holder.date.setText(format.format(newDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }


}