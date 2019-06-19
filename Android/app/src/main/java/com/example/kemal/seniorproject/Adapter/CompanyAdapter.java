package com.example.kemal.seniorproject.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kemal.seniorproject.Model.Company;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyGlide;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.Holder> {

    private List<Company> companyDetail;
    ClickListener clickListener;
    public Context context;


    public CompanyAdapter(List<Company> companyDetail, ClickListener clickListener) {
        this.companyDetail = companyDetail;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CompanyAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.company_list, parent, false);

        final CompanyAdapter.Holder v_holder = new CompanyAdapter.Holder(v);
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
    public void onBindViewHolder(@NonNull CompanyAdapter.Holder holder, int position) {
        final Company company = companyDetail.get(position);

        holder.name.setText(company.getName());
        MyGlide.image(context,company.getImage_url(),holder.image);



    }

    @Override
    public int getItemCount() {
        return companyDetail.size();
    }


}
