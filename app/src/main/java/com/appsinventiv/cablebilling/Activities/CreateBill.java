package com.appsinventiv.cablebilling.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsinventiv.cablebilling.Models.BillModel;
import com.appsinventiv.cablebilling.R;
import com.appsinventiv.cablebilling.Utils.CommonUtils;
import com.appsinventiv.cablebilling.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.ByteArrayOutputStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class CreateBill extends AppCompatActivity {


    TextView userDetails, billAmount, date, number;

    String name, phone, address, packageTypeText;
    int bill;
    Button send;
    RelativeLayout billLayout;
    private Uri ui;
    private Intent whatsappIntent;

    DatabaseReference mDatabase;
    private String idddd;
    TextView recievedByText, packageType;
    TextView title, addressTv;
    ImageView image;
    private String admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Create Bill");
        getPermissions();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        title = findViewById(R.id.title);
        addressTv = findViewById(R.id.address);
        image = findViewById(R.id.image);


        title.setText(SharedPrefs.getTitle());
        addressTv.setText(SharedPrefs.getAddress());
        Glide.with(this).load(SharedPrefs.getLogoUrl()).into(image);


        userDetails = findViewById(R.id.userDetails);
        billLayout = findViewById(R.id.billLayout);
        packageType = findViewById(R.id.packageType);
        billAmount = findViewById(R.id.billAmount);
        number = findViewById(R.id.number);
        recievedByText = findViewById(R.id.recievedByText);
        date = findViewById(R.id.date);
        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.startsWith("+923")) {
                } else if (phone.startsWith("03")) {
                    phone = "92" + phone.substring(1, phone.length());
                } else if (phone.startsWith("+")) {

                }
                viewToBitmap(billLayout, billLayout.getWidth(), billLayout.getHeight());


            }
        });


        bill = getIntent().getIntExtra("bill", 0);
        admin = getIntent().getStringExtra("admin");
        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        address = getIntent().getStringExtra("address");
        packageTypeText = getIntent().getStringExtra("packageTypeText");
        idddd = CommonUtils.getFormattedDateOnl(System.currentTimeMillis()) + phone;

        recievedByText.setText(SharedPrefs.getParchiName());


//        userDetails.setText("Name: " + name + "\nPhone: " + phone + "\nAddr: " + address);
        number.setText(idddd);
        packageType.setText(packageTypeText);
        userDetails.setText(name);
        date.setText(CommonUtils.getFormattedDateOnly(System.currentTimeMillis()));
        billAmount.setText(bill + "/-");

    }

    public Bitmap viewToBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
//        saveImage(bitmap, status_text.getText().toString().substring(0, 5));
        ui = getImageUri(bitmap);
        whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra("jid", phone + "@s.whatsapp.net");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Here Is Your Bill");
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, ui);
        whatsappIntent.setType("image/jpeg");
        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(whatsappIntent);
            updateBillToDB();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(CreateBill.this, "Whatsapp have not been installed", Toast.LENGTH_SHORT).show();
        }


        return bitmap;
    }

    private void updateBillToDB() {
        BillModel model = new BillModel(idddd, bill, System.currentTimeMillis(), name, phone, address,
                SharedPrefs.getAgent().getPhone(), phone,admin);
        mDatabase.child("Bills").child(idddd).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
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
