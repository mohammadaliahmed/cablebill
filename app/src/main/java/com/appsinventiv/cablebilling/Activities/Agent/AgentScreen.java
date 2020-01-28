package com.appsinventiv.cablebilling.Activities.Agent;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.appsinventiv.cablebilling.Activities.Splash;
import com.appsinventiv.cablebilling.Models.BillModel;
import com.appsinventiv.cablebilling.Models.ParchiModel;
import com.appsinventiv.cablebilling.Models.UserModel;
import com.appsinventiv.cablebilling.R;
import com.appsinventiv.cablebilling.Utils.CommonUtils;
import com.appsinventiv.cablebilling.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

public class AgentScreen extends AppCompatActivity {


    CardView customers, settings;
    private ArrayList<String> itemList = new ArrayList<>();
    private ArrayList<UserModel> customersList = new ArrayList<>();
    DatabaseReference mDatabase;
    TextView recoveryToday, recoveryUsers;
    ArrayList<BillModel> dayBillList = new ArrayList<>();
    ArrayList<BillModel> monthBill = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_screen);
        recoveryToday = findViewById(R.id.recoveryToday);
        recoveryUsers = findViewById(R.id.recoveryUsers);
        settings = findViewById(R.id.settings);
        customers = findViewById(R.id.customers);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgentScreen.this, BillFromAgent.class));
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgentScreen.this, ParchiSettings.class));
            }
        });

        getPermissions();
        getAllContactsFromServer();

        getRecoveryDataFromServer();
        getParchiModelFromServer();
        this.setTitle("Welcome, " + SharedPrefs.getAgent().getName());


    }

    private void getParchiModelFromServer() {
        mDatabase.child("ParchiDetails").child(SharedPrefs.getAgent().getAdmin()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ParchiModel model = dataSnapshot.getValue(ParchiModel.class);
                    if (model != null) {
                        SharedPrefs.setTitle(model.getTitle());
                        SharedPrefs.setLogoUrl(model.getPicUrl());
                        SharedPrefs.setAddress(model.getAddress());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRecoveryDataFromServer() {
        mDatabase.child("Bills").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    dayBillList.clear();
                    monthBill.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BillModel model = snapshot.getValue(BillModel.class);
                        if (model != null) {
                            if (model.getBillById() != null) {
                                if (model.getAdmin().equalsIgnoreCase(SharedPrefs.getAgent().getAdmin())) {
                                    if (model.getBillById().equalsIgnoreCase(SharedPrefs.getAgent().getPhone())) {
                                        if (model.getId().contains(CommonUtils.getFormattedDateOnl(System.currentTimeMillis()))) {

                                            dayBillList.add(model);
                                        }
                                        if (model.getId().contains(CommonUtils.getYearMonth(System.currentTimeMillis()))) {

                                            monthBill.add(model);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (dayBillList.size() > 0) {
                        calculateTotal();
                    }
                    if (monthBill.size() > 0) {
//                        calculateMonthTotal();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void calculateTotal() {
        int total = 0;
        for (BillModel model : dayBillList) {
            total = total + model.getBillAmount();

        }
        recoveryUsers.setText("Recovery Users: " + dayBillList.size());
        recoveryToday.setText("Recovery Today: \nRs " + total);
    }

//    private void calculateMonthTotal() {
//        int total = 0;
//        for (BillModel model : monthBill) {
//            total = total + model.getBillAmount();
//
//        }
////        recoveryMonth.setText("This Month: \nRs " + total);
//    }

    private void getAllContactsFromServer() {
        mDatabase.child("Customers").child(SharedPrefs.getAgent().getAdmin()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel model = snapshot.getValue(UserModel.class);
                        if (model != null) {
//                            customersList.add(model);
                            String phone = model.getPhone().replace(" ", "").replace("-", "");
                            if (!itemList.contains(phone)) {
                                addContact(model.getName(), phone);
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readAllContacts() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replaceAll("[()\\s-]+", "");

            if (phoneNumber.length() > 8) {

                itemList.add(phoneNumber);
            }

        }
        phones.close();

    }

    private void addContact(String name, String phone) {
        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // first and last names
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                .build());

        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());


        try {
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPermissions() {


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CONTACTS

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
                    readAllContacts();
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }
        if (item.getItemId() == R.id.action_logout) {

            SharedPrefs.logout();
            Intent i = new Intent(AgentScreen.this, Splash.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
