package com.amitthakare.imagereducer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {

    private static int splash_time = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scree);
        handleNotificationData();

        //For Not to have Status Bar
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashScreen.this, Slider.class);
                startActivity(homeIntent);
                finish();
            }
        }, 3000);

        // For Splash Time and After that to move main activity
        

    }

    private void handleNotificationData()
    {
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        {
            if (bundle.containsKey("data1"))
            {
                Log.e("Handle Data","Data 1 : "+bundle.getString("data1"));

            }
            if (bundle.containsKey("data2"))
            {
                Log.e("Handle Data","Data 2 : "+bundle.getString("data2"));
            }
            if (bundle.containsKey("playstore"))
            {
                //here we just attaching the values and then we handle it in main activity
                //this will be handle if user has closed our app
                ScanConstants.datakey = "playstore";
                ScanConstants.datavalue = bundle.getString("playstore");
                ScanConstants.isClicked="Yes";

                Log.e("Handle Data","Playstore : "+bundle.getString("playstore"));
            }
        }
    }
}