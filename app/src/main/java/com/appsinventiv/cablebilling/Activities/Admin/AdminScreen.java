package com.appsinventiv.cablebilling.Activities.Admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.appsinventiv.cablebilling.Activities.Agent.ParchiSettings;
import com.appsinventiv.cablebilling.Activities.LoginActivity;
import com.appsinventiv.cablebilling.Activities.MainActivity;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminScreen extends AppCompatActivity {
    CardView customers, agents, settings;
    TextView recoveryToday, recoveryMonth, toRecover;

    DatabaseReference mDatabase;
    ArrayList<BillModel> dayBillList = new ArrayList<>();
    ArrayList<BillModel> monthBill = new ArrayList<>();
    private ArrayList<UserModel> userList = new ArrayList<>();
    private int monttotal=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);
        recoveryToday = findViewById(R.id.recoveryToday);
        recoveryMonth = findViewById(R.id.recoveryMonth);
        toRecover = findViewById(R.id.toRecover);
        settings = findViewById(R.id.settings);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.setTitle("Administration");

        customers = findViewById(R.id.customers);
        agents = findViewById(R.id.agents);

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminScreen.this, MainActivity.class));
            }
        });
        agents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminScreen.this, ListOfAgents.class));
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminScreen.this, ParchiSettings.class));
            }
        });
        getRecoveryDataFromServer();
        getParchiModelFromServer();
        getCustomersDataFromDb();
    }

    private void getCustomersDataFromDb() {
        mDatabase.child("Customers").child(SharedPrefs.getLoggedInAsWhichAdmin()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel model = snapshot.getValue(UserModel.class);
                        if (model != null && model.getName() != null) {
                            userList.add(model);

                        }

                    }
                    calculateMonthTotal();


                } else {

                    CommonUtils.showToast("No Data");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getParchiModelFromServer() {
        mDatabase.child("ParchiDetails").child(SharedPrefs.getLoggedInAsWhichAdmin()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BillModel model = snapshot.getValue(BillModel.class);
                        if (model != null) {
                            if (model.getAdmin().equalsIgnoreCase(SharedPrefs.getLoggedInAsWhichAdmin())) {
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
                if (dayBillList.size() > 0) {
                    calculateTotal();
                }
                if (monthBill.size() > 0) {
                    calculateMonthTotal();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void calculateRecovery() {
        if(userList.size()>0 && monthBill.size()>0) {
            int toRecoverTotal = 0;
            for (UserModel model : userList) {
                toRecoverTotal = toRecoverTotal + model.getBill();
            }
            int abc = toRecoverTotal - monttotal;
            toRecover.setText("To recover: " + abc);
        }
    }

    private void calculateTotal() {
        int total = 0;
        for (BillModel model : dayBillList) {
            total = total + model.getBillAmount();

        }
        recoveryToday.setText("Recovery Today: \nRs " + total);
    }

    private void calculateMonthTotal() {
         monttotal = 0;
        for (BillModel model : monthBill) {
            monttotal = monttotal + model.getBillAmount();

        }
        calculateRecovery();

        recoveryMonth.setText("This Month: \nRs " + monttotal);
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
            Intent i = new Intent(AdminScreen.this, Splash.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
