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