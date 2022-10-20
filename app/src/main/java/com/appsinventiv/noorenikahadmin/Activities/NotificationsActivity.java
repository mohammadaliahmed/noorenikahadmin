package com.appsinventiv.noorenikahadmin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.appsinventiv.noorenikahadmin.Models.Data;
import com.appsinventiv.noorenikahadmin.Models.NewUserModel;
import com.appsinventiv.noorenikahadmin.Models.NotificationModel;
import com.appsinventiv.noorenikahadmin.Models.SendNotificationModel;
import com.appsinventiv.noorenikahadmin.Models.User;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.AppConfig;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.appsinventiv.noorenikahadmin.Utils.Constants;
import com.appsinventiv.noorenikahadmin.Utils.ForegroundService;
import com.appsinventiv.noorenikahadmin.Utils.NotificationAsync;
import com.appsinventiv.noorenikahadmin.Utils.SharedPrefs;
import com.appsinventiv.noorenikahadmin.Utils.UserClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

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
        mDatabase = Constants.M_DATABASE;
        Button btnStartService = findViewById(R.id.start);
        Button btnStopService = findViewById(R.id.stop);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().length() == 0) {
                    title.setError("Enter Title");
                } else if (message.getText().length() == 0) {
                    message.setError("Enter message");
                } else {
                    Intent serviceIntent = new Intent(NotificationsActivity.this, ForegroundService.class);
                    serviceIntent.putExtra("notificationTitle", title.getText().toString());
                    serviceIntent.putExtra("notificationMessage", message.getText().toString());
                    ContextCompat.startForegroundService(NotificationsActivity.this, serviceIntent);
                }
            }
        });
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(NotificationsActivity.this, ForegroundService.class);
                stopService(serviceIntent);
            }
        });

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
//                    String fcm="fzY8TW
//                    tCQAeEky_MFfSuj9:APA91bE3akMMM_FTc_b8J2mWThDsJEYm2X1NY5oGf4nA50SwYerfMOV1lC4Wznwwh1sa4IZWIniLRszADXKG-07CcpOonvCJFOUASCmepIEr-smjmntesC6DPOecpajkhCau_ZVs8QXm";
//
//                    NotificationAsync notificationAsync = new NotificationAsync(NotificationsActivity.this);
//                    String NotificationTitle = title.getText().toString();
//                    String NotificationMessage = message.getText().toString();
//                    notificationAsync.execute(
//                            "ali",
//                            fcm,
//                            NotificationTitle,
//                            NotificationMessage,
//                            "admin",
//                            "marketing");
//
                }

            }
        });
//        getUsersFromDb();

    }

    private void getUsersFromDb() {
        for(int i=0;i<635;i++){
            userList.add("dGk8WYpHR-ujIzdUU-9nHF:APA91bHtt6sp8dbY6S5XLRrj6F-maN9_AcYClpWU8TTXWE57nFMptR28xEq5RRgcvYS6mkDulEfCrc65sTzri9A_O-MKLPurWjyBksqv2zj6hB1K5YAcLDCSMx9mvwRsiZ8as6bQt_i1");
        }
//        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    NewUserModel user = snapshot.getValue(NewUserModel.class);
//                    if (user != null && user.getFcmKey() != null) {
//                        userList.add(user.getFcmKey());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


    }

    private void sendNotifcations() {
        send.setEnabled(false);
        try {
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
        }catch (Exception e){

        }


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


}