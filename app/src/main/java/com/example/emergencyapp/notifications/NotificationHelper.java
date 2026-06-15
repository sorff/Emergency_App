package com.example.emergencyapp.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.emergencyapp.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "emergency_channel";
    private static final String CHANNEL_NAME = "Alertes d'urgence";
    private static int notifId = 1000;

    /** Crée le canal de notification (requis Android 8+). */
    public static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal pour les alertes d'urgence");
            NotificationManager manager = ctx.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /** Envoie une notification locale avec le titre et le message fournis. */
    public static void sendNotification(Context ctx, String titre, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(Color.parseColor("#C0392B"))
                .setContentTitle(titre)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notifId++, builder.build());
        }
    }
}
