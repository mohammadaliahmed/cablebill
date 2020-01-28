package com.appsinventiv.cablebilling.Activities.Agent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.appsinventiv.cablebilling.Models.ParchiModel;
import com.appsinventiv.cablebilling.R;
import com.appsinventiv.cablebilling.Utils.CommonUtils;
import com.appsinventiv.cablebilling.Utils.CompressImage;
import com.appsinventiv.cablebilling.Utils.Glide4Engine;
import com.appsinventiv.cablebilling.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ParchiSettings extends AppCompatActivity {


    ImageView pickImage, image;
    EditText title, address;
    Button save;
    private List<Uri> mSelected = new ArrayList<>();
    StorageReference mStorageRef;
    DatabaseReference mDatabase;
    private String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_settings);
        getPermissions();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Create image");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef= FirebaseStorage.getInstance().getReference();

        pickImage = findViewById(R.id.pickImage);
        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        address = findViewById(R.id.address);
        save = findViewById(R.id.save);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMatisse();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().length() == 0) {
                    title.setError("Enter title");
                } else if (address.getText().length() == 0) {
                    address.setError("Enter address");
                } else if (mSelected.size() == 0) {
                    CommonUtils.showToast("Please select logo");
                } else {
                    saveDetails();
                }
            }
        });
        title.setText(SharedPrefs.getTitle());
        address.setText(SharedPrefs.getAddress());
        Glide.with(this).load(SharedPrefs.getLogoUrl()).into(image);


    }


    private void saveDetails() {

//        SharedPrefs.setLogoUrl("" + mSelected.get(0));
//        SharedPrefs.setAddress(address.getText().toString());
//        SharedPrefs.setTitle(title.getText().toString());
//        CommonUtils.showToast("Saved");
        putPictures(imageUrl);


    }

    private void initMatisse() {
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(23);
    }

    public void putPictures(String path) {
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        ;
        Uri file = Uri.fromFile(new File(path));


        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
//                        Uri downloadUrl = taskSnapshot.getre;
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri downloadUrl = urlTask.getResult();
                        mDatabase.child("ParchiDetails").child(SharedPrefs.getLoggedInAsWhichAdmin()).setValue(new ParchiModel(title.getText().toString(), address.getText().toString(), "" + downloadUrl)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Saved");
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        CommonUtils.showToast(exception.getMessage());
                        // ...
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 23 && data != null) {

            mSelected = Matisse.obtainResult(data);
            Glide.with(ParchiSettings.this).load(mSelected.get(0)).into(image);
            CompressImage compressImage = new CompressImage(this);
            imageUrl = compressImage.compressImage("" + mSelected.get(0));
        }
    }


    private void getPermissions() {


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }


        return super.onOptionsItemSelected(item);
    }

}
