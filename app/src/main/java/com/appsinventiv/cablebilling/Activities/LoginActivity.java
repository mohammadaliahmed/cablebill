package com.appsinventiv.cablebilling.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.appsinventiv.cablebilling.Activities.Admin.AdminScreen;
import com.appsinventiv.cablebilling.Activities.Agent.AgentScreen;
import com.appsinventiv.cablebilling.Models.AgentModel;
import com.appsinventiv.cablebilling.R;
import com.appsinventiv.cablebilling.Utils.CommonUtils;
import com.appsinventiv.cablebilling.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    Button login;
    EditText password, username;
    RadioButton admin, agent;
    String userType;
    HashMap<String, AgentModel> agentMap = new HashMap<>();
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
        agent = findViewById(R.id.agent);
        admin = findViewById(R.id.admin);


        admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userType = "admin";
                }
            }
        });
        agent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userType = "agent";
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().length() == 0) {
                    username.setError("Enter username");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else if (userType == null) {
                    CommonUtils.showToast("Please select login type");
                } else {
                    loginNow();
                }
            }
        });

        getAllUsersFromDB();

    }

    private void getAllUsersFromDB() {
        mDatabase.child("Agents").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AgentModel model = snapshot.getValue(AgentModel.class);
                        if (model != null) {
                            agentMap.put(snapshot.getKey(), model);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loginNow() {
        if (userType.equalsIgnoreCase("admin")) {
            if (username.getText().toString().equalsIgnoreCase("adminadmin") && password.getText().toString().equals("admin123")) {
                SharedPrefs.setLoggedInAs("admin");
                SharedPrefs.setLoggedInAsWhichAdmin("naeem");
                startActivity(new Intent(LoginActivity.this, AdminScreen.class));
                finish();
            } else if (username.getText().toString().equalsIgnoreCase("bilal") && password.getText().toString().equals("admin333")) {
                SharedPrefs.setLoggedInAs("admin");
                SharedPrefs.setLoggedInAsWhichAdmin("bilal");
                startActivity(new Intent(LoginActivity.this, AdminScreen.class));
                finish();
            } else {
                CommonUtils.showToast("Wrong username and password");
            }

        } else {
            if (agentMap.containsKey(username.getText().toString())) {
                if (agentMap.get(username.getText().toString()).getPassword().equals(password.getText().toString())) {
                    SharedPrefs.setLoggedInAs("agent");
                    SharedPrefs.setParchiName(agentMap.get(username.getText().toString()).getName());
                    SharedPrefs.setAgent(agentMap.get(username.getText().toString()));
                    startActivity(new Intent(LoginActivity.this, AgentScreen.class));
                    finish();
                } else {
                    CommonUtils.showToast("Wrong password");
                }


            } else {
                CommonUtils.showToast("User does not exist");
            }
        }
    }


}
