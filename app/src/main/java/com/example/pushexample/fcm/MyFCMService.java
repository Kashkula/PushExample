package com.example.pushexample.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.pushexample.MainActivity;
import com.example.pushexample.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFCMService extends FirebaseMessagingService {
    private String title, message, imageUrl, type;
    NotificationManager notificationManager;

    private int notificationId;
    String channelId = "Default";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        createNotificationChannel();

        onPostExecute(remoteMessage);
    }

    private void onPostExecute(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        title = data.get("title");

        if (remoteMessage.getData().containsKey("content") && remoteMessage.getData().get("content") != null)
            message = data.get("massage");


        if (remoteMessage.getData().containsKey("id") && remoteMessage.getData().get("id") != null)
            notificationId = Integer.parseInt(Objects.requireNonNull(data.get("id")));


        Intent intent = new Intent(this, MainActivity.class);
        openNotification(intent);
    }

    private void openNotification(Intent intent) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder;
        builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText(message)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();

        notification.tickerText = title;

        notificationManager.notify(notificationId, notification);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
