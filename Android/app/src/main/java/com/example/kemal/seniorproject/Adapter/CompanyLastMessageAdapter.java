package com.example.kemal.seniorproject.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kemal.seniorproject.Model.Message;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyGlide;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyLastMessageAdapter extends RecyclerView.Adapter<CompanyLastMessageAdapter.ViewHolder> {


    private List<Message> messagesList;
    ClickListener clickListener;
    public Context context;

    public CompanyLastMessageAdapter(List<Message> messagesList, ClickListener clickListener) {
        this.messagesList = messagesList;
        this.clickListener = clickListener;

    }


    @Override
    public CompanyLastMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.last_message_list, parent, false);

        final CompanyLastMessageAdapter.ViewHolder v_holder = new CompanyLastMessageAdapter.ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(view, v_holder.getPosition());
            }
        });
        context = parent.getContext();


        return v_holder;


    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public CircleImageView imageview;
        public TextView message, name;
        public TextView date;


        public ViewHolder(View itemView) {
            super(itemView);


            message = itemView.findViewById(R.id.message_user_content);
            date = itemView.findViewById(R.id.message_date);
            imageview = itemView.findViewById(R.id.userLastMessageImage);
            name = itemView.findViewById(R.id.lastMessageName);


        }
    }

    @Override
    public void onBindViewHolder(@NonNull final CompanyLastMessageAdapter.ViewHolder holder, int position) {
        final Message messages = messagesList.get(position);

        holder.message.setText(messages.getSenderId() + ":" + messages.getMessage());
        holder.name.setText(messages.getName());
        MyGlide.image(context, messages.getUserImage(), holder.imageview);

    }


    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}

