package com.example.emergencyapp;

import android.app.Application;

import com.example.emergencyapp.notifications.NotificationHelper;
import com.google.android.gms.maps.MapsInitializer;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createNotificationChannel(this);
        // Pré-charge le module Google Maps en arrière-plan pour que l'ouverture
        // de l'onglet Carte soit plus rapide et ne bloque pas le thread principal.
        prechargerMaps();
    }

    /** Initialise le SDK Maps de façon asynchrone au démarrage. */
    private void prechargerMaps() {
        new Thread(() -> {
            try {
                MapsInitializer.initialize(getApplicationContext(),
                        MapsInitializer.Renderer.LATEST, null);
            } catch (Exception ignored) {
                // Sans services Google Play ou sans clé, on ignore : la carte gérera l'échec
            }
        }).start();
    }
}
