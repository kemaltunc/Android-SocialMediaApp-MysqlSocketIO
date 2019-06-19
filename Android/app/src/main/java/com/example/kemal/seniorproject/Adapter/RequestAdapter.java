package com.example.kemal.seniorproject.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kemal.seniorproject.Model.Post;
import com.example.kemal.seniorproject.Model.User;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.RequestListActivity;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyGlide;
import com.example.kemal.seniorproject.Settings.MyVolley;
import com.example.kemal.seniorproject.Settings.ServerAdress;
import com.example.kemal.seniorproject.User.UserShowActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.Holder> {

    private List<User> userDetail;
    ClickListener clickListener;
    public Context context;


    public RequestAdapter(List<User> userDetail, ClickListener clickListener) {
        this.userDetail = userDetail;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RequestAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_list, parent, false);

        final RequestAdapter.Holder v_holder = new RequestAdapter.Holder(v);
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

        Button accept, decline;

        public Holder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userlist_name);
            image = itemView.findViewById(R.id.userlist_image);

            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);


        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestAdapter.Holder holder, final int position) {
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


        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request("accept", user, position);
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request("decline", user, position);
            }
        });

    }

    private void request(String type, User user, int position) {
        String COMPANY_REQUEST = ServerAdress.host + "company_request.php";

        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getUserId());
        params.put("companyId", user.getCompanyId());
        params.put("type", type);

        MyVolley volley = new MyVolley();
        volley.volley(context, COMPANY_REQUEST, params, "", new MyVolley.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Intent intent = new Intent(context, RequestListActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userDetail.size();
    }


}
