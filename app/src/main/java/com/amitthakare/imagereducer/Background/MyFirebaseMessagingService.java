package com.amitthakare.imagereducer.Background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.amitthakare.imagereducer.MainActivity;
import com.amitthakare.imagereducer.R;
import com.amitthakare.imagereducer.ScanConstants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    //https://www.youtube.com/watch?v=2WtPF4qwPGU - Notification Link

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        //upload token to server
        //have to store into preferences for further use
        Log.e("Token",token);
    }

    //this will handle the notification when app is running
    // because when the application is running the sdk handle it by own and we need to handle this messages.
    //it contain notification payload as well as data payload
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("Notification",remoteMessage.getFrom());

        //this will create the channel to show the msg if user is active
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChanner();
        }


        if (remoteMessage.getNotification() != null)
        {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.e("Notification Service","Title : "+title + "  Body : "+body);
            showNotification(title,body);
        }

        //this will be handle if data notification with key is send to user
        if (remoteMessage.getData().size() > 0 )
        {
            for (Map.Entry<String,String> entry : remoteMessage.getData().entrySet())
            {

                if (entry.getKey().equals("playstore"))
                {
                   /* Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(entry.getValue()));
                    startActivity(intent);*/

                    /*final String appPackageName = entry.getValue();
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + appPackageName));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } catch (android.content.ActivityNotFoundException anfe) {
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                    }*/

                    ScanConstants.datakey=entry.getKey();
                    ScanConstants.datavalue= entry.getValue();

                    Log.e("Handle Data Service","Playstore : "+entry.getValue());
                }
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChanner()
    {
        NotificationChannel notificationChannel = new NotificationChannel("channel_id","Test_Notification",NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription("My Notification");

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    //to show the notifications
    private void showNotification(String title,String body)
    {

        Intent intent = new Intent(this, MainActivity.class);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(getApplicationContext(),"channel_id")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.image_reducer_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.image_reducer_icon))
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT));
        notificationManager.notify(1,builder.build());

    }




}

