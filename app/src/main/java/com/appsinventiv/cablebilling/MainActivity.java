package com.appsinventiv.cablebilling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.appsinventiv.cablebilling.Adapters.UserListAdapter;
import com.appsinventiv.cablebilling.Models.UserModel;
import com.appsinventiv.cablebilling.Utils.CommonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerview;
    UserListAdapter adapter;
    private ArrayList<UserModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;
    EditText search;
    RelativeLayout wholeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wholeLayout = findViewById(R.id.wholeLayout);
        search = findViewById(R.id.search);
        recyclerview = findViewById(R.id.recyclerview);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());
            }
        });
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new UserListAdapter(this, itemList, new UserListAdapter.UserListCallbacks() {
            @Override
            public void onGenerateBill(UserModel model) {
//                CommonUtils.showToast(model.getName());
                Intent i = new Intent(MainActivity.this, CreateBill.class);
                i.putExtra("name", model.getName());
                i.putExtra("phone", model.getPhone());
                i.putExtra("address", model.getAddress());
                i.putExtra("bill", model.getBill());
                startActivity(i);
            }
        });
        recyclerview.setAdapter(adapter);
        getDataFromServer();

    }

    private void getDataFromServer() {
        mDatabase.child("Customers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel model = snapshot.getValue(UserModel.class);
                        if (model != null && model.getName() != null) {
                            itemList.add(model);

                        }

                    }

                    Collections.sort(itemList, new Comparator<UserModel>() {
                        @Override
                        public int compare(UserModel country1, UserModel country2) {
                            return country1.getName().compareToIgnoreCase(country2.getName());
                        }
                    });
                    wholeLayout.setVisibility(View.GONE);
                    adapter.updateList(itemList);
                } else {
                    itemList.clear();
                    wholeLayout.setVisibility(View.GONE);
                    CommonUtils.showToast("No Data");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, AddCustomer.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
