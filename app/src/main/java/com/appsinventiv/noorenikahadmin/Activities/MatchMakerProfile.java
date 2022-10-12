package com.appsinventiv.noorenikahadmin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.noorenikahadmin.Adapters.CreatedProfilesAdapter;
import com.appsinventiv.noorenikahadmin.Models.MatchMakerModel;
import com.appsinventiv.noorenikahadmin.Models.NewUserModel;
import com.appsinventiv.noorenikahadmin.Models.NotificationModel;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.appsinventiv.noorenikahadmin.Utils.Constants;
import com.appsinventiv.noorenikahadmin.Utils.NotificationAsync;
import com.appsinventiv.noorenikahadmin.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchMakerProfile extends AppCompatActivity {

    DatabaseReference mDatabase;
    ImageView image;
    TextView mbName;
    RecyclerView recyclerView;
    private List<NewUserModel> itemList = new ArrayList<>();
    CreatedProfilesAdapter adapter;
    TextView pendingApproval;
    String matchMakerId;
    Button approve, pending;
    private String fcmKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_maker_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Match Maker Profile");
        mDatabase = Constants.M_DATABASE;
        matchMakerId = getIntent().getStringExtra("matchMakerId");
        image = findViewById(R.id.image);
        pending = findViewById(R.id.pending);
        approve = findViewById(R.id.approve);
        pendingApproval = findViewById(R.id.pendingApproval);
        recyclerView = findViewById(R.id.recycler);
        mbName = findViewById(R.id.mbName);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new CreatedProfilesAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("MatchMakers").child(matchMakerId).child("approved").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        CommonUtils.showToast("Profile Approved");
                        pendingApproval.setVisibility(View.GONE);
                        sendNotification();
                    }
                });
            }
        });
        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("MatchMakers").child(matchMakerId).child("approved").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        CommonUtils.showToast("Profile is pending");
                        pendingApproval.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


        getMbData();
        getMatchMakerData();


    }

    private void sendNotification() {
        NotificationAsync notificationAsync = new NotificationAsync(this);
        String NotificationTitle = "Match maker profile";
        String NotificationMessage = "Your match maker profile is approved";
        notificationAsync.execute(
                "ali",
                fcmKey,
                NotificationTitle,
                NotificationMessage,
                "",
                "matchmakerApproved");
        String key = "" + System.currentTimeMillis();
        NotificationModel model = new NotificationModel(key, NotificationTitle,
                NotificationMessage, "matchmakerApproved",
                "https://icon-library.com/images/admin-icon-png/admin-icon-png-12.jpg",
                "admin", System.currentTimeMillis());
        mDatabase.child("Notifications").child(matchMakerId).child(key).setValue(model);

    }

    private void getMatchMakerData() {
        mDatabase.child("Users").child(matchMakerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NewUserModel model = dataSnapshot.getValue(NewUserModel.class);
                fcmKey = model.getFcmKey();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMbData() {
        mDatabase.child("MatchMakers").child(matchMakerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                MatchMakerModel user = dataSnapshot.getValue(MatchMakerModel.class);
                try {
                    Glide.with(MatchMakerProfile.this).load(user.getPicUrl()).into(image);
                } catch (Exception e) {

                }
                if (user.isApproved()) {
                    pendingApproval.setVisibility(View.GONE);

                } else {
                    pendingApproval.setVisibility(View.VISIBLE);

                }
                mbName.setText(user.getMbName());
                if (user.getProfilesCreated() != null) {
                    for (Map.Entry<String, Object> entry : user.getProfilesCreated().entrySet()) {
                        String key = entry.getKey();
                        getUserFromDB(key);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getUserFromDB(String key) {
        mDatabase.child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NewUserModel userModel = dataSnapshot.getValue(NewUserModel.class);
                if (userModel != null) {
                    itemList.add(userModel);
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