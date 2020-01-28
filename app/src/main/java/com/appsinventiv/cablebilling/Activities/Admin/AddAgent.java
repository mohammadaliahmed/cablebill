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
import android.widget.Button;
import android.widget.EditText;

import com.appsinventiv.cablebilling.Activities.Agent.AgentScreen;
import com.appsinventiv.cablebilling.Activities.LoginActivity;
import com.appsinventiv.cablebilling.Models.AgentModel;
import com.appsinventiv.cablebilling.R;
import com.appsinventiv.cablebilling.Utils.CommonUtils;
import com.appsinventiv.cablebilling.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddAgent extends AppCompatActivity {

    EditText name, phone, password, address;
    Button save, delete;
    DatabaseReference mDatabase;

    String agentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_agent);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Add agent");
        agentId = getIntent().getStringExtra("agentId");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        address = findViewById(R.id.address);
        save = findViewById(R.id.save);
        delete = findViewById(R.id.delete);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else if (phone.getText().length() == 0) {
                    phone.setError("Enter phone");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else if (address.getText().length() == 0) {
                    address.setError("Enter address");
                } else {
                    if (agentId != null) {
                        updateData();
                    } else {
                        saveData();
                    }
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Agents").child(agentId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Removed");
                        finish();
                    }


                });
            }
        });
        if (agentId != null) {
            getDataFromServer();
            delete.setVisibility(View.VISIBLE);
            phone.setFocusable(false);
        }
    }
    private void updateData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name.getText().toString());
        map.put("password", password.getText().toString());
        map.put("address", address.getText().toString());
        mDatabase.child(agentId).updateChildren(map);
    }
    private void getDataFromServer() {
        mDatabase.child("Agents").child(agentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    AgentModel model = dataSnapshot.getValue(AgentModel.class);
                    if (model != null) {
                        name.setText(model.getName());
                        phone.setText(model.getPhone());
                        password.setText(model.getPassword());
                        address.setText(model.getAddress());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveData() {
        mDatabase.child("Agents").child(phone.getText().toString()).setValue(new AgentModel(
                name.getText().toString(),
                phone.getText().toString(),
                password.getText().toString(),
                address.getText().toString(),
                System.currentTimeMillis(),SharedPrefs.getLoggedInAsWhichAdmin()
        )).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.showToast("Saved");
            }
        });
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
