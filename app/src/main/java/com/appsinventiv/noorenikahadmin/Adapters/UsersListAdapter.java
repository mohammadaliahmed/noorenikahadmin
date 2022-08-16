package com.appsinventiv.noorenikahadmin.Adapters;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.noorenikahadmin.Models.PaymentsModel;
import com.appsinventiv.noorenikahadmin.Models.User;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {
    Context context;
    List<User> itemList;
    UsersAdapterCallback callback;

    public UsersListAdapter(Context context, List<User> itemList,
                            UsersAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(List<User> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User model = itemList.get(position);
        Glide.with(context).load(model.getLivePicPath()).into(holder.image);
        Glide.with(context)
                .load(model.getLivePicPath())

                .into(holder.image);
        holder.name.setText(model.getName() + ", " + model.getAge());
        holder.details.setText("Education: " + model.getEducation() + "\n" + "City: " + model.getCity()
                + "\nCast: " + model.getCast());
        if (model.isRejected()) {
            holder.status.setText("Rejected");
            holder.status.setBackgroundResource(R.drawable.rejected);
        } else {

            if (model.isPaid()) {
                holder.status.setText("Active");
                holder.status.setBackgroundResource(R.drawable.approved);

            } else {
                holder.status.setText("Pending");
                holder.status.setBackgroundResource(R.drawable.pending);

            }
        }

        holder.approveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onApproveProfile(model);
            }
        });
        holder.rejectProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onRejectProfile(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, status, name, phone, details;
        ImageView image;
        Button approveProfile, rejectProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
            image = itemView.findViewById(R.id.image);
            approveProfile = itemView.findViewById(R.id.approveProfile);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            details = itemView.findViewById(R.id.details);
            rejectProfile = itemView.findViewById(R.id.rejectProfile);

        }
    }

    public interface UsersAdapterCallback {
        public void onApproveProfile(User model);

        public void onRejectProfile(User model);
    }


}
