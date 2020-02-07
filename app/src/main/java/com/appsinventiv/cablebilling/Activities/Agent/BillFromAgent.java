package com.appsinventiv.cablebilling.Activities.Agent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.appsinventiv.cablebilling.Activities.AddCustomer;
import com.appsinventiv.cablebilling.Activities.Admin.AdminScreen;
import com.appsinventiv.cablebilling.Activities.CreateBill;
import com.appsinventiv.cablebilling.Activities.LoginActivity;
import com.appsinventiv.cablebilling.Activities.MainActivity;
import com.appsinventiv.cablebilling.Adapters.UserListAdapter;
import com.appsinventiv.cablebilling.Models.BillModel;
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
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BillFromAgent extends AppCompatActivity {
    RecyclerView recyclerview;
    UserListAdapter adapter;
    private ArrayList<UserModel> itemList = new ArrayList<>();
    ArrayList<String> sentBillList = new ArrayList<>();
    DatabaseReference mDatabase;
    EditText search, dueDate;
    RelativeLayout wholeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_from_agent);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Customers");
        wholeLayout = findViewById(R.id.wholeLayout);
        search = findViewById(R.id.search);
        dueDate = findViewById(R.id.dueDate);
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
        dueDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filterDate(s.toString());
            }
        });
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new UserListAdapter(this, itemList, new UserListAdapter.UserListCallbacks() {
            @Override
            public void onGenerateBill(UserModel model) {
//                CommonUtils.showToast(model.getName());
                Intent i = new Intent(BillFromAgent.this, CreateBill.class);
                i.putExtra("name", model.getName());
                i.putExtra("phone", model.getPhone());
                i.putExtra("address", model.getAddress());
                i.putExtra("admin", SharedPrefs.getAgent().getAdmin());
                i.putExtra("bill", model.getBill());
                i.putExtra("packageTypeText", model.getPackageType());
                startActivity(i);
            }
        });
        recyclerview.setAdapter(adapter);
        adapter.setCanClick(false);
        getDataFromServer();
        getRecoveryDataFromServer();

    }

    private void getDataFromServer() {
        mDatabase.child("Customers").child(SharedPrefs.getAgent().getAdmin()).addValueEventListener(new ValueEventListener() {
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

    private void getRecoveryDataFromServer() {
        mDatabase.child("Bills").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    sentBillList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BillModel model = snapshot.getValue(BillModel.class);
                        if (model != null) {
                            if (model.getId().contains(CommonUtils.getYearMonth(System.currentTimeMillis()))) {
                                String billto = model.getBillTo();
                                if (billto != null) {
                                    if (billto.startsWith("92")) {
                                        billto = billto.replace("92", "0");
                                    }
                                    if (billto.startsWith("+92")) {
                                        billto = billto.replace("+92", "0");
                                    }
                                    sentBillList.add(billto);
                                }
                            }
                        }
                    }
                    adapter.setSendToList(sentBillList);

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

//        if (id == R.id.action_settings) {
//            startActivity(new Intent(MainActivity.this, AddCustomer.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
