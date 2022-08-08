package com.appsinventiv.noorenikahadmin.Adapters;

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
import com.appsinventiv.noorenikahadmin.Models.RequestPayoutModel;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.List;

public class PayoutsHistoryAdapter extends RecyclerView.Adapter<PayoutsHistoryAdapter.ViewHolder> {
    Context context;
    List<RequestPayoutModel> itemList;
    PayoutHistoryAdapterCallback callback;

    public PayoutsHistoryAdapter(Context context, List<RequestPayoutModel> itemList,
                                 PayoutHistoryAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(List<RequestPayoutModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.payout_history_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestPayoutModel model = itemList.get(position);

        holder.name.setText("Name: " + model.getName());
        holder.amountRequested.setText("Rs. " + model.getAmount());
        holder.details.setText("Phone: 0" + model.getPhone() + "\nPay Via: " + model.getPayoutOption()
                + "\nRequested Date: " + CommonUtils.getFormattedDate(model.getTime()));

        holder.paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMarkAsPaid(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, amountRequested, name, details;
        ImageView image;
        Button paid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            amountRequested = itemView.findViewById(R.id.amountRequested);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            paid = itemView.findViewById(R.id.paid);
            details = itemView.findViewById(R.id.details);

        }
    }

    public interface PayoutHistoryAdapterCallback {

        public void onMarkAsPaid(RequestPayoutModel model);
    }


}
