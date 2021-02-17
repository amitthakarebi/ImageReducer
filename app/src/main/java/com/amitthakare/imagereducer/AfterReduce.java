package com.amitthakare.imagereducer;

import androidx.annotation.NonNull;
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

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;

public class AfterReduce extends AppCompatActivity {

    AdView bottomAd;
    ImageView imageView;
    InterstitialAd mInterstitialAd;
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
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(AfterReduce.this);
                } else {
                    Log.e("TAG", "The interstitial ad wasn't ready yet.");
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

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-4970909843127323/8976400418", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i("Interstitial", "onAdLoaded");

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.e("TAG", "The ad was dismissed.");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.e("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.e("TAG", "The ad was shown.");
                    }
                });

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i("Interstitial", loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });


    }

}