package com.amitthakare.imagereducer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.amitthakare.imagereducer.MainActivity;
import com.amitthakare.imagereducer.PrivacyPolicy;
import com.amitthakare.imagereducer.R;


public class SlideViewPagerAdapter extends PagerAdapter {

    Context context;

    public SlideViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 1; // we want three slides
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pager, container, false);
        // now by this view we can access every item that we have created on pager1.xml

        ImageView main_img = view.findViewById(R.id.pager1img);
       // final ImageView ind1 = view.findViewById(R.id.ind1);
       // ImageView ind2 = view.findViewById(R.id.ind2);
       // ImageView ind3 = view.findViewById(R.id.ind3);
        final CheckBox isAgreePrivacy = view.findViewById(R.id.isAgreePrivacyPolicy);
        TextView iAgreeTextView = view.findViewById(R.id.iAgreeTextView);
        TextView main_text = view.findViewById(R.id.pager1text);
        Button btnGetStarted = view.findViewById(R.id.pager1buton);

        iAgreeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PrivacyPolicy.class);
                context.startActivity(intent);
            }
        });

        //when button Get Started hits then goti Login Activity
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAgreePrivacy.isChecked())
                {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else
                {
                    Toast.makeText(context, "Please accept the Privacy Policy!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // to get the screen number and provide data.
        switch (position) {

            case 0:
                main_img.setImageResource(R.drawable.image_reducer_viewpager);
              //  ind1.setImageResource(R.drawable.unselected);
               // ind2.setImageResource(R.drawable.unselected);
                //ind3.setImageResource(R.drawable.selected);
                main_text.setText("Now reduce your images at your required sizes with in few seconds.");
                break;

        }


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
