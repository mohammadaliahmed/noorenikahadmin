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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.appsinventiv.noorenikahadmin.Models.PaymentsModel;
import com.appsinventiv.noorenikahadmin.Models.PromotionBanner;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.appsinventiv.noorenikahadmin.Utils.CompressImage;
import com.appsinventiv.noorenikahadmin.Utils.Constants;
import com.appsinventiv.noorenikahadmin.Utils.SharedPrefs;
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


public class AddPromotionBanner extends AppCompatActivity {
    ImageView image, uploadedImage;
    EditText url, uploadUrl;
    Button save;
    private ArrayList<String> mSelected = new ArrayList<>();
    private String imageUrl;
    ProgressBar progress;
    DatabaseReference mDatabase;
    CardView uploadedLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promtional_banner);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mDatabase = Constants.M_DATABASE;
        this.setTitle("Add Promotion Banner");
        save = findViewById(R.id.save);
        uploadedLayout = findViewById(R.id.uploadedLayout);
        uploadedImage = findViewById(R.id.uploadedImage);
        uploadUrl = findViewById(R.id.uploadUrl);
        progress = findViewById(R.id.progress);
        url = findViewById(R.id.url);
        image = findViewById(R.id.image);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url.getText().length() == 0) {
                    url.setError("Enter Url");
                } else if (mSelected.size() == 0) {
                    CommonUtils.showToast("Please pick image");
                } else {
                    uploadImg();
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendNotification();

                Options options = Options.init()
                        .setRequestCode(23)                                           //Request code for activity results
                        .setCount(1)
                        .setExcludeVideos(true)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                        ;                                       //Custom Path For media Storage

                Pix.start(AddPromotionBanner.this, options);
            }
        });
        getDataFromDb();

    }

    private void getDataFromDb() {
        mDatabase.child("PromotionalBanner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    uploadedLayout.setVisibility(View.VISIBLE);
                    PromotionBanner promotionBanner = dataSnapshot.getValue(PromotionBanner.class);
                    Glide.with(AddPromotionBanner.this).load(promotionBanner.getImgUrl()).into(uploadedImage);
                    uploadUrl.setText(promotionBanner.getUrl());

                } else {
                    uploadedLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadImg() {
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
                                        PromotionBanner model = new PromotionBanner(url.getText().toString(), uri.toString());
                                        mDatabase.child("PromotionalBanner").setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                CommonUtils.showToast("Uploaded");
                                                progress.setVisibility(View.GONE);
                                            }
                                        });
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

            CompressImage compressImage = new CompressImage(AddPromotionBanner.this);
            imageUrl = compressImage.compressImage("" + mSelected.get(0));
            Glide.with(AddPromotionBanner.this).load(mSelected.get(0)).into(image);

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