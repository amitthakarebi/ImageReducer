package com.amitthakare.imagereducer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
//https://youtu.be/82uwhCj3zuI // firebase notification background
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button compressImageBtn, applySettingBtn, selectImgBtn;
    ImageView imageViewReduce;
    TextView finalSizeText, percentCompressQuality, originalSizeText, percentCompressSize;
    SeekBar compressQualitySeekBar, compressSizeSeekBar;
    Uri selectedImgUri, originalBitmapUri;
    Bitmap tempBitmap, tempBitmap1, forSizeBitmap, originalBitmap, tempBitmap3 = null;
    String originalFilePath, tempFileName;
    EditText pxHeight, pxWidth;
    SwitchCompat maintainResolution;
    ImageView reduceToolbarIcon;
    File file, OriginalFile;
    int compressQuality = 0, compressSize = 0;
    boolean isMaintainResolution, isQualityChanged = false, isSizeChanged = false, isSettingApplied = false,checkIfSizeApplied=false;
    LinearLayout settingLayout, pxHeightLayout, pxWidthLayout, compressSizeLayout,imageViewLayout;

    //navigation
    DrawerLayout drawerLayoutMain;
    NavigationView navigationViewMain;
    Toolbar toolbarMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this will be called and affective when the notification send with the keys with it means payload or data notifications.
        handleNotificationData();

        givePermissionWrite();

        File file = new File(ScanConstants.REDUCE_IMG_PATH);
        file.mkdirs();

        initialize();
        setNavigation();

        selectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 2);
            }
        });

        compressQualitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percentCompressQuality.setText(progress + "%");
                compressQuality = 100 - (50 + progress);
                Log.e("Quality Progress ", "" + compressQuality);
                isQualityChanged = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isQualityChanged = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        compressSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percentCompressSize.setText(progress + "%");
                compressSize = progress;
                isSizeChanged = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSizeChanged = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        maintainResolution.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pxWidthLayout.setVisibility(View.GONE);
                    pxHeightLayout.setVisibility(View.GONE);
                    isMaintainResolution = true;
                    compressSizeLayout.setVisibility(View.VISIBLE);
                } else {
                    pxHeightLayout.setVisibility(View.VISIBLE);
                    pxWidthLayout.setVisibility(View.VISIBLE);
                    isMaintainResolution = false;
                    compressSizeLayout.setVisibility(View.GONE);
                }
            }
        });

        compressImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSettingApplied) {
                    try {
                        saveBitmapToLocation();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please Apply Setting!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        applySettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    applyImageSetting(compressQuality, compressSize, isMaintainResolution, pxHeight, pxWidth);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setNavigation() {
        // Navigation Hooks
        drawerLayoutMain = findViewById(R.id.drawer_layout);
        navigationViewMain = findViewById(R.id.main_navigationView);
        toolbarMain = findViewById(R.id.reducerToolbar);

        //Tool Bar Work
        setSupportActionBar(toolbarMain);

        // Navigation Menu too and fro when button is click
        navigationViewMain.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayoutMain, toolbarMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayoutMain.addDrawerListener(toggle);
        toggle.syncState();

        toolbarMain.setNavigationIcon(null);

        // To set Icon on Navigation
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.main_camera_icon);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        navigationViewMain.setNavigationItemSelectedListener(this);
        navigationViewMain.setCheckedItem(R.id.menu_home);
    }

    private void saveBitmapToLocation() throws IOException {

        /*Bitmap saveBitmap = null;
        OutputStream fOut = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        file = new File(ScanConstants.REDUCE_IMG_PATH, timeStamp + ".jpg");
        fOut = new FileOutputStream(file);
        saveBitmap = tempBitmap3;
        saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        fOut.flush();
        fOut.close();*/

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File fileNew = new File(ScanConstants.REDUCE_IMG_PATH, timeStamp + ".jpg");

        file.renameTo(fileNew);


        File file1 = new File(ScanConstants.REDUCE_IMG_PATH + File.separator + "Temp");

        if (file1.isDirectory())
            for (File child : file1.listFiles())
                child.delete();
        file1.delete();

        MediaScannerConnection.scanFile(this,new String[]{fileNew.getPath()}, new String[]{"image/jpeg"},null);

        ScanConstants.ReducedFilePath = fileNew.getPath();

        Intent intent = new Intent(MainActivity.this, AfterReduce.class);
        startActivity(intent);

    }

    private void givePermissionWrite() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    private void applyImageSetting(int compressQuality, int compressSize, boolean isMaintainResolution, EditText pxHeight, EditText pxWidth) throws IOException {
        isSettingApplied = true;
        Log.e("Quality Progress ", compressQuality + "");
        Log.e("Size Progress ", compressSize + "");

        File path = new File(ScanConstants.REDUCE_IMG_PATH + File.separator + "Temp");
        path.mkdirs();

        tempBitmap = null;
        tempBitmap1 = null;
        //tempBitmap = applyQualitySetting(compressQuality);
        //tempBitmap1 = applySizeSetting(isMaintainResolution, pxHeight, pxWidth, compressSize, tempBitmap);

        tempBitmap = getBitmapByUri(originalBitmapUri);

        applySizeSetting(isMaintainResolution, pxHeight, pxWidth, compressSize, tempBitmap);
        if (checkIfSizeApplied)
        {
            tempBitmap3 = null;
            tempBitmap3 = applyQualitySetting(compressQuality);

            setFinalSize(tempBitmap3);

            imageViewReduce.setImageBitmap(tempBitmap3);
        }


    }

    private void setFinalSize(Bitmap tempBitmap3) throws IOException {
        OutputStream fOut = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        tempFileName = timeStamp;
        file = new File(ScanConstants.REDUCE_IMG_PATH + File.separator + "Temp", timeStamp + ".jpg");
        fOut = new FileOutputStream(file);
        forSizeBitmap = tempBitmap3;
        forSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
        fOut.flush();
        fOut.close();
        //MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

        float bytesize = file.length();
        float kbsize = bytesize / 1024;
        finalSizeText.setText("Final Image Size : " + new DecimalFormat("##.##").format(kbsize) + " Kb");

    }

    private void applySizeSetting(boolean isMaintainResolution, EditText pxHeight, EditText pxWidth, int compressSize, Bitmap tempBitmap) {

        if (isMaintainResolution) {

            float newHeight;
            float newWidth;
            //Size by % Resolution
            //finding the perfect size of bitmap according to percent
            if (compressSize == 0) {
                newHeight = tempBitmap.getHeight();
                newWidth = tempBitmap.getWidth();
            } else {
                float newWidthPercent = (float) tempBitmap.getWidth() * ((float) compressSize / 100);
                float newHeightPercent = (float) tempBitmap.getHeight() * ((float) compressSize / 100);
                newHeight = tempBitmap.getHeight() - newHeightPercent;
                newWidth = tempBitmap.getWidth() - newWidthPercent;
            }

            if (newHeight == 0 && newWidth == 0) {
                newHeight = 1;
                newWidth = 1;
            }
            //create a bitmap and return
            tempBitmap1 = Bitmap.createScaledBitmap(tempBitmap, (int) newWidth, (int) newHeight, true);
            checkIfSizeApplied=true;
        } else {
            if (TextUtils.isEmpty(pxHeight.getText()) && TextUtils.isEmpty(pxWidth.getText()))
            {
                Toast.makeText(this, "Insert Height and Width !", Toast.LENGTH_SHORT).show();
                checkIfSizeApplied=false;
            }else
            {
                //get the width and height and create a bitmap then return
                tempBitmap1 = Bitmap.createScaledBitmap(tempBitmap, Integer.parseInt(pxWidth.getText().toString()), Integer.parseInt(pxHeight.getText().toString()), true);
                checkIfSizeApplied=true;
            }
        }

    }

    private Bitmap applyQualitySetting(int compressQuality) {
        //Bitmap bitmap = getBitmapByUri(originalBitmapUri);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tempBitmap1.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream);
        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(outputStream.toByteArray()));
        return decoded;

    }

    private void initialize() {
        reduceToolbarIcon = findViewById(R.id.reduceToolbarIcon);
        selectImgBtn = findViewById(R.id.selectImageBtn);
        imageViewReduce = findViewById(R.id.reduceImageView);
        settingLayout = findViewById(R.id.settingLayout);
        compressImageBtn = findViewById(R.id.compressImageBtn);
        pxHeight = findViewById(R.id.pxHeight);
        pxWidth = findViewById(R.id.pxWidth);
        originalSizeText = findViewById(R.id.originalSizeTextView);
        percentCompressQuality = findViewById(R.id.percentCompressText);
        compressQualitySeekBar = findViewById(R.id.seekBar);
        maintainResolution = findViewById(R.id.resolutionMaintainSwitch);
        maintainResolution.setChecked(true);
        isMaintainResolution = true;
        pxHeightLayout = findViewById(R.id.pxHeightLayout);
        pxWidthLayout = findViewById(R.id.pxWidthLayout);
        compressSizeSeekBar = findViewById(R.id.compressSizeSeekBar);
        percentCompressSize = findViewById(R.id.compressSizeSeekBarTextPercentage);
        compressSizeLayout = findViewById(R.id.compressSizeLayout);
        applySettingBtn = findViewById(R.id.applySettingBtn);
        finalSizeText = findViewById(R.id.finalSizeTextView);
        imageViewLayout = findViewById(R.id.linearLayout);

        compressQualitySeekBar.setMax(50);
        compressQualitySeekBar.setProgress(0);
        compressQuality = 50;
        percentCompressQuality.setText("0%");

        compressSizeSeekBar.setMax(100);
        compressSizeSeekBar.setProgress(10);
        compressSize = 10;
        percentCompressSize.setText(compressSize + "%");


        pxWidthLayout.setVisibility(View.GONE);
        pxHeightLayout.setVisibility(View.GONE);
        compressSizeLayout.setVisibility(View.VISIBLE);
        imageViewLayout.setVisibility(View.GONE);

        reduceToolbarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayoutMain.openDrawer(navigationViewMain);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            //store image uri to variable
            selectedImgUri = data.getData();
            originalBitmapUri = selectedImgUri;

            //stored bitmap from uri
            originalBitmap = getBitmapByUri(selectedImgUri);
            Log.e("inside on activity", ":");


            //setting up the bitmap to imageview
            imageViewReduce.setImageBitmap(originalBitmap);

            //show and hide options
            settingLayout.setVisibility(View.VISIBLE);
            imageViewLayout.setVisibility(View.VISIBLE);
            selectImgBtn.setVisibility(View.GONE);

            compressSizeSeekBar.setProgress(10);
            compressSize = 10;
            compressQualitySeekBar.setProgress(0);
            compressQuality = 50;

            finalSizeText.setText("Final Image Size :");

            //assign width and height to the edittext
            pxHeight.setText(originalBitmap.getHeight() + "");
            pxWidth.setText(originalBitmap.getWidth() + "");


            // find image actual size and location in terms of file
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImgUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            originalFilePath = cursor.getString(columnIndex);
            cursor.close();
            OriginalFile = new File(originalFilePath);
            float bytesize = OriginalFile.length();
            float kbSize = bytesize / 1024;

            originalSizeText.setText("Original Image Size :" + new DecimalFormat("##.##").format(kbSize) + " Kb");

            //Toast.makeText(this, originalFilePath, Toast.LENGTH_SHORT).show();


        }

    }

    private Bitmap getBitmapByUri(Uri uri) {

        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= 29) {
            ImageDecoder.Source source = ImageDecoder.createSource(getApplicationContext().getContentResolver(), uri);
            try {
                bitmap = ImageDecoder.decodeBitmap(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_home:
                break;
            case R.id.menu_privacy_policy:
                navigationViewMain.setCheckedItem(R.id.menu_home);
                move(MainActivity.this, PrivacyPolicy.class);
                break;
            case R.id.menu_website:
                navigationViewMain.setCheckedItem(R.id.menu_home);
                String url = "https://www.universaltalks.in/";
                Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                if (i.resolveActivity(getPackageManager()) == null) {
                    i.setData(Uri.parse(url));
                }
                startActivity(i);
                break;
        }
        drawerLayoutMain.closeDrawer(GravityCompat.START);
        return true;
    }

    public void move(Context context, Class classs) {
        Intent intent = new Intent(context, classs);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        int getResult = settingLayout.getVisibility();

        if (getResult==0)
        {
            settingLayout.setVisibility(View.GONE);
            imageViewLayout.setVisibility(View.GONE);
            selectImgBtn.setVisibility(View.VISIBLE);
        }else
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
            // Set the message show for the Alert time
            builder.setMessage("Thank you for using our app.\nClick Yes to exit!");

            // Set Alert Title
            builder.setTitle("Do you want to close app?");
            builder.setIcon(R.drawable.image_reducer_icon);

            // Set Cancelable false
            // for when the user clicks on the outside
            // the Dialog Box then it will remain show
            builder.setCancelable(false);

            // Set the positive button with yes name
            // OnClickListener method is use of
            // DialogInterface interface.

            builder
                    .setPositiveButton(
                            "Yes",
                            new DialogInterface
                                    .OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    // When the user click yes button
                                    finishAffinity();
                                    //finish();

                                    //System.exit(0);
                                }
                            });

            // Set the Negative button with No name
            // OnClickListener method is use
            // of DialogInterface interface.
            builder
                    .setNegativeButton(
                            "No",
                            new DialogInterface
                                    .OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    // If user click no
                                    // then dialog box is canceled.
                                    dialog.cancel();
                                }
                            });

            // Create the Alert dialog
            android.app.AlertDialog alertDialog = builder.create();

            // Show the Alert Dialog box
            alertDialog.show();
        }
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
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("New Intent","New Intent Called");
    }
}