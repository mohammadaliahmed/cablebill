package com.appsinventiv.cablebilling.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.appsinventiv.cablebilling.Activities.Admin.AdminScreen;
import com.appsinventiv.cablebilling.Activities.Agent.AgentScreen;
import com.appsinventiv.cablebilling.R;
import com.appsinventiv.cablebilling.Utils.SharedPrefs;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    public static int SPLASH_TIME_OUT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (SharedPrefs.getLoggedInAs().equalsIgnoreCase("admin")) {
                    Intent i = new Intent(Splash.this, AdminScreen.class);
                    startActivity(i);
                } else if (SharedPrefs.getLoggedInAs().equalsIgnoreCase("agent")) {
                    Intent i = new Intent(Splash.this, AgentScreen.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(Splash.this, LoginActivity.class);
                    startActivity(i);
                }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
//          <!--alias:fixitclient
//        pass:fixitclient-->
    }
}
