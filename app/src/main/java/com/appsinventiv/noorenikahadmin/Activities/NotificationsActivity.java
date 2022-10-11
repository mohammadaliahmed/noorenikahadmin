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

import com.appsinventiv.noorenikahadmin.Models.Data;
import com.appsinventiv.noorenikahadmin.Models.SendNotificationModel;
import com.appsinventiv.noorenikahadmin.Models.User;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.AppConfig;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.appsinventiv.noorenikahadmin.Utils.Constants;
import com.appsinventiv.noorenikahadmin.Utils.NotificationAsync;
import com.appsinventiv.noorenikahadmin.Utils.NotificationInterface;
import com.appsinventiv.noorenikahadmin.Utils.SharedPrefs;
import com.appsinventiv.noorenikahadmin.Utils.UserClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                    if (userList.size() > 0) {
                        CommonUtils.showToast("Sending notifications");
                        sendNotifcations();
                    } else {
                        CommonUtils.showToast("Please wait. Preparing to send notifications");
                    }
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
        progress.setVisibility(View.VISIBLE);
        Data data = new Data(title.getText().toString(), message.getText().toString(),
                "admin", "marketing");
        SendNotificationModel model = new SendNotificationModel(userList.get(count),
                data);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        Call<ResponseBody> call = getResponse.sendNotification(model, AppConfig.key);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                count++;
                if (count < userList.size()) {
                    counter.setText("Sent to: " + count + "/" + userList.size());
                    sendNotifcations();
                } else {
                    CommonUtils.showToast("Notification sent to all");
                    progress.setVisibility(View.GONE);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


//        for (String fcm : userList) {
//
//            NotificationAsync notificationAsync = new NotificationAsync(NotificationsActivity.this);
//            String NotificationTitle = title.getText().toString();
//            String NotificationMessage = message.getText().toString();
//            notificationAsync.execute(
//                    "ali",
//                    fcm,
//                    NotificationTitle,
//                    NotificationMessage,
//                    "admin",
//                    "marketing");
//        }


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