package com.appsinventiv.noorenikahadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.noorenikahadmin.Models.NewUserModel;
import com.appsinventiv.noorenikahadmin.Models.PromotionBanner;
import com.appsinventiv.noorenikahadmin.R;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PromotionalBannersAdapter extends RecyclerView.Adapter<PromotionalBannersAdapter.ViewHolder> {
    Context context;
    List<PromotionBanner> itemList;

    public PromotionalBannersAdapter(Context context, List<PromotionBanner> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<PromotionBanner> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.promotion_banner_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PromotionBanner item = itemList.get(position);

        Glide.with(context)
                .load(item.getImgUrl())
                .into(holder.image);
        holder.text.setText("Url: " + item.getUrl() + "\n" + "Placement: " + item.getPlacement());


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);

            image = itemView.findViewById(R.id.image);

        }
    }


}
