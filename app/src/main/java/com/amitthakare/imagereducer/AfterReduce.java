package com.amitthakare.imagereducer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;

public class AfterReduce extends AppCompatActivity {

    AdView bottomAd;
    ImageView imageView;
    InterstitialAd interstitialAd;
    Button finishedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_reduce);

        imageView = findViewById(R.id.imageViewAfterReduce);
        finishedBtn = findViewById(R.id.finishBtn);



        //File image = new File(ScanConstants.ReducedFilePath);
       // BitmapFactory.Options bmOptions = new BitmapFactory.Options();
       // Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);

        //imageView.setImage(ImageSource.bitmap(bitmap));
        imageView.setImageURI(Uri.parse(ScanConstants.ReducedFilePath));

        adsIni();

        finishedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AfterReduce.this, "Saved To Location!", Toast.LENGTH_SHORT).show();
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });
    }

    private void adsIni() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });


        bottomAd = findViewById(R.id.afterReducedBottomAds);

        AdRequest adRequest1 = new AdRequest.Builder().build();
        bottomAd.loadAd(adRequest1);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-4970909843127323/8976400418");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                finish();
            }
        });




    }

}