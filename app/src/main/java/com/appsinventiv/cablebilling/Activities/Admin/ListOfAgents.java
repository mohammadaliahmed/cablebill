package com.appsinventiv.cablebilling.Activities.Admin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.appsinventiv.cablebilling.Activities.AddCustomer;
import com.appsinventiv.cablebilling.Activities.Agent.AgentScreen;
import com.appsinventiv.cablebilling.Activities.LoginActivity;
import com.appsinventiv.cablebilling.Activities.MainActivity;
import com.appsinventiv.cablebilling.Adapters.AgentListAdapter;
import com.appsinventiv.cablebilling.Models.AgentModel;
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
import java.util.Queue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListOfAgents extends AppCompatActivity {
    RecyclerView recyclerview;
    AgentListAdapter adapter;
    private ArrayList<AgentModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_agents);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("List of agents");

        recyclerview = findViewById(R.id.recyclerview);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new AgentListAdapter(this, itemList, new AgentListAdapter.AgentListCallbacks() {
            @Override
            public void onAgentClicked(AgentModel model) {
                showData(model);
            }
        });
        recyclerview.setAdapter(adapter);
        getDataFromServer();


    }

    private void showData(final AgentModel agent) {
        mDatabase.child("Bills").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<BillModel> dayBillList = new ArrayList<>();
                ArrayList<BillModel> monthBill = new ArrayList<>();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BillModel model = snapshot.getValue(BillModel.class);
                        if (model != null) {
                            if (agent.getPhone().equalsIgnoreCase(model.getBillById())) {
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
                final Dialog dialog = new Dialog(ListOfAgents.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View layout = layoutInflater.inflate(R.layout.alert_dialog_curved, null);

                dialog.setContentView(layout);

                TextView recoveryToday = layout.findViewById(R.id.recoveryToday);
                TextView recoveryMonth = layout.findViewById(R.id.recoveryMonth);
                TextView title = layout.findViewById(R.id.title);
                title.setText("Recovery By: " + agent.getName());
                if (dayBillList.size() > 0) {
//                    calculateTotal(dayBillList);
                    recoveryToday.setText("Recovery Today Rs: " + calculateMonthTotal(monthBill));

                }
                if (monthBill.size() > 0) {
                    recoveryMonth.setText("Recovery Month Rs: " + calculateMonthTotal(monthBill));
                }


                dialog.show();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private int calculateMonthTotal(ArrayList<BillModel> monthBill) {
        int total = 0;
        for (BillModel model : monthBill) {
            total = total + model.getBillAmount();

        }
//        recoveryMonth.setText("This Month: \nRs " + total);
        return total;
    }

    private int calculateTotal(ArrayList<BillModel> dayBillList) {
        int total = 0;
        for (BillModel model : dayBillList) {
            total = total + model.getBillAmount();

        }
//        recoveryToday.setText("Recovery Today: \nRs " + total);
        return total;
    }

    private void getDataFromServer() {
        mDatabase.child("Agents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AgentModel model = snapshot.getValue(AgentModel.class);
                        if (model != null) {
                            if(model.getAdmin().equalsIgnoreCase(SharedPrefs.getLoggedInAsWhichAdmin())) {
                                itemList.add(model);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    itemList.clear();
                    adapter.notifyDataSetChanged();
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

        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        if (id == R.id.action_add) {
            startActivity(new Intent(ListOfAgents.this, AddAgent.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


