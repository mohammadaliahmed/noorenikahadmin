package com.appsinventiv.noorenikahadmin.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.noorenikahadmin.Adapters.PayoutsHistoryAdapter;
import com.appsinventiv.noorenikahadmin.Models.NotificationModel;
import com.appsinventiv.noorenikahadmin.Models.ReferralCodePaidModel;
import com.appsinventiv.noorenikahadmin.Models.RequestPayoutModel;
import com.appsinventiv.noorenikahadmin.Models.User;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.appsinventiv.noorenikahadmin.Utils.NotificationAsync;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Payouts extends AppCompatActivity{


    RecyclerView recycler;
    PayoutsHistoryAdapter adapter;
    private List<RequestPayoutModel> itemList = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payouts);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        mDatabase = FirebaseDatabase.getInstance("https://noorenikah-default-rtdb.firebaseio.com/").getReference();

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new PayoutsHistoryAdapter(this, itemList, new PayoutsHistoryAdapter.PayoutHistoryAdapterCallback() {
            @Override
            public void onMarkAsPaid(RequestPayoutModel model) {
                showAlert(model);

//                mDatabase.child("OldPayouts").child(model.getId()).setValue(model);

            }
        });
        recycler.setAdapter(adapter);
        getDatafromDb();
    }

    private void showAlert(RequestPayoutModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Mark as paid? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                model.setPayoutTime(System.currentTimeMillis());
                model.setPaid(true);
                mDatabase.child("RequestPayout").child(model.getPhone()).child(model.getId()).child("paid").setValue(true);
                CommonUtils.showToast("Marked as paid");
                sendNotification(model);
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendNotification(RequestPayoutModel model) {
        mDatabase.child("Users").child(model.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                NotificationAsync notificationAsync = new NotificationAsync(Payouts.this);
                String NotificationTitle = "Payment";
                String NotificationMessage = "You have been paid Rs." + model.getAmount();
                notificationAsync.execute(
                        "ali",
                        user.getFcmKey(),
                        NotificationTitle,
                        NotificationMessage,
                        "",
                        "payout");
                String key = "" + System.currentTimeMillis();
                NotificationModel model = new NotificationModel(key, NotificationTitle,
                        NotificationMessage, "payout", "https://icon-library.com/images/admin-icon-png/admin-icon-png-12.jpg", "admin", System.currentTimeMillis());
                mDatabase.child("Notifications").child(user.getPhone()).child(key).setValue(model);

                mDatabase.child("ReferralCodesHistory").child(user.getMyReferralCode()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String,ReferralCodePaidModel> referList=new HashMap<>();
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            ReferralCodePaidModel model1=snapshot.getValue(ReferralCodePaidModel.class);
                            if(model1!=null){
                                referList.put(model1.getPhone(),model1);
                            }
                        }
                        mDatabase.child("OldReferralData").child(user.getMyReferralCode()).setValue(referList);
                        mDatabase.child("ReferralCodesHistory").child(user.getMyReferralCode()).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getDatafromDb() {
        mDatabase.child("RequestPayout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            RequestPayoutModel payoutModel = snapshot1.getValue(RequestPayoutModel.class);
                            if (!payoutModel.isPaid()) {
                                itemList.add(payoutModel);

                            }
                        }
                    }
                    adapter.setItemList(itemList);
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