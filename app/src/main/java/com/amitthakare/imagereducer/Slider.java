package com.amitthakare.imagereducer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.amitthakare.imagereducer.Adapters.SlideViewPagerAdapter;

public class Slider extends AppCompatActivity {

    public static ViewPager viewPager;
    SlideViewPagerAdapter slideViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        viewPager = findViewById(R.id.viewPager);
        slideViewPagerAdapter = new SlideViewPagerAdapter(this);
        viewPager.setAdapter(slideViewPagerAdapter);

        if (openAlready())
        {
            //if already visited then directly goto Login Screen
            Intent intent = new Intent(Slider.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else
        {
            //set the offline data with key and specifier
            SharedPreferences.Editor editor=getSharedPreferences("slide",MODE_PRIVATE).edit();
            //if user has visited first time then store the slide kay value as true.
            editor.putBoolean("slide",true);
            editor.commit();
        }

    }

    private boolean openAlready() {

        //fetch data from file with key "slide" ans return result.
        SharedPreferences sharedPreferences = getSharedPreferences("slide",MODE_PRIVATE);
        boolean result = sharedPreferences.getBoolean("slide",false);
        return result;
    }
}