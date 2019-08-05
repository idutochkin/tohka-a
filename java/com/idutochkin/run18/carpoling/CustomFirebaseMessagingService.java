package com.idutochkin.run18.carpoling;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    String user_id;
    String user_name;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        sendNotification(remoteMessage.getNotification().getBody(),
                remoteMessage.getNotification().getTitle(),
                remoteMessage.getData().toString());

    }
    private void sendNotification(String messageBody, String messageTitle, String messageData) {
        try {
            Log.d("LOG1", "DATA-body: "+messageBody);
            Log.d("LOG1", "DATA-title: "+messageTitle);
            //JSONObject resObj = new JSONObject(messageData);
            Log.d("LOG1", "DATA-meta: "+messageData.toString());
        } catch (Exception e) {
            Log.d("LOG", "Connect: "+e.toString());
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("id", user_id);
        intent.putExtra("name", user_name);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //if(MainActivity.isActivityVisible()) {
            startActivity(intent);
        //}

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder.setContentIntent(contentIntent);
        notificationManager.notify(0, notificationBuilder.build());
    }
}