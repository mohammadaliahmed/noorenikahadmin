package com.appsinventiv.noorenikahadmin.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.noorenikahadmin.Adapters.PaymentHistoryAdapter;
import com.appsinventiv.noorenikahadmin.Adapters.UsersListAdapter;
import com.appsinventiv.noorenikahadmin.Models.NotificationModel;
import com.appsinventiv.noorenikahadmin.Models.PaymentsModel;
import com.appsinventiv.noorenikahadmin.Models.User;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.appsinventiv.noorenikahadmin.Utils.NotificationAsync;
import com.appsinventiv.noorenikahadmin.Utils.NotificationInterface;
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
import java.util.HashMap;
import java.util.List;

public class UserList extends AppCompatActivity implements NotificationInterface {

    RecyclerView recycler;
    private DatabaseReference mDatabase;
    private List<User> itemList = new ArrayList();
    UsersListAdapter adapter;
    EditText search;
    TextView userCount;
    Button verofy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        this.setTitle("Users");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        userCount = findViewById(R.id.userCount);
        verofy = findViewById(R.id.verofy);
        search = findViewById(R.id.search);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mDatabase = FirebaseDatabase.getInstance("https://noorenikah-default-rtdb.firebaseio.com/").getReference();
        adapter = new UsersListAdapter(this, itemList, new UsersListAdapter.UsersAdapterCallback() {
            @Override
            public void onApproveProfile(User model) {
                approveProfile(model);
            }

            @Override
            public void onRejectProfile(User model) {
                rejectProfile(model);
            }
        });
        recycler.setAdapter(adapter);
        getDatFromDB();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());

            }
        });
//        verofy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (User user : itemList) {
//                    if (user.getPhone() != null) {
//                        mDatabase.child("Users").child(user.getPhone()).child("phoneVerified").setValue(true);
//                    }
//                }
//            }
//        });

    }

    private void approveProfile(User model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserList.this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to Approve profile? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("rejected", false);
                map.put("paid", true);

                mDatabase.child("Users").child(model.getPhone())
                        .updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                CommonUtils.showToast("Profile Approved");
                                sendNotification(model.getPhone(), "Profile Approved", "Your Profile is approved");

                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void rejectProfile(User model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserList.this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to Reject profile? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.child("Users").child(model.getPhone())
                        .child("rejected").setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                CommonUtils.showToast("Profile Rejected");
                                sendNotification(model.getPhone(), "Profile Rejected", "Your Profile is Rejected");

                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void sendNotification(String phone, String payment_approved, String your_payment_is_approved) {
        mDatabase.child("Users").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.getFcmKey() != null) {
                        NotificationAsync notificationAsync = new NotificationAsync(UserList.this);
                        String NotificationTitle = payment_approved;
                        String NotificationMessage = your_payment_is_approved;
                        notificationAsync.execute(
                                "ali",
                                user.getFcmKey(),
                                NotificationTitle,
                                NotificationMessage,
                                "",
                                "profile");
                        String key = "" + System.currentTimeMillis();
                        NotificationModel model = new NotificationModel(key, NotificationTitle,
                                NotificationMessage, "profile", "https://icon-library.com/images/admin-icon-png/admin-icon-png-12.jpg", "admin", System.currentTimeMillis());
                        mDatabase.child("Notifications").child(user.getPhone()).child(key).setValue(model);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getDatFromDB() {
        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User model = snapshot.getValue(User.class);
                        if (model != null) {
                            itemList.add(model);
                        }
                    }
                    userCount.setText("Users: " + itemList.size());
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


    @Override
    public void onSent() {

    }
}