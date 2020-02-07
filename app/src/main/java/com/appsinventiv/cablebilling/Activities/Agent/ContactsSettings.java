package com.appsinventiv.cablebilling.Activities.Agent;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.appsinventiv.cablebilling.Activities.Admin.AdminScreen;
import com.appsinventiv.cablebilling.Activities.LoginActivity;
import com.appsinventiv.cablebilling.Adapters.ContactUserListAdapter;
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
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsSettings extends AppCompatActivity {

    DatabaseReference mDatabase;
    HashMap<String, String> phoneContact = new HashMap<>();
    RecyclerView recyclerview;
    ContactUserListAdapter adapter;
    ArrayList<String> phoneList = new ArrayList<>();
    private ArrayList<UserModel> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_settings);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Contacts");


        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new ContactUserListAdapter(ContactsSettings.this, userList, phoneList);
        recyclerview.setAdapter(adapter);

        adapter.setCallbacks(new ContactUserListAdapter.ContactListCallbacks() {
            @Override
            public void onSaveContact(UserModel model) {
                addContact(model.getName(), model.getPhone());
                phoneList.add(model.getPhone());
                adapter.setContactList(phoneList);
                CommonUtils.showToast("Saved contact");

            }

            @Override
            public void onDeleteContact(UserModel model) {
                phoneList.remove(model.getPhone());
                adapter.setContactList(phoneList);
                deleteContact(model.getPhone());
                CommonUtils.showToast("Deleted");

            }
        });

        getPermissions();

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
            CommonUtils.showToast("Saved");
        } catch (Exception e) {
            CommonUtils.showToast(e.getMessage());
        }
    }


    private void getAllContactsFromServer() {
        mDatabase.child("Customers").child(SharedPrefs.getAgent().getAdmin()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel model = snapshot.getValue(UserModel.class);
                        if (model != null) {
                            String fone = model.getPhone().replace(" ", "");
                            model.setPhone(fone);
                            userList.add(model);
//                            customersList.add(model);
//                            deleteContact(AgentScreen.this,model.getPhone());

                        }
                    }
                    adapter.setItemList(userList);


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


            phoneContact.put(phoneNumber, phoneNumber);


        }
        phones.close();
        if (phoneContact.size() > 0) {
            phoneList.addAll(phoneContact.values());
            adapter.setContactList(phoneList);
            getAllContactsFromServer();

        }

    }

    public boolean deleteContact(String phone) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                    getContentResolver().delete(uri, null, null);
                    return true;


                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }

    private void getPermissions() {


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CONTACTS

        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {

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
