package com.appsinventiv.noorenikahadmin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appsinventiv.noorenikahadmin.Models.User;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.appsinventiv.noorenikahadmin.Utils.Constants;
import com.appsinventiv.noorenikahadmin.Utils.NotificationAsync;
import com.appsinventiv.noorenikahadmin.Utils.NotificationInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity implements NotificationInterface {

    Button send;
    EditText message;
    EditText title;
    ProgressBar progress;
    int count = 0;
    private DatabaseReference mDatabase;
    TextView counter;
    private List<String> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
            this.setTitle("Send Notification");

        }

        send = findViewById(R.id.send);
        progress = findViewById(R.id.progress);
        title = findViewById(R.id.title);
        counter = findViewById(R.id.counter);
        message = findViewById(R.id.message);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().length() == 0) {
                    title.setError("Enter Title");
                } else if (message.getText().length() == 0) {
                    message.setError("Enter message");
                } else {
                    CommonUtils.showToast("Sending notifications");
                    sendNotifcations();
                }

            }
        });


        getUsersFromDb();

    }

    private void getUsersFromDb() {
        mDatabase = Constants.M_DATABASE;
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getFcmKey() != null) {
                        userList.add(user.getFcmKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendNotifcations() {
        for (String fcm : userList) {

            NotificationAsync notificationAsync = new NotificationAsync(NotificationsActivity.this);
            String NotificationTitle = title.getText().toString();
            String NotificationMessage = message.getText().toString();
            notificationAsync.execute(
                    "ali",
                    fcm,
                    NotificationTitle,
                    NotificationMessage,
                    "admin",
                    "marketing");
        }
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