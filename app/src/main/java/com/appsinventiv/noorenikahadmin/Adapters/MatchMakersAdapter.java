package com.appsinventiv.noorenikahadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.noorenikahadmin.Activities.MatchMakerProfile;
import com.appsinventiv.noorenikahadmin.Models.MatchMakerModel;
import com.appsinventiv.noorenikahadmin.Models.PaymentsModel;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.List;

public class MatchMakersAdapter extends RecyclerView.Adapter<MatchMakersAdapter.ViewHolder> {
    Context context;
    List<MatchMakerModel> itemList;

    public MatchMakersAdapter(Context context, List<MatchMakerModel> itemList
                              ) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<MatchMakerModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.match_maker_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchMakerModel model = itemList.get(position);
        Glide.with(context).load(model.getPicUrl()).into(holder.image);
        if (model.isApproved()) {
            holder.status.setText("Approved");
            holder.status.setBackgroundResource(R.drawable.rejected);
        } else {

            holder.status.setText("Pending");
            holder.status.setBackgroundResource(R.drawable.pending);

        }
        holder.name.setText( model.getMbName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MatchMakerProfile.class);
                intent.putExtra("matchMakerId",model.getPhone());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView status, name;
        ImageView image;
        Button approve,reject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.status);
            image = itemView.findViewById(R.id.image);

            name = itemView.findViewById(R.id.name);

        }
    }



}
