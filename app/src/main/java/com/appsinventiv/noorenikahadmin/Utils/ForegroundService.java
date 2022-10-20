package com.appsinventiv.noorenikahadmin.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.appsinventiv.noorenikahadmin.Activities.MainActivity;
import com.appsinventiv.noorenikahadmin.Activities.NotificationsActivity;
import com.appsinventiv.noorenikahadmin.Models.Data;
import com.appsinventiv.noorenikahadmin.Models.NewUserModel;
import com.appsinventiv.noorenikahadmin.Models.SendNotificationModel;
import com.appsinventiv.noorenikahadmin.R;
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

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private List<String> userList = new ArrayList<>();
    private Notification notification;
    DatabaseReference mDatabase;
    String notificationMessage, notificationTitle;
    int count = 0;
    private static final int NOTIF_ID = 1;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationTitle = intent.getStringExtra("notificationTitle");
        notificationMessage = intent.getStringExtra("notificationMessage");
        createNotificationChannel();

        startForeground(NOTIF_ID, getMyActivityNotification());

        //do heavy work on a background thread

        //stopSelf();
        mDatabase = Constants.M_DATABASE;
        getDataOfUsers();

        return START_NOT_STICKY;
    }

    private Notification getMyActivityNotification() {
        // The PendingIntent to launch our activity if the user selects
        // this notification
        CharSequence title = "Service Running";

        Intent notificationIntent = new Intent(this, NotificationsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Sending Notification " + count + "/" + userList.size())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

    }


    private void getDataOfUsers() {
//        for (int i = 0; i < 10; i++) {
//            userList.add("dGk8WYpHR-ujIzdUU-9nHF:APA91bHtt6sp8dbY6S5XLRrj6F-maN9_AcYClpWU8TTXWE57nFMptR28xEq5RRgcvYS6mkDulEfCrc65sTzri9A_O-MKLPurWjyBksqv2zj6hB1K5YAcLDCSMx9mvwRsiZ8as6bQt_i1");
//        }
//        sendNotifcations();
//
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NewUserModel user = snapshot.getValue(NewUserModel.class);
                    if (user != null && user.getFcmKey() != null) {
                        userList.add(user.getFcmKey());
                    }
                }
                if (userList.size() > 0) {
                    sendNotifcations();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendNotifcations() {
        Notification notification = getMyActivityNotification();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIF_ID, notification);
        try {
            Data data = new Data(notificationTitle, notificationMessage,
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
                        sendNotifcations();
                    } else {
                        CommonUtils.showToast("Notification sent to all");
                        stopSelf();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        } catch (Exception e) {

        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

        }
    }
}