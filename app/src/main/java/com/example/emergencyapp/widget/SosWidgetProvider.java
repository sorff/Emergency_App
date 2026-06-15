package com.example.emergencyapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.emergencyapp.R;
import com.example.emergencyapp.activities.SplashActivity;

/**
 * Widget d'écran d'accueil : un bouton SOS qui ouvre l'application.
 * (Le geste "maintenir 3s" n'est pas possible sur un widget launcher ;
 * le tap ouvre directement l'écran SOS de l'app.)
 */
public class SosWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            majWidget(context, appWidgetManager, id);
        }
    }

    /** Construit le widget et branche le clic vers l'app. */
    private void majWidget(Context context, AppWidgetManager manager, int widgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_sos);

        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        views.setOnClickPendingIntent(R.id.widgetSosButton, pending);
        manager.updateAppWidget(widgetId, views);
    }
}
