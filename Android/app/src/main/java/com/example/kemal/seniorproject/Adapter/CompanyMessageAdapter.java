package com.example.kemal.seniorproject.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kemal.seniorproject.Model.Message;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyGlide;
import com.example.kemal.seniorproject.Settings.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyMessageAdapter extends RecyclerView.Adapter<CompanyMessageAdapter.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;


    private List<Message> messagesList;
    ClickListener clickListener;
    public Context context;


    public CompanyMessageAdapter(List<Message> messagesList, ClickListener clickListener) {
        this.messagesList = messagesList;
        this.clickListener = clickListener;

    }

    @NonNull
    @Override
    public CompanyMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.company_message_right, parent, false);
                viewHolder = new CompanyMessageAdapter.ViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.company_message_left, parent, false);
                viewHolder = new CompanyMessageAdapter.ViewHolder(viewChatOther);
                break;

        }

        context = parent.getContext();

        return (CompanyMessageAdapter.ViewHolder) viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView image;
        public TextView message, name;
        public TextView date;

        public ViewHolder(View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.message_user_content);
            date = itemView.findViewById(R.id.message_date);
            image = itemView.findViewById(R.id.circleImageView);
            name = itemView.findViewById(R.id.username);


        }
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyMessageAdapter.ViewHolder holder, int position) {
        final Message messages = messagesList.get(position);

        String myId = SessionManager.myId;

        if (!myId.equals(messages.getSenderId())) {
            MyGlide.image(context, messages.getUserImage(), holder.image);
            holder.name.setText(messages.getName());
        }
        holder.message.setText(messages.getMessage());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date newDate = format.parse(messages.getDate());

            format = new SimpleDateFormat("hh:mm");
            holder.date.setText(format.format(newDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }


    @Override
    public int getItemViewType(int position) {
        String myId = SessionManager.myId;
        if (myId.equals(messagesList.get(position).getSenderId()))
            return VIEW_TYPE_ME;
        else if (!myId.equals(messagesList.get(position).getSenderId()))
            return VIEW_TYPE_OTHER;
        return VIEW_TYPE_ME;
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
