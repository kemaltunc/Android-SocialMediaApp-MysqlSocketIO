package com.example.kemal.seniorproject.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.kemal.seniorproject.MessageSendActivity;
import com.example.kemal.seniorproject.Model.Message;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyGlide;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    private static final int VIEW_TYPE_ME_IMAGE = 3;
    private static final int VIEW_TYPE_OTHER_IMAGE = 4;


    private String messageType;


    private List<Message> messagesList;
    ClickListener clickListener;
    public Context context;

    public MessageAdapter(List<Message> messagesList, String messageType, ClickListener clickListener) {
        this.messagesList = messagesList;
        this.clickListener = clickListener;
        this.messageType = messageType;
    }


    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (messageType.equals("private_message")) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

            RecyclerView.ViewHolder viewHolder = null;

            switch (viewType) {

                case VIEW_TYPE_ME:
                    View viewChatMine = layoutInflater.inflate(R.layout.message_right, parent, false);
                    viewHolder = new ViewHolder(viewChatMine);
                    break;

                case VIEW_TYPE_OTHER:
                    View viewChatOther = layoutInflater.inflate(R.layout.message_left, parent, false);
                    viewHolder = new ViewHolder(viewChatOther);
                    break;
                case VIEW_TYPE_ME_IMAGE:
                    View viewChatMineImage = layoutInflater.inflate(R.layout.message_right_image, parent, false);
                    viewHolder = new ViewHolder(viewChatMineImage);
                    break;
                case VIEW_TYPE_OTHER_IMAGE:
                    View viewChatOtherImage = layoutInflater.inflate(R.layout.message_left_image, parent, false);
                    viewHolder = new ViewHolder(viewChatOtherImage);
                    break;
            }
            context = parent.getContext();

            return (ViewHolder) viewHolder;
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.last_message_list, parent, false);

            final MessageAdapter.ViewHolder v_holder = new MessageAdapter.ViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, v_holder.getPosition());
                }
            });
            context = parent.getContext();


            return v_holder;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public CircleImageView imageview;
        public TextView message, name;
        public TextView date;
        public ImageView message_image;


        public ViewHolder(View itemView) {
            super(itemView);


            message = itemView.findViewById(R.id.message_user_content);
            date = itemView.findViewById(R.id.message_date);
            message_image = itemView.findViewById(R.id.message_image);

            if (messageType.equals("lastMessage")) {
                imageview = itemView.findViewById(R.id.userLastMessageImage);
                name = itemView.findViewById(R.id.lastMessageName);
            }


        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {

        // serverAdress = new ServerAdress((Activity) context);
        ServerAdress.init((Activity) context);
        final Message messages = messagesList.get(position);


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date newDate = format.parse(messages.getDate());

            format = new SimpleDateFormat("hh:mm");
            holder.date.setText(format.format(newDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (messageType.equals("lastMessage")) {
            holder.name.setText(messages.getName());
            Glide.with(context).load(messages.getUserImage()).into(holder.imageview);

            if (messagesList.get(position).getMessageType().equals("text"))
                holder.message.setText(messages.getMessage());
            else if (messagesList.get(position).getMessageType().equals("image"))
                holder.message.setText("Görüntü");
        } else {
            if (messagesList.get(position).getMessageType().equals("image"))
                MyGlide.image(context, ServerAdress.message_images + messages.getMessage(), holder.message_image);
            else
                holder.message.setText(messages.getMessage());

        }


    }


    @Override
    public int getItemViewType(int position) {


        String myId = SessionManager.myId;
        if (myId.equals(messagesList.get(position).getSenderId()) && messagesList.get(position).getMessageType().equals("text")) {
            return VIEW_TYPE_ME;
        } else if (!myId.equals(messagesList.get(position).getSenderId()) && messagesList.get(position).getMessageType().equals("text")) {
            return VIEW_TYPE_OTHER;
        } else if (myId.equals(messagesList.get(position).getSenderId()) && messagesList.get(position).getMessageType().equals("image")) {
            return VIEW_TYPE_ME_IMAGE;
        } else if (!myId.equals(messagesList.get(position).getSenderId()) && messagesList.get(position).getMessageType().equals("image")) {
            return VIEW_TYPE_OTHER_IMAGE;
        }

        return VIEW_TYPE_ME;

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}


