package com.appsinventiv.noorenikahadmin.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.appsinventiv.noorenikahadmin.Models.Data;
import com.appsinventiv.noorenikahadmin.Models.NewUserModel;
import com.appsinventiv.noorenikahadmin.Models.NotificationModel;
import com.appsinventiv.noorenikahadmin.Models.PromotionBanner;
import com.appsinventiv.noorenikahadmin.Models.SendNotificationModel;
import com.appsinventiv.noorenikahadmin.Models.User;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.AppConfig;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.appsinventiv.noorenikahadmin.Utils.CompressImage;
import com.appsinventiv.noorenikahadmin.Utils.Constants;
import com.appsinventiv.noorenikahadmin.Utils.ForegroundService;
import com.appsinventiv.noorenikahadmin.Utils.NotificationAsync;
import com.appsinventiv.noorenikahadmin.Utils.SharedPrefs;
import com.appsinventiv.noorenikahadmin.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
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
    ImageView picImage;
    private ArrayList<String> mSelected;
    private String imageUrl;
    private String livePicPath="";

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
        picImage = findViewById(R.id.picImage);
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
                   if(imageUrl!=null){
                       uploadImg();
                   }else{
                       startSe();
                   }
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
        picImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Options options = Options.init()
                        .setRequestCode(23)                                           //Request code for activity results
                        .setCount(1)
                        .setExcludeVideos(true)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                        ;                                       //Custom Path For media Storage

                Pix.start(NotificationsActivity.this, options);
            }
        });


    }

    private void startSe() {
        Intent serviceIntent = new Intent(NotificationsActivity.this, ForegroundService.class);
        serviceIntent.putExtra("notificationTitle", title.getText().toString());
        serviceIntent.putExtra("notificationMessage", message.getText().toString());
        serviceIntent.putExtra("notificationImage", livePicPath);
        ContextCompat.startForegroundService(NotificationsActivity.this, serviceIntent);
    }

    private void uploadImg() {
        CommonUtils.showToast("Image uploading");
        progress.setVisibility(View.VISIBLE);
        try {
            String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

            Uri file = Uri.fromFile(new File(imageUrl));

            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

            final StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

            riversRef.putFile(file)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get a URL to the uploaded content
                        riversRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        CommonUtils.showToast("Image uploaded. Sending notification now");
                                        progress.setVisibility(View.GONE);
                                         livePicPath = uri.toString();
                                         startSe();
                                    }
                                });


                            }
                        });


                    })
                    .addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        // ...

                        CommonUtils.showToast("There was some error uploading pic");


                    });
        } catch (Exception e) {
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 23 && data != null) {
            mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            CompressImage compressImage = new CompressImage(NotificationsActivity.this);
            imageUrl = compressImage.compressImage("" + mSelected.get(0));
            Glide.with(NotificationsActivity.this).load(mSelected.get(0)).into(picImage);

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


}