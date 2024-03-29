package com.appsinventiv.noorenikahadmin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.appsinventiv.noorenikahadmin.Models.PromotionBanner;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    CardView payments, users, payouts, notifications, promotionBanner, posts, matchMakers;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Dashboard");
        getSupportActionBar().setElevation(0);
        users = findViewById(R.id.users);
        posts = findViewById(R.id.posts);
        matchMakers = findViewById(R.id.matchMakers);
        notifications = findViewById(R.id.notifications);
        promotionBanner = findViewById(R.id.promotionBanner);
        payouts = findViewById(R.id.payouts);
        payments = findViewById(R.id.payments);
        mDatabase = FirebaseDatabase.getInstance("https://noorenikah-default-rtdb.firebaseio.com/").getReference();

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostLists.class));
//                startActivity(new Intent(MainActivity.this, MarkChatsAsRead.class));

            }
        });
        matchMakers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListOfMatchMakers.class));

            }
        });

        payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PaymentsHistory.class));
            }
        });
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserList.class));
            }
        });
        promotionBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddPromotionBanner.class));
            }
        });
        payouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Payouts.class));
            }
        });
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationsActivity.class));

            }
        });
        updateFcmKey();
    }

    private void updateFcmKey() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String token = task.getResult();
                SharedPrefs.setFcmKey(token);
                mDatabase.child("Admin").child("fcmKey").setValue(token);

            }
        });

    }

}