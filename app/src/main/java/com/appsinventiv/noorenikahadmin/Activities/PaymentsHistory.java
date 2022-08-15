package com.appsinventiv.noorenikahadmin.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.noorenikahadmin.Adapters.PaymentHistoryAdapter;
import com.appsinventiv.noorenikahadmin.Models.ChatModel;
import com.appsinventiv.noorenikahadmin.Models.NotificationModel;
import com.appsinventiv.noorenikahadmin.Models.PaymentsModel;
import com.appsinventiv.noorenikahadmin.Models.User;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.appsinventiv.noorenikahadmin.Utils.NotificationAsync;
import com.appsinventiv.noorenikahadmin.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PaymentsHistory extends AppCompatActivity {

    RecyclerView recycler;
    private DatabaseReference mDatabase;
    private List<PaymentsModel> itemList = new ArrayList();
    PaymentHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_history);
        this.setTitle("Payments");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mDatabase = FirebaseDatabase.getInstance("https://noorenikah-default-rtdb.firebaseio.com/").getReference();
        adapter = new PaymentHistoryAdapter(this, itemList, new PaymentHistoryAdapter.PaymentHistoryAdapterCallback() {
            @Override
            public void onApprovePayment(PaymentsModel model) {
                approvePayment(model);

            }

            @Override
            public void onProfilePayment(PaymentsModel model) {
                approveProfile(model);
            }
        });
        recycler.setAdapter(adapter);

        getDatFromDB();


    }

    private void approveProfile(PaymentsModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentsHistory.this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to Approve profile? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.child("Users").child(model.getPhone())
                        .child("paid").setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                CommonUtils.showToast("Profile Approved");
                                sendNotification(model.getPhone(), "Profile Approved",
                                        "Your Profile is approved", "profile");

                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void approvePayment(PaymentsModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentsHistory.this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to Approve payment? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.child("Payments").child(model.getPhone()).child(model.getId()).child("approved").setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                CommonUtils.showToast("Payment Approved");
                                sendNotification(model.getPhone(), "Payment Approved",
                                        "Your payment is approved", "payment");
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendNotification(String phone, String payment_approved, String your_payment_is_approved, String type) {
        mDatabase.child("Users").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.getFcmKey() != null) {
                        NotificationAsync notificationAsync = new NotificationAsync(PaymentsHistory.this);
                        String NotificationTitle = payment_approved;
                        String NotificationMessage = your_payment_is_approved;
                        notificationAsync.execute(
                                "ali",
                                user.getFcmKey(),
                                NotificationTitle,
                                NotificationMessage,
                                "",
                                "payment");
                        String key = "" + System.currentTimeMillis();
                        NotificationModel model = new NotificationModel(key, NotificationTitle,
                                NotificationMessage, type, "https://icon-library.com/images/admin-icon-png/admin-icon-png-12.jpg",
                                "admin", System.currentTimeMillis());
                        mDatabase.child("Notifications").child(user.getPhone()).child(key).setValue(model);

                        mDatabase.child("ReferralCodesHistory").child(user.getReferralCode())
                                .child(user.getPhone()).child("paid").setValue(true);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getDatFromDB() {
        mDatabase.child("Payments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        for (DataSnapshot snapshot : dataSnapshot1.getChildren()) {
                            PaymentsModel model = snapshot.getValue(PaymentsModel.class);
                            if (model != null) {
                                itemList.add(model);
                            }
                        }
                        Collections.sort(itemList, new Comparator<PaymentsModel>() {
                            @Override
                            public int compare(PaymentsModel listData, PaymentsModel t1) {
                                Long ob1 = listData.getTime();
                                Long ob2 = t1.getTime();
                                return ob2.compareTo(ob1);

                            }
                        });
                        adapter.setItemList(itemList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}