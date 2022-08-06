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
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.List;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> {
    Context context;
    List<PaymentsModel> itemList;
    PaymentHistoryAdapterCallback callback;

    public PaymentHistoryAdapter(Context context, List<PaymentsModel> itemList,
                                 PaymentHistoryAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(List<PaymentsModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.payment_history_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentsModel model = itemList.get(position);
        Glide.with(context).load(model.getPicUrl()).into(holder.image);
        if (model.isRejected()) {
            holder.paymentStatus.setText("Payment: Rejected");
            holder.paymentStatus.setBackgroundResource(R.drawable.rejected);
        } else {
            if (model.isApproved()) {
                holder.paymentStatus.setText("Payment: Approved");
                holder.paymentStatus.setBackgroundResource(R.drawable.approved);

            } else {
                holder.paymentStatus.setText("Payment: Pending");
                holder.paymentStatus.setBackgroundResource(R.drawable.pending);
            }
        }
        holder.name.setText("Name: \n"+model.getName());
        holder.phone.setText("Phone:\n0"+model.getPhone());
        holder.date.setText("Date: \n" + CommonUtils.getFormattedDateOnly(model.getTime()));

        holder.approvePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onApprovePayment(model);
            }
        });
        holder.approveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onProfilePayment(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, paymentStatus, name, phone;
        ImageView image;
        Button approvePayment, approveProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
            image = itemView.findViewById(R.id.image);
            approveProfile = itemView.findViewById(R.id.approveProfile);
            name = itemView.findViewById(R.id.name);
            approvePayment = itemView.findViewById(R.id.approvePayment);
            phone = itemView.findViewById(R.id.phone);

        }
    }

    public interface PaymentHistoryAdapterCallback {
        public void onApprovePayment(PaymentsModel model);

        public void onProfilePayment(PaymentsModel model);
    }


}
