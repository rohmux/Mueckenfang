package de.androidnewcomer.uhrwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Created by uwe on 26.05.15.
 */
public class UhrWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int i=0; i<appWidgetIds.length; i++) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.uhr_widget);
            appWidgetManager.updateAppWidget(appWidgetIds[i],views);
        }
    }
}
