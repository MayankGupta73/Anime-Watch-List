package com.example.mayankgupta.animewatchlist;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    public ReminderBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String animeName = intent.getStringExtra("Anime Name");

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setColor(Color.BLUE)
                .setContentTitle("Anime Reminder")
                .setContentText(animeName+" is about to start!")
                .setSmallIcon(R.mipmap.ic_launcher2);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(123,notifBuilder.build());
    }
}
