package com.example.kemal.seniorproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kemal.seniorproject.Company.CompanyShowActivity;
import com.example.kemal.seniorproject.Model.ComComments;
import com.example.kemal.seniorproject.Model.Company;
import com.example.kemal.seniorproject.R;
import com.example.kemal.seniorproject.Settings.ClickListener;
import com.example.kemal.seniorproject.Settings.MyGlide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdviceAdapter extends RecyclerView.Adapter<AdviceAdapter.Holder> {

    private List<Company> companyDetail;
    ClickListener clickListener;
    public Context context;


    public AdviceAdapter(List<Company> companyDetail, ClickListener clickListener) {
        this.companyDetail = companyDetail;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AdviceAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.advice_layout, parent, false);

        final AdviceAdapter.Holder v_holder = new AdviceAdapter.Holder(v);
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

        TextView name, sector;
        ImageView image;


        public Holder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.advice_name);
            sector = itemView.findViewById(R.id.advice_sector);
            image = itemView.findViewById(R.id.advice_img);


        }
    }

    @Override
    public void onBindViewHolder(@NonNull AdviceAdapter.Holder holder, int position) {
        final Company company = companyDetail.get(position);

        holder.name.setText(company.getName());
        holder.sector.setText(company.getSector());
        MyGlide.image(context, company.getImage_url(), holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CompanyShowActivity.class);
                intent.putExtra("id", company.getCompanyId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return companyDetail.size();
    }


}
